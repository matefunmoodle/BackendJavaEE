/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.proygrado.ejb;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import edu.proygrado.dto.ArchivoDTO;
import edu.proygrado.dto.ArchivoRestriccion;
import edu.proygrado.dto.CompartirArchivoInputDTO;
import edu.proygrado.dto.EvaluacionDTO;
import edu.proygrado.dto.TipoArchivo;
import edu.proygrado.ejb.fileSharing.WSUserSharedFilesMgr;
import edu.proygrado.matefun.MatefunException;
import edu.proygrado.modelo.Alumno;
import edu.proygrado.modelo.Archivo;
import edu.proygrado.modelo.Docente;
import edu.proygrado.modelo.EstadoArchivo;
import edu.proygrado.modelo.Evaluacion;
import edu.proygrado.modelo.Usuario;
import edu.proygrado.utils.MoodleFiles;
import edu.proygrado.utils.MoodleFunctions;
import edu.proygrado.utils.MoodleWS;
import edu.proygrado.utils.StringPair;
import edu.proygrado.utils.Utils;

@Stateless
public class ArchivosEJB {

	@PersistenceContext(unitName = "matefunDS")
	private EntityManager em;

	public void persist(Object object) {
		em.persist(object);
	}

	public List<ArchivoDTO> getArchivosUsuario(String cedula) throws Exception {
		List<Archivo> archivos;

		Usuario user = em.find(Usuario.class, cedula);
		if (user == null) {
			throw new Exception("No existe el usuario con cedula " + cedula);
		}
		if (user instanceof Alumno) {
			archivos = em.createQuery(
							"select ar from Alumno al join al.archivos ar where LOWER(al.cedula)=LOWER(:cedula) and ar.eliminado=:statusEliminado")
					.setParameter("cedula", cedula).setParameter("statusEliminado", false).getResultList();
		} else {
			archivos = em.createQuery(
							"select ar from Docente d join d.archivos ar where LOWER(d.cedula)=LOWER(:cedula) and ar.eliminado=:statusEliminado")
					.setParameter("cedula", cedula).
					setParameter("statusEliminado", false).getResultList();
		}
		List<ArchivoDTO> archivosDTO = new ArrayList<>();
		archivos.stream().forEach((archivo) -> {
			archivosDTO.add(new ArchivoDTO(archivo));
		});

		return archivosDTO;
	}

	public List<ArchivoDTO> getArchivosCompartidosAlumno(String cedula) throws Exception {
		List<Archivo> archivos = em
				.createQuery("select ar from Alumno al join al.archivosCompartidos ar where al.cedula=:cedula")
				.setParameter("cedula", cedula).getResultList();

		List<Archivo> archivosGrupo = em
				.createQuery(
						"select archivos from Grupo g join g.archivos archivos join g.alumnos alumnos where alumnos.cedula =:cedula")
				.setParameter("cedula", cedula).getResultList();

		List<ArchivoDTO> archivosDTO = new ArrayList<>();
		archivos.stream().forEach((archivo) -> {
			archivosDTO.add(new ArchivoDTO(archivo));
		});
		archivosGrupo.stream().forEach((archivo) -> {
			archivosDTO.add(new ArchivoDTO(archivo));
		});
		return archivosDTO;
	}

	
	public Integer createMoodleDraftFile(ArchivoDTO archivo, String matefunToken, InvitadoEJB invitadoEJB, boolean saveWithWebServicesUser, String filepath) throws Exception {
		
		List<StringPair> allTokens = invitadoEJB.getAllMoodleTokens(matefunToken);
		
		String url = invitadoEJB.getMoodleApiEndpoint(matefunToken) + "/webservice/upload.php";
		String fileName = archivo.getNombre();
		String token = saveWithWebServicesUser ? allTokens.get(allTokens.size()-1).getValue() : allTokens.get(0).getValue();
		int _itemid = (new Random()).nextInt(99999999); 
		String fileContent = archivo.getContenido();
				//_fileContent!=null && _fileContent.length > 0 && _fileContent[0]!=null ? _fileContent[0] :
				//Utils.toBase64(archivo.getContenido());
		
		System.out.println("filepath: " + filepath);
		System.out.println("nombre: " + fileName);
		System.out.println("token: " + token);
		System.out.println("itemid: " + (new Integer(_itemid)).toString());
		System.out.println("_itemid: " + _itemid);
		System.out.println("fileContent: " + fileContent);
		System.out.println("url: " + url);
		
		String boundary = Long.toHexString(System.currentTimeMillis());

		URLConnection connection = new URL(url).openConnection();
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

		try (
		    OutputStream output = connection.getOutputStream();
		    PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true);
		) {
		    // envia token
		    writer.append("--" + boundary).append("\r\n");
		    writer.append("Content-Disposition: form-data; name=\"token\"").append("\r\n");
		    writer.append("Content-Type: text/plain; charset=UTF-8").append("\r\n");
		    writer.append("\r\n").append(token).append("\r\n").flush();
		    
		    // envia itemid
		    writer.append("--" + boundary).append("\r\n");
		    writer.append("Content-Disposition: form-data; name=\"itemid\"").append("\r\n");
		    writer.append("Content-Type: text/plain; charset=UTF-8").append("\r\n");
		    writer.append("\r\n").append((new Integer(_itemid)).toString()).append("\r\n").flush();
		    
		    // envia filepath
		    writer.append("--" + boundary).append("\r\n");
		    writer.append("Content-Disposition: form-data; name=\"filepath\"").append("\r\n");
		    writer.append("Content-Type: text/plain; charset=UTF-8").append("\r\n");
		    writer.append("\r\n").append(filepath).append("\r\n").flush();

		    // envia archivo.
		    writer.append("--" + boundary).append("\r\n");
		    writer.append("Content-Disposition: form-data; name=\"file_1\"; filename=\"" + fileName + "\"").append("\r\n");
		    writer.append("Content-Type: text/plain; charset=UTF-8").append("\r\n");
		    writer.append("\r\n").flush();
		    output.write(fileContent.getBytes(Charset.forName("UTF-8")));
		    output.flush();
		    writer.append("\r\n").flush();

		    writer.append("--" + boundary + "--").append("\r\n").flush();
		}

		HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
		int responseCode = httpURLConnection.getResponseCode();
		if (responseCode==200)
			return _itemid;
		else
			throw new MatefunException("No se puede crear archivo draft. ("+responseCode+")");
	}
	
	@SuppressWarnings("serial")
	public ArchivoDTO crearArchivoPrivadoMoodle(ArchivoDTO archivo, String matefunToken, InvitadoEJB invitadoEJB, String filepath, boolean saveWithWebServicesUser) throws Exception {
		//TODO: solo va a crear en el root de private files, cambiar esto para que pueda crear en todo el arbol

		int itemid = createMoodleDraftFile(archivo,  matefunToken,  invitadoEJB,  saveWithWebServicesUser, filepath);
		
		System.out.println("Retorno itmeid: " + itemid );
		
		JsonObject result = MoodleWS.POST(	invitadoEJB,
											matefunToken,
											MoodleFunctions.core_user_add_user_private_files,
											new HashMap<String, Object>() {{ put("draftid", itemid ); }},
											saveWithWebServicesUser);
		
		if (!result.getBoolean(MoodleWS.IS_OK))
			throw new MatefunException("error on core_user_add_user_private_files: " + result.get("result").toString());
		
		return new ArchivoDTO(
				Utils.getIdByFilepath(filepath + archivo.getNombre(), invitadoEJB.getMoodleUserId(matefunToken), saveWithWebServicesUser),
				archivo.getNombre(),
				new Date(),
				Utils.toBase64(archivo.getContenido()),
				invitadoEJB.getUsuario(matefunToken).getCedula(),
				true,
				false,
				MoodleFiles.rootFileId,
				-1l,
				false,
				EstadoArchivo.Edicion.toString(),
				filepath + archivo.getNombre(),
				new EvaluacionDTO(),
				saveWithWebServicesUser ? TipoArchivo.COMPARTIDO : TipoArchivo.PRIVADO,
				!saveWithWebServicesUser, //TODO: puedeCompartir no debe depender de si el archivo es compartido o no, debe haber checkbox
				filepath);
	}
	
	
	public ArchivoDTO crearArchivo(ArchivoDTO archivoDTO) throws Exception {
		boolean existeArchivo = 0 < em
				.createQuery(
						"select count(a) from Archivo a where lower(a.nombre)=lower(:nombre) and a.creador.cedula=:cedula and a.padre.id =:padreId and a.eliminado=0",
						Long.class)
				.setParameter("nombre", archivoDTO.getNombre()).setParameter("padreId", archivoDTO.getPadreId())
				.setParameter("cedula", archivoDTO.getCedulaCreador()).getSingleResult();
		if (!existeArchivo) {
			Usuario creador = em.find(Usuario.class, archivoDTO.getCedulaCreador());

			if (creador == null) {
				throw new Exception("No existe el usuario de cedula " + archivoDTO.getCedulaCreador());
			}
			Archivo padre = em.find(Archivo.class, archivoDTO.getPadreId());
			Archivo arch = new Archivo(archivoDTO.getNombre(), new Date(), archivoDTO.getContenido(),
					EstadoArchivo.Edicion, archivoDTO.isEditable(), archivoDTO.isDirectorio(), padre, creador);
			arch.setEliminado(archivoDTO.isEliminado());
			if (creador instanceof Docente) {
				((Docente) creador).addArchivo(arch);
			} else if (creador instanceof Alumno) {
				((Alumno) creador).addArchivo(arch);
			}
			em.persist(arch);
			em.flush();
			return new ArchivoDTO(arch);
		} else {
			throw new Exception("Ya existe un archivo de nombre" + archivoDTO.getNombre());
		}
	}

	public ArchivoDTO getCopiaCompartido(String cedula, Long archivoId) throws Exception {
		Archivo archivo = em.find(Archivo.class, archivoId);
		try {
			Archivo copiaExistente = (Archivo) em
					.createQuery(
							"select ar from Alumno a join a.archivosCompartidos ar where a.cedula =:cedula and ar.archivoOrigen.id =:archivoId ")
					.setParameter("cedula", cedula).setParameter("archivoId", archivoId).getSingleResult();
			return new ArchivoDTO(copiaExistente);
		} catch (NoResultException nr) {
			// no existe la copia. No se hace nada con esta excepcion.
		}
		if (archivo == null) {
			throw new Exception("No exite el archivo de id " + archivoId);
		}

		Alumno alumno = em.find(Alumno.class, cedula);
		if (alumno == null) {
			throw new Exception("No existe el alumno de cedula " + cedula);
		}
		Archivo root = null;
		for (Archivo a : alumno.getArchivos()) {
			if (a.getPadre() == null) {
				root = a;
			}
		}
		Archivo copia = new Archivo(archivo.getNombre(), new Date(), archivo.getContenido(), EstadoArchivo.Edicion,
				true, false, root, archivo.getCreador());
		copia.setArchivoOrigen(archivo);
		alumno.addArchivoCompartido(copia);
		em.persist(copia);
		em.flush();
		return new ArchivoDTO(copia);
	}

	public ArchivoDTO editarArchivo(long archivoId, ArchivoDTO archivoDTO) throws Exception {
		Archivo archivo = em.find(Archivo.class, archivoId);
		if (archivo != null) {
			archivo.setContenido(archivoDTO.getContenido());
			archivo.setNombre(archivoDTO.getNombre());
			archivo.setEditable(archivoDTO.isEditable());
			archivo.setEliminado(archivoDTO.isEliminado());
			Archivo padre = em.find(Archivo.class, archivoDTO.getPadreId());
			archivo.setPadre(padre);
			archivo.setEstado(EstadoArchivo.valueOf(archivoDTO.getEstado()));
			return new ArchivoDTO(archivo);
		} else {
			throw new Exception("No se encuentra el archivo con id: " + archivoId);
		}
	}

	public ArchivoDTO getArchivo(long archivoId) throws Exception {
		Archivo archivo = em.find(Archivo.class, archivoId);
		if (archivo != null) {
			return new ArchivoDTO(archivo);
		} else {
			throw new Exception("No se encuentra el archivo con id: " + archivoId);
		}
	}

	public void eliminarArchivo(long archivoId) throws Exception {
		Archivo archivo = em.find(Archivo.class, archivoId);
		if (archivo != null) {
			em.remove(archivo);
		}
	}

	public EvaluacionDTO evaluarArchivo(Long archivoId, EvaluacionDTO evaluacion) throws Exception {
		Archivo archivo = em.find(Archivo.class, archivoId);
		if (archivo == null) {
			throw new Exception("No se encuentra el archivo con id: " + archivoId);
		}
		Docente docente = em.find(Docente.class, evaluacion.getCedulaDocente());
		if (docente == null) {
			throw new Exception("No se encuentra el docente de cedula: " + evaluacion.getCedulaDocente());
		}
		Evaluacion eval;
		if (archivo.getEvaluacion() == null) {
			eval = new Evaluacion();
			em.persist(eval);
		} else {
			eval = archivo.getEvaluacion();
		}
		eval.setDescripcion(evaluacion.getDescripcion());
		eval.setDocente(docente);
		eval.setFecha(new Date());
		eval.setNota(evaluacion.getNota());
		archivo.setEvaluacion(eval);
		archivo.setEstado(EstadoArchivo.Corregido);
		return evaluacion;
	}

	
	public List<ArchivoDTO> getUserPrivateFiles (String matefunToken, InvitadoEJB invitadoEJB, String filepath) throws Exception {
		List<ArchivoDTO> result = new ArrayList<ArchivoDTO>();
		Long rootId = Utils.getIdByFilepath(filepath, invitadoEJB.getMoodleUserId(matefunToken), false);
		result.add(ArchivoDTO.rootDir(rootId, invitadoEJB.getUsuario(matefunToken).getCedula() ));
		result.addAll(getAllMoodlePrivateFiles(matefunToken, invitadoEJB, filepath, new ArrayList<ArchivoRestriccion>()));
		return result;
	}

	
	private boolean cumpleRestriccion(JsonObject entity, List<ArchivoRestriccion> restricciones) throws MatefunException {

		
		// Si el archivo 'filename' cumple alguna restriccion, entonces se entrega
		
		
		//System.out.println("cumpleRestriccion: dir?: " + entity.getBoolean("isdir") + " filename: " + entity.getString("filename") );
		
		if (entity.getBoolean("isdir") || restricciones.isEmpty())
			return true;
		
		String filename = entity.getString("filename");
		
		for (ArchivoRestriccion restriccion : restricciones) {
			switch(restriccion.getTipoDestinatario()) {
			  
			  case CURSO:
				 //System.out.print("\tCURSO: " + restriccion.getValor() + " " + filename);
				 if (Pattern.compile(String.format("dcur%d]",restriccion.getValor())).matcher(filename).find()) {
					 //System.out.println(" true\n------\n");
					 return true;
				 } else {
					 //System.out.println(" false");
				 }
			  break;
			  
			  case GRUPO:
				//System.out.print("\tGRUPO: " + restriccion.getValor1() + " " + restriccion.getValor2() + " " + filename);
				if (Pattern.compile(String.format("dcur%dgrp%d]", restriccion.getValor1(), restriccion.getValor2())).matcher(filename).find()) {
					//System.out.println(" true\n------\n");
				 	return true;
				 } else {
					 //System.out.println(" false");
				 }
			  break;

			  case USUARIO:
				  //System.out.print("\tUSUARIO: " + restriccion.getValor() + " " + filename);
				  if (Pattern.compile(String.format("dusr%d", restriccion.getValor() )).matcher(filename).find()) {
					 //System.out.println(" true\n------\n");
					 return true;
				 }else {
					 //System.out.println(" false");
				 }
			  break;
			    
			  default:
			    throw new MatefunException("Tipo Destinatario desconocido");
			}
		}
		//System.out.print("\n------\n");
		return false;
	}
	
	@SuppressWarnings("serial")
	public List<ArchivoDTO> getAllMoodlePrivateFiles(String matefunToken, InvitadoEJB invitadoEJB, String filepath, List<ArchivoRestriccion> restricciones, boolean...forceMatefunWebServicesUser) throws Exception {
		
		boolean _forceMatefunWebServicesUser = forceMatefunWebServicesUser!=null && forceMatefunWebServicesUser.length>0 && forceMatefunWebServicesUser[0];
		
		Long filesOwner = _forceMatefunWebServicesUser ?
							invitadoEJB.getMoodleWebServicesUserId(matefunToken) :
							invitadoEJB.getMoodleUserId(matefunToken);
		
		List<ArchivoDTO> files = new ArrayList<ArchivoDTO>();
		Map<String, Object> params = new HashMap<String, Object>() {{
			put("contextid", -1 );
			put("component", "user");
			put("filearea", "private");
			put("itemid", 0);
			put("filepath", filepath);
			put("contextlevel", "user");
			put("instanceid", filesOwner);
			put("filename", "");
		}};
		
		JsonObject result = MoodleWS.GET(invitadoEJB, matefunToken, MoodleFunctions.core_files_get_files, params, _forceMatefunWebServicesUser);
		if (result.getBoolean(MoodleWS.IS_OK)) {
			JsonArray entities = result.getJsonObject("result").getJsonArray("files");

			for (int index = 0 ; index < entities.size() ; index++ ) {
				JsonObject entity = entities.getJsonObject(index);

				if (!cumpleRestriccion(entity, restricciones))
					continue;
				
				String moodleFilePath = entity.getBoolean("isdir") ? entity.getString("filepath") :
											 						 entity.getString("url").replace("?forcedownload=1", "")
											 						 						.replace("?forcedownload=0", "")
											 						 						.replace("webservice/pluginfile.php", "pluginfile.php")
											 						 						.replace("pluginfile.php" , "webservice/pluginfile.php");
				
				Date fechaCreacion = new java.util.Date(entity.getInt("timecreated") /*- Utils.DifferenceFromGMTSeconds*/);
				
				
				String path = entity.getString("filepath") + (!entity.getBoolean("isdir") ? entity.getString("filename") : "");
				
				Long id = Utils.getIdByFilepath(path, invitadoEJB.getMoodleUserId(matefunToken), _forceMatefunWebServicesUser);
				Long fatherId = Utils.getIdByFilepath(filepath, invitadoEJB.getMoodleUserId(matefunToken), _forceMatefunWebServicesUser);
				
				TipoArchivo tipo = _forceMatefunWebServicesUser ?
										TipoArchivo.COMPARTIDO :
										TipoArchivo.PRIVADO;
				String filename = _forceMatefunWebServicesUser ?
									entity.getString("filename").replaceFirst("\\[.*?\\]", "") :
									entity.getString("filename");
				
				String directorioMatefun = !entity.getBoolean("isdir") ? entity.getString("filepath") : entity.getString("filepath").replaceFirst( entity.getString("filename") + "\\/$", "");
				
				files.add(new ArchivoDTO(id, filename, fechaCreacion, null, invitadoEJB.getUsuario(matefunToken).getCedula(), true, false, fatherId, -1l, entity.getBoolean("isdir"), EstadoArchivo.Edicion.toString(), moodleFilePath, new EvaluacionDTO(),tipo, !(TipoArchivo.COMPARTIDO==tipo), directorioMatefun));
				//TODO: puedeCompartir no debe depender de si el archivo es compartido o no, debe haber checkbox
			}
		}

		return files;
	}
	
	
	public List<ArchivoDTO> getAllFilesSharedToMe(String matefunToken, InvitadoEJB invitadoEJB, String filepath) throws Exception {
		return (new WSUserSharedFilesMgr())
				.getAllFilesSharedToMe(matefunToken, invitadoEJB, this, filepath);
	}
	
	public String getMoodleSharedFileContents(String matefunToken, InvitadoEJB invitadoEJB, String filepath) throws Exception {
		return (new WSUserSharedFilesMgr())
				.getMoodleSharedFileContents(matefunToken, invitadoEJB, filepath);
		
	}
	
	@SuppressWarnings("serial")
	public List<ArchivoDTO> getAllMoodleCourseFiles(String matefunToken, InvitadoEJB invitadoEJB) throws Exception {
		List<ArchivoDTO> files = new ArrayList<ArchivoDTO>();
		//files.add(ArchivoDTO.rootDir(-1l, invitadoEJB.getUsuario(matefunToken).getCedula()));
		
		List<Long> allCourseIds = invitadoEJB.getCoursesInfo(matefunToken).getEnrolledcourses().stream()
									.map(c -> c.getId()).collect(Collectors.toList());

		for (Long cursoId : allCourseIds) {
			
			Map<String, Object> params = new HashMap<String, Object>() {{ put("courseid", cursoId ); }};
			JsonObject result = MoodleWS.GET(invitadoEJB, matefunToken, MoodleFunctions.core_course_get_contents, params );

			Long fileId = 0l;
			if (result.getBoolean(MoodleWS.IS_OK)) {
				JsonArray contents = result.getJsonArray(MoodleWS.RESULT_KEY);
				for (int i=0 ; i<contents.size() ; i++ ) {
					JsonObject content = contents.getJsonObject(i);
					JsonArray modules = content.getJsonArray("modules");
					for (int j=0 ; j<modules.size() ; j++) {
						JsonObject module = modules.getJsonObject(j);
						if (module.containsKey("contents")) {
							JsonArray moduleContents = module.getJsonArray("contents");
							for (int k=0 ; k < moduleContents.size() ; k++ ) {
								JsonObject moduleContent = moduleContents.getJsonObject(k);
								if (moduleContent.containsKey("type") && moduleContent.getString("type").equals("file")) {

									String moodleFilePath = moduleContent.getString("fileurl")
											.replace("?forcedownload=1", "")
											.replace("?forcedownload=0", "")
											.replace("webservice/pluginfile.php", "pluginfile.php")
											.replace("pluginfile.php" , "webservice/pluginfile.php");

									String fileContents = MoodleFiles.getMoodleFileContents(invitadoEJB, moodleFilePath, matefunToken);
									/*
									String path = entity.getString("filepath") + (!entity.getBoolean("isdir") ? entity.getString("filename") : "");
									
									Long id = Utils.getIdByFilepath(path, invitadoEJB.getMoodleUserId(matefunToken), _forceMatefunWebServicesUser);
									Long fatherId = Utils.getIdByFilepath(filepath, invitadoEJB.getMoodleUserId(matefunToken), _forceMatefunWebServicesUser);
								 * */
									
									files.add(new ArchivoDTO(
											fileId++, //id
											moduleContent.getString("filename"), //nombre
											new Date(), //fechaCreacion
											fileContents, //contenido
											invitadoEJB.getUsuario(matefunToken).getCedula(), //cedulaCreador
											true, //editable
											false, //eliminado
											-1l, //padreId
											-1l, //archivoOrigenId
											false, //directorio
											EstadoArchivo.Edicion.toString(), //estado
											moodleFilePath, //moodleFilePath
											new EvaluacionDTO(), //evaluacion
											TipoArchivo.CURSO, //tipo
											false, //puedeCompartir
											"/"));
								}
							}
						}
						
					}
				}
				
			}else {
				throw new MatefunException("core_course_get_contents execution not ok");
			}
		}

		return files;
	}
	
    public List<ArchivoDTO> compartirArchivo(String matefunToken, InvitadoEJB invitadoEJB, CompartirArchivoInputDTO dataShareFile) throws Exception {
    	return (new WSUserSharedFilesMgr())
    		    .createUpdateSharedFile(matefunToken, invitadoEJB, this, dataShareFile);
    }
	
}

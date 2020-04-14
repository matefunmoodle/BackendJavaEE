package edu.proygrado.ejb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import edu.proygrado.dto.AllUsersResultDTO;
import edu.proygrado.dto.ArchivoDTO;
import edu.proygrado.dto.BackupAlumnoDTO;
import edu.proygrado.dto.BackupDocenteDTO;
import edu.proygrado.dto.BackupUsuarioDTO;
import edu.proygrado.dto.ConfiguracionDTO;
import edu.proygrado.dto.EvaluacionDTO;
import edu.proygrado.dto.GrupoDTO;
import edu.proygrado.dto.UserResultDTO;
import edu.proygrado.matefun.MatefunException;
import edu.proygrado.modelo.Admin;
import edu.proygrado.modelo.Alumno;
import edu.proygrado.modelo.Archivo;
import edu.proygrado.modelo.Configuracion;
import edu.proygrado.modelo.Docente;
import edu.proygrado.modelo.EstadoArchivo;
import edu.proygrado.modelo.Evaluacion;
import edu.proygrado.modelo.Grupo;
import edu.proygrado.modelo.GrupoPK;
import edu.proygrado.modelo.LiceoPK;
import edu.proygrado.modelo.Usuario;
import edu.proygrado.utils.MoodleFunctions;
import edu.proygrado.utils.MoodleWS;
import edu.proygrado.utils.StringPair;

@Stateless
public class UsuarioEJB {
	
	@PersistenceContext(unitName = "matefunDS")
    private EntityManager em;
	
	@EJB
	private InvitadoEJB invitadoEJB;
	
	public ConfiguracionDTO actualizarConfiguracion(String cedula, ConfiguracionDTO configuracion) throws Exception{
		Usuario usuario;
        try {
            
            usuario = em.createQuery("select u from Usuario u where u.cedula=:cedula", Usuario.class)
                    .setParameter("cedula", cedula)
                    .getSingleResult();
            if(usuario.getConfiguracion()==null){
            	usuario.setConfiguracion(new Configuracion(configuracion));
            }else{
            	Configuracion config = usuario.getConfiguracion();
            	config.setThemeEditor(configuracion.getThemeEditor());
            	config.setFontSizeEditor(config.getFontSizeEditor());
            	config.setArgumentoF(configuracion.isArgumentoF());
            	config.setArgumentoI(configuracion.isArgumentoI());
            }
        } catch (Exception ex) {
        	throw new Exception("Excepcion al guardar configuraciones");
        }
        return new ConfiguracionDTO(usuario.getConfiguracion());
	}
	
	public ConfiguracionDTO getConfiguracion(String cedula){
		Usuario usuario;
        try {
            
            usuario = em.createQuery("select u from Usuario u where u.cedula=:cedula", Usuario.class)
                    .setParameter("cedula", cedula)
                    .getSingleResult();
            if(usuario.getConfiguracion()==null){
            	return null;
            }else{
            	return new ConfiguracionDTO(usuario.getConfiguracion());
            }
        } catch (Exception ex) {
        	return null;
        }
        
	}
	
	public BackupUsuarioDTO respaldarUsuario(String cedula) throws MatefunException{
		Usuario usuario = em.find(Usuario.class, cedula);
		if(usuario == null){
			throw new MatefunException("No existe el usuario con cedula "+cedula);
		}
		if(usuario instanceof Alumno){
			List<Grupo> grupos = em.createQuery("select g from Grupo g where exists (select al from g.alumnos al where al.cedula =:cedula )", Grupo.class)
					.setParameter("cedula", cedula)
					.getResultList();
			return new BackupAlumnoDTO((Alumno) usuario,grupos);
		}else{
			return new BackupDocenteDTO((Docente) usuario);
		}
	}
	
	public String restaurarAlumno(BackupAlumnoDTO backupAlumnoDTO) throws MatefunException {
		Usuario usuario = em.find(Usuario.class, backupAlumnoDTO.getCedula());
		if(usuario!=null){
			throw new MatefunException("Ya existe el usuario de cedula "+backupAlumnoDTO.getCedula());
		}
		Alumno alumno = new Alumno();
		alumno.setCedula(backupAlumnoDTO.getCedula());
		alumno.setNombre(backupAlumnoDTO.getNombre());
		alumno.setApellido(backupAlumnoDTO.getApellido());
		alumno.setArchivos(new ArrayList<>());
		alumno.setArchivosCompartidos(new ArrayList<>());
		Configuracion config = new Configuracion(backupAlumnoDTO.getConfiguracion());
		em.persist(config);
		alumno.setConfiguracion(config);
		if(backupAlumnoDTO.getGrupos()!=null){
			for(GrupoDTO grupoDTO: backupAlumnoDTO.getGrupos()){
				LiceoPK lpk = new LiceoPK(grupoDTO.getLiceoId());
				GrupoPK gpk = new GrupoPK(grupoDTO.getAnio(), grupoDTO.getGrado(), grupoDTO.getGrupo(), lpk);
				Grupo grupo = em.find(Grupo.class, gpk);
				grupo.addAlumno(alumno);
			}
		}
		if(backupAlumnoDTO.getArchivos()!=null){
			Archivo root = null;
			for(ArchivoDTO archivoDTO : backupAlumnoDTO.getArchivos()){
				Archivo archivo = new Archivo();
				archivo.setArchivoOrigen(null);
				archivo.setContenido(archivoDTO.getContenido());
				archivo.setDirectorio(archivoDTO.isDirectorio());
				archivo.setEditable(archivoDTO.isEditable());
				archivo.setEliminado(archivoDTO.isEliminado());
				archivo.setEstado(EstadoArchivo.valueOf(archivoDTO.getEstado()));
				archivo.setFechaCreacion(archivoDTO.getFechaCreacion());
				archivo.setNombre(archivoDTO.getNombre());
				archivo.setCreador(alumno);
				if(archivoDTO.getEvaluacion()!=null){
					EvaluacionDTO evalDTO = archivoDTO.getEvaluacion();
					Docente docente = em.find(Docente.class, evalDTO.getCedulaDocente());
					if(docente!=null){
						Evaluacion eval = new Evaluacion();
						eval.setDescripcion(evalDTO.getDescripcion());
						eval.setFecha(evalDTO.getFecha());
						eval.setNota(evalDTO.getNota());
						eval.setDocente(docente);
						archivo.setEvaluacion(eval);
					}
				}
				if(archivoDTO.getPadreId() == -1){
					root = archivo;
					root.setPadre(null);
				}
				alumno.addArchivo(archivo);
			}
			for(Archivo archivo : alumno.getArchivos()){
				if(archivo != root){
					archivo.setPadre(root);
				}
				em.persist(archivo);
			}
		}
		if(backupAlumnoDTO.getArchivosCompartidos()!=null){
			for(ArchivoDTO archivoDTO : backupAlumnoDTO.getArchivosCompartidos()){
				Archivo archivo = new Archivo();
				Archivo origen = em.find(Archivo.class, archivoDTO.getId());
				archivo.setArchivoOrigen(origen);
				archivo.setContenido(archivoDTO.getContenido());
				archivo.setDirectorio(archivoDTO.isDirectorio());
				archivo.setEditable(archivoDTO.isEditable());
				archivo.setEliminado(archivoDTO.isEliminado());
				archivo.setEstado(EstadoArchivo.valueOf(archivoDTO.getEstado()));
				archivo.setFechaCreacion(archivoDTO.getFechaCreacion());
				archivo.setNombre(archivoDTO.getNombre());
				if(origen!=null){
					Docente creador = em.find(Docente.class, archivoDTO.getCedulaCreador());
					if(creador!=null){
						archivo.setCreador(creador);
					}else{
						archivo.setCreador(alumno);
					}
				}
				if(archivoDTO.getEvaluacion()!=null){
					EvaluacionDTO evalDTO = archivoDTO.getEvaluacion();
					Docente docente = em.find(Docente.class, evalDTO.getCedulaDocente());
					if(docente!=null){
						Evaluacion eval = new Evaluacion();
						eval.setDescripcion(evalDTO.getDescripcion());
						eval.setFecha(evalDTO.getFecha());
						eval.setNota(evalDTO.getNota());
						eval.setDocente(docente);
						archivo.setEvaluacion(eval);
						em.persist(eval);
					}
				}
				archivo.setPadre(null);
				//Si esta definido el archivo origen lo agrego a los compartidos
				//sino se agrega a la lista de archivos del usuario. 
				if(origen!=null){
					alumno.addArchivoCompartido(archivo);
				}else{
					alumno.addArchivo(archivo);
				}
				em.persist(archivo);
			}
			
		}
		em.persist(alumno);
		return "Alumno ingresado. ";
	}	
	
	public String restaurarDocente(BackupDocenteDTO backupDocenteDTO) throws MatefunException {
		Usuario usuario = em.find(Usuario.class, backupDocenteDTO.getCedula());
		if(usuario!=null){
			throw new MatefunException("Ya existe el usuario de cedula "+backupDocenteDTO.getCedula());
		}
		Docente docente = new Docente();
		docente.setCedula(backupDocenteDTO.getCedula());
		docente.setNombre(backupDocenteDTO.getNombre());
		docente.setApellido(backupDocenteDTO.getApellido());
		docente.setArchivos(new ArrayList<>());
		docente.setGruposAsignados(new ArrayList<>());
		Configuracion config = new Configuracion(backupDocenteDTO.getConfiguracion());
		em.persist(config);
		docente.setConfiguracion(config);
		if(backupDocenteDTO.getGruposAsignados()!=null){
			for(GrupoDTO grupoDTO: backupDocenteDTO.getGruposAsignados()){
				LiceoPK lpk = new LiceoPK(grupoDTO.getLiceoId());
				GrupoPK gpk = new GrupoPK(grupoDTO.getAnio(), grupoDTO.getGrado(), grupoDTO.getGrupo(), lpk);
				Grupo grupo = em.find(Grupo.class, gpk);
				docente.addGrupoAsignado(grupo);
			}
		}
		if(backupDocenteDTO.getArchivos()!=null){
			Archivo root = null;
			for(ArchivoDTO archivoDTO : backupDocenteDTO.getArchivos()){
				Archivo archivo = new Archivo();
				archivo.setArchivoOrigen(null);
				archivo.setContenido(archivoDTO.getContenido());
				archivo.setDirectorio(archivoDTO.isDirectorio());
				archivo.setEditable(archivoDTO.isEditable());
				archivo.setEliminado(archivoDTO.isEliminado());
				archivo.setEstado(EstadoArchivo.valueOf(archivoDTO.getEstado()));
				archivo.setFechaCreacion(archivoDTO.getFechaCreacion());
				archivo.setNombre(archivoDTO.getNombre());
				archivo.setCreador(docente);
				//No se deberia dar el caso. Se deja por completitud
				if(archivoDTO.getEvaluacion()!=null){
					EvaluacionDTO evalDTO = archivoDTO.getEvaluacion();
					Docente evaluador = em.find(Docente.class, evalDTO.getCedulaDocente());
					if(evaluador!=null){
						Evaluacion eval = new Evaluacion();
						eval.setDescripcion(evalDTO.getDescripcion());
						eval.setFecha(evalDTO.getFecha());
						eval.setNota(evalDTO.getNota());
						eval.setDocente(evaluador);
						archivo.setEvaluacion(eval);
						em.persist(eval);
					}
				}
				if(archivoDTO.getPadreId() == -1){
					root = archivo;
					root.setPadre(null);
				}
				docente.addArchivo(archivo);
				em.persist(archivo);
			}
			for(Archivo archivo : docente.getArchivos()){
				if(archivo != root){
					archivo.setPadre(root);
				}
			}
		}
		em.persist(docente);
		return "Docente ingresado. ";
	}
	
	public String eliminarUsuario(String cedula) throws MatefunException {
		Usuario usuario = em.find(Usuario.class, cedula);
		if(usuario == null){
			throw new MatefunException("No existe el usuario con cedula "+cedula);
		}
		if(usuario instanceof Alumno){
			Alumno alumno = (Alumno) usuario;
			List<Grupo> grupos = em.createQuery("select g from Grupo g where exists (select al from g.alumnos al where al.cedula =:cedula )", Grupo.class)
					.setParameter("cedula", cedula)
					.getResultList();
			for(Grupo grupo : grupos){
				grupo.getAlumnos().remove(alumno);
				grupo.getArchivos().removeAll(alumno.getArchivos());				
			}
			System.out.println("Alumno");
			for(Archivo archivo: alumno.getArchivos()){
				System.out.println(archivo.getId());
				Evaluacion eval = archivo.getEvaluacion();
				if(eval!=null){
					eval.setDocente(null);
					em.remove(eval);
				}
				archivo.setArchivoOrigen(null);
				archivo.setCreador(null);
				archivo.setEvaluacion(null);
				archivo.setPadre(null);
				em.remove(archivo);
			}
			alumno.setArchivos(null);
			System.out.println("Compartidos");
			for(Archivo archivo: alumno.getArchivosCompartidos()){
				System.out.println(archivo.getId());
				Evaluacion eval = archivo.getEvaluacion();
				if(eval!=null){
					eval.setDocente(null);
					em.remove(eval);
				}
				archivo.setArchivoOrigen(null);
				archivo.setCreador(null);
				archivo.setEvaluacion(null);
				archivo.setPadre(null);
				em.remove(archivo);
			}
			alumno.setArchivosCompartidos(null);
			//Codigo para corregir bug de archivos creados por el alumno pero no referenciados en su lista de archivos.
			List<Archivo> archivosNoReferenciados = em.createQuery("select a from Archivo a where a.creador.cedula = :cedula",Archivo.class)
					.setParameter("cedula", cedula)
					.getResultList();
			for(Archivo archivo: archivosNoReferenciados){
				System.out.println(archivo.getId());
				Evaluacion eval = archivo.getEvaluacion();
				if(eval!=null){
					eval.setDocente(null);
					em.remove(eval);
				}
				archivo.setArchivoOrigen(null);
				archivo.setCreador(null);
				archivo.setEvaluacion(null);
				archivo.setPadre(null);
				em.remove(archivo);
			}
			
			em.remove(alumno);
		}else{
			Docente docente = (Docente) usuario;
			
			for(Archivo archivo: docente.getArchivos()){
				archivo.setPadre(null);
				List<Grupo> grupos = em.createQuery("select g from Grupo g where exists (select a from g.archivos a where a.id=:id)",Grupo.class)
						.setParameter("id", archivo.getId())
						.getResultList();
				for(Grupo g : grupos){
					g.getArchivos().remove(archivo);
				}
				
				System.out.println("Archivo docente "+archivo.getId());
				Evaluacion eval = archivo.getEvaluacion();
				if(eval!=null){
					eval.setDocente(null);
					em.remove(eval);
				}
				//Lista de archivos que son copia del compartido por el docente. 
				List<Archivo> archivos = em.createQuery("select a from Archivo a where a.archivoOrigen.id = :id", Archivo.class )
						.setParameter("id", archivo.getId())
						.getResultList();
				for(Archivo copiaAlumno : archivos){
					System.out.println("copia archivo "+copiaAlumno.getId());
					List<Alumno> alumnos = em.createQuery("select a from Alumno a where exists (select ac from a.archivosCompartidos ac where ac.id = :id)",Alumno.class)
							.setParameter("id", copiaAlumno.getId())
							.getResultList();
					
					//Los archivos compartidos que tiene el alumno se mueven a su lista de archivos personales. 
					for(Alumno alumno : alumnos){
						alumno.getArchivosCompartidos().remove(copiaAlumno);
						Archivo root = em.createQuery("select a from Archivo a where a.padre = null and a.creador.cedula=:cedula",Archivo.class)
								.setParameter("cedula", alumno.getCedula())
								.getSingleResult();
						copiaAlumno.setPadre(root);
						copiaAlumno.setCreador(alumno);
						copiaAlumno.setEstado(EstadoArchivo.Edicion);
						copiaAlumno.setArchivoOrigen(null);
						alumno.addArchivo(copiaAlumno);
					}					
				}
			}
			
			for(Archivo archivo:docente.getArchivos()){
				em.remove(archivo);
			}
			em.remove(docente);
		}
		
		return "Usuario y recursos eliminados";
	}
	
	private static List<String> getSkippedUsers() {
		//TODO: hacer bien esto
		List<String> result = new ArrayList<String>();
		result.add("guest");
		result.add("admin");
		result.add("wsuser");
		return result;
	}
	
	public String getMatefunAdminUsername() {
		try {
			return em.createQuery("select a from Admin a", Admin.class).getSingleResult().getCedula();
		} catch (Exception ex) { 
			ex.printStackTrace();
		}
		return null;
	}
	
	
	@SuppressWarnings("serial")
	public List<UserResultDTO> getAllNonSuspendedUsers(String loggedUserToken, StringPair criteria) throws Exception{

		//parameters
		Map<String, Object> params = new HashMap<String, Object>() {{
	        put("criteria[0][key]", criteria.getKey() );
	        put("criteria[0][value]", criteria.getValue() );
	    }};

	    JsonObject result = MoodleWS.GET(invitadoEJB, loggedUserToken, MoodleFunctions.core_user_get_users, params, true);
	    
	    if (!result.getBoolean(MoodleWS.IS_OK)) {
	    	throw new MatefunException("No se puede obtener informacion de usuarios con core_user_get_users");
	    }else {
	    	
	    	JsonArray users = result.getJsonObject("result").getJsonArray("users");
		    AllUsersResultDTO userInfo = new AllUsersResultDTO();
		    userInfo.setWarnings(null);
		    userInfo.setUsers(new ArrayList<UserResultDTO>());		    
		    
		    //List<String> skippedUsers = getSkippedUsers(); //TODO: como esta funcion ahora es generica, sacar el skippedUsers para afuera
		    for (int i = 0; i < users.size(); ++i)
		    	userInfo.getUsers().add(new UserResultDTO(users.getJsonObject(i)));

			return userInfo.getUsers();
	    }
	}

}

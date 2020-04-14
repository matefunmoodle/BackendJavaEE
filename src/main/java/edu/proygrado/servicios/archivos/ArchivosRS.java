package edu.proygrado.servicios.archivos;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import edu.proygrado.dto.ArchivoDTO;
import edu.proygrado.dto.ArchivoRestriccion;
import edu.proygrado.dto.CompartirArchivoInputDTO;
import edu.proygrado.dto.EvaluacionDTO;
import edu.proygrado.dto.SimplePostResultDTO;
import edu.proygrado.ejb.ArchivosEJB;
import edu.proygrado.ejb.InvitadoEJB;
import edu.proygrado.utils.MoodleConstants;
import edu.proygrado.utils.MoodleFiles;
import edu.proygrado.utils.Utils;

@Stateless
@Path("/archivo")
public class ArchivosRS{
	
    @EJB
    private ArchivosEJB archivosEJB;
    
    @EJB
    private InvitadoEJB invitadoEJB;
    
    @Inject
    private HttpServletRequest httpServletRequest;
    
    @GET
    @Path("/contenido")
    @Produces(MediaType.APPLICATION_JSON)
    public String getContenido(@QueryParam("moodleFilePath") String moodleFilePath, @QueryParam("compartido") Boolean compartido) throws Exception {
    	//APTO SHARED
    	boolean _compartido = compartido!=null && compartido;
    	if (!_compartido)
    		return MoodleFiles.getMoodleFileContents(invitadoEJB, moodleFilePath, Utils.getToken(httpServletRequest));
    	else
    		return archivosEJB.getMoodleSharedFileContents(Utils.getToken(httpServletRequest), invitadoEJB, moodleFilePath);
    }
    
    @GET
    @Path("/contenido/directorio")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ArchivoDTO> getDirectoryContents(@QueryParam("filepath") String filepath, @QueryParam("compartido") Boolean compartido) throws Exception {
    	boolean _compartido = compartido!=null && compartido;
    	if (!_compartido)
    		return archivosEJB.getAllMoodlePrivateFiles(Utils.getToken(httpServletRequest), invitadoEJB, filepath, new ArrayList<ArchivoRestriccion>());
    	else
    		return archivosEJB.getAllFilesSharedToMe(Utils.getToken(httpServletRequest), invitadoEJB, filepath);
    }
    

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/cursos")
    public List<ArchivoDTO> getArchivosDeCursos(@QueryParam("cedula") String cedula) throws Exception {
    	return archivosEJB.getAllMoodleCourseFiles(Utils.getToken(httpServletRequest), invitadoEJB);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ArchivoDTO> getArchivosUsuario(@QueryParam("cedula") String cedula, @QueryParam ("compartidos") Boolean compartidos) throws Exception {
    	// FE: HaskellService.getArchivos-> get '/servicios/archivo'
    	// FE: HaskellService.getArchivosCompartidosAlumno-> get '/servicios/archivo' (compartidos=true)
    	
    	String matefunToken = Utils.getToken(httpServletRequest);
    	if (invitadoEJB.getUsuario(matefunToken).getTipo().equals("admin"))
    		return new ArrayList<ArchivoDTO>();

    	if(compartidos!=null && compartidos){
        	if(Utils.esInvitado(invitadoEJB, httpServletRequest)){
        		return invitadoEJB.getArchivosCompartidosAlumno(matefunToken, cedula);
        	}else{
        		return archivosEJB.getAllMoodleCourseFiles(matefunToken, invitadoEJB);
        	}
        }else{
        	if(Utils.esInvitado(invitadoEJB, httpServletRequest)){
        		return invitadoEJB.getArchivosUsuario(matefunToken,cedula);
        	}else{
        		return archivosEJB.getUserPrivateFiles(matefunToken, invitadoEJB, MoodleConstants.privateFilesRootDir);
        	}
        }
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/compartido/{archivoId}")
    public ArchivoDTO getCopiaCompartido(@QueryParam("cedula") String cedula, @PathParam("archivoId") Long idArchivo) throws Exception{
    	//FE: HaskellService.getCopiaArchivoCompartidoGrupo -> get(SERVER+'/servicios/archivo/compartido/'+archivoId
    	if(Utils.esInvitado(invitadoEJB, httpServletRequest)){
    		return invitadoEJB.getCopiaCompartido(Utils.getToken(httpServletRequest),cedula,idArchivo);
    	}else{
    		return archivosEJB.getCopiaCompartido(cedula,idArchivo);
    	}
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ArchivoDTO crearArchivo(ArchivoDTO archivoDTO) throws Exception{
    	if(Utils.esInvitado(invitadoEJB, httpServletRequest)){
    		return invitadoEJB.crearArchivo(Utils.getToken(httpServletRequest),archivoDTO);
    	}else{
    		//APTO SHARED
    		String filepath = archivoDTO.getDirectorioMatefun();
    		return archivosEJB.crearArchivoPrivadoMoodle(archivoDTO, Utils.getToken(httpServletRequest), invitadoEJB, filepath, false);
    	}
    }
    
    
    @POST
    @Path("/compartir")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public SimplePostResultDTO compartirArchivo(CompartirArchivoInputDTO dataShareFile) throws Exception {
    	return new SimplePostResultDTO("Archivo compartido con exito.", true, 
    			archivosEJB.compartirArchivo(Utils.getToken(httpServletRequest), invitadoEJB, dataShareFile));
    }
    
    
    @PUT
    @Path("/{archivoId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ArchivoDTO editarArchivo(@PathParam("archivoId") long archivoId, ArchivoDTO archivoDTO) throws Exception{
    	if(Utils.esInvitado(invitadoEJB, httpServletRequest)){
        	return invitadoEJB.editarArchivo(Utils.getToken(httpServletRequest),archivoId, archivoDTO);
        }else{
        	String filepath = archivoDTO.getMoodleFilePath().substring(archivoDTO.getMoodleFilePath().indexOf("private") + "private".length(), archivoDTO.getMoodleFilePath().lastIndexOf("/")) + "/";
        	return archivosEJB.crearArchivoPrivadoMoodle(archivoDTO, Utils.getToken(httpServletRequest), invitadoEJB, filepath, false);
        }
    }
    
    @DELETE
    @Path("/{archivoId}")
    public void eliminarArchivo(@PathParam("archivoId") long archivoId) throws Exception{
    	//FE: HaskellService.eliminarArchivo -> delete(SERVER+'/servicios/archivo/'+archivoId
    	if(Utils.esInvitado(invitadoEJB, httpServletRequest)){
    		invitadoEJB.eliminarArchivo(Utils.getToken(httpServletRequest),archivoId);
    	}else{
    		archivosEJB.eliminarArchivo(archivoId);
    	}
    }
    
    @POST
    @Path("/{archivoId}/evaluacion")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public EvaluacionDTO evaluarArchivo(@PathParam("archivoId") long archivoId, EvaluacionDTO evaluacion) throws Exception {
    	//FE: HaskellService.calificarArchivo -> post(SERVER+'/servicios/archivo/'+archivoId+'/evaluacion'
    	return archivosEJB.evaluarArchivo(archivoId, evaluacion);
    }

}

package edu.proygrado.servicios.archivos;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import edu.proygrado.dto.ArchivoDTO;
import edu.proygrado.dto.EvaluacionDTO;
import edu.proygrado.ejb.ArchivosEJB;
import edu.proygrado.ejb.InvitadoEJB;
import edu.proygrado.modelo.Usuario;

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
    @Produces(MediaType.APPLICATION_JSON)
    public List<ArchivoDTO> getArchivosUsuario(@QueryParam("cedula") String cedula, @QueryParam ("compartidos") Boolean compartidos) throws Exception {
        if(compartidos!=null && compartidos){
        	if(esInvitado()){
        		return invitadoEJB.getArchivosCompartidosAlumno(getToken(), cedula);
        	}else{
        		return archivosEJB.getArchivosCompartidosAlumno(cedula);
        	}
        }else{
        	if(esInvitado()){
        		return invitadoEJB.getArchivosUsuario(getToken(),cedula);
        	}else{
        		return archivosEJB.getArchivosUsuario(cedula);
        	}
        }
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/compartido/{archivoId}")
    public ArchivoDTO getCopiaCompartido(@QueryParam("cedula") String cedula, @PathParam("archivoId") Long idArchivo) throws Exception{
    	if(esInvitado()){
    		return invitadoEJB.getCopiaCompartido(getToken(),cedula,idArchivo);
    	}else{
    		return archivosEJB.getCopiaCompartido(cedula,idArchivo);
    	}
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ArchivoDTO crearArchivo(ArchivoDTO archivoDTO) throws Exception{
    	if(esInvitado()){
    		return invitadoEJB.crearArchivo(getToken(),archivoDTO);
    	}else{
    		return archivosEJB.crearArchivo(archivoDTO);
    	}
    }
    
    @PUT
    @Path("/{archivoId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ArchivoDTO editarArchivo(@PathParam("archivoId") long archivoId, ArchivoDTO archivoDTO) throws Exception{
        if(esInvitado()){
        	return invitadoEJB.editarArchivo(getToken(),archivoId, archivoDTO);
        }else{
        	return archivosEJB.editarArchivo(archivoId, archivoDTO);
        }
    }
    
    @DELETE
    @Path("/{archivoId}")
    public void eliminarArchivo(@PathParam("archivoId") long archivoId) throws Exception{
    	if(esInvitado()){
    		invitadoEJB.eliminarArchivo(getToken(),archivoId);
    	}else{
    		archivosEJB.eliminarArchivo(archivoId);
    	}
    }
    
    @POST
    @Path("/{archivoId}/evaluacion")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public EvaluacionDTO evaluarArchivo(@PathParam("archivoId") long archivoId, EvaluacionDTO evaluacion) throws Exception {
    	return archivosEJB.evaluarArchivo(archivoId, evaluacion);
    }
    
    private boolean esInvitado(){
    	String token = getToken();
    	Usuario usuario = invitadoEJB.getUsuario(token);
    	if(usuario!=null && usuario.getCedula().toLowerCase().equals("invitado")){
    		System.out.println("Es usuario invitado");
    		return true;
    	}
    	return false;
    }
    
    private String getToken(){
    	String token = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
    	if(token!=null && token.contains("Bearer ")){
    		token = token.substring("Bearer ".length());
    	}else{
    		throw new NotAuthorizedException("");
    	}
    	return token;
    }
}

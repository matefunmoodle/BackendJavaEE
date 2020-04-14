package edu.proygrado.servicios.evaluaciones;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import edu.proygrado.dto.AlumnoDTO;
import edu.proygrado.dto.ArchivoDTO;
import edu.proygrado.dto.Assignment;
import edu.proygrado.dto.GrupoDTO;
import edu.proygrado.dto.SimplePostResultDTO;
import edu.proygrado.dto.UsuarioDTO;
import edu.proygrado.ejb.ArchivosEJB;
import edu.proygrado.ejb.EvaluacionesEJB;
import edu.proygrado.ejb.InvitadoEJB;
import edu.proygrado.matefun.MatefunException;
import edu.proygrado.utils.Utils;

@Stateless
@Path("/evaluaciones")
public class EvaluacionesRS{
	
    @EJB
    private ArchivosEJB archivosEJB;
    
    @EJB
    private InvitadoEJB invitadoEJB;
    
    @EJB
    private EvaluacionesEJB evaluacionesEJB;
    
    @Inject
    private HttpServletRequest httpServletRequest;
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/assignments/all")
    public List<Assignment> getAllAssignments() throws Exception {
    	if(Utils.esInvitado(invitadoEJB, httpServletRequest))
    		throw new MatefunException("getAllAssignments for invitado");
    	else
    		return evaluacionesEJB.getAllAssignments(Utils.getToken(httpServletRequest), invitadoEJB);
    }
    

    @POST
    @Path("/entregaarchivo/{assignmentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public SimplePostResultDTO entregarArchivoParaEvaluacion(@PathParam("assignmentId") Integer assignmentId, ArchivoDTO archivo) throws Exception {
    	if (archivo==null || assignmentId==null)
    		throw new MatefunException("valores de entrada invalidos para /evaluaciones/entregaarchivo");
    	return evaluacionesEJB.entregarArchivoParaEvaluacion(Utils.getToken(httpServletRequest), invitadoEJB, archivosEJB, archivo, assignmentId);
    }
    
    
    @POST
    @Path("/corregir")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ArchivoDTO corregirAssignment(ArchivoDTO archivo) throws Exception {
    	if (archivo==null || (archivo!=null && archivo.getEvaluacion()==null))
    		throw new MatefunException("valores de entrada invalidos para /evaluaciones/corregir");
    	return evaluacionesEJB.corregirAssignment(Utils.getToken(httpServletRequest), invitadoEJB, archivosEJB, archivo);
    }
    
    
    @GET
    @Path("/getSubmissionStatus")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public JsonObject getSubmissionStatus(@QueryParam("assignid") Integer assignid, @QueryParam("userid") Integer userid, @QueryParam("groupid") Integer groupid) throws Exception {
    	if (assignid==null)
    		throw new Exception("Error en getSubmissionStatus, assignid es null");
    	return evaluacionesEJB.getSubmissionStatus(Utils.getToken(httpServletRequest), invitadoEJB, archivosEJB, assignid, userid, groupid);
    }
    
	@GET
	@Path("/grupal")
	@Produces(MediaType.APPLICATION_JSON)
	public List<GrupoDTO> getAssignmentsPorGrupos(@QueryParam("cursos") String cursos) throws Exception {
		
		System.out.println("getAssignmentsPorGrupos START");
		Long startTime = (new Date()).getTime();
			List<GrupoDTO> ret = this.evaluacionesEJB.getAssignmentsPorGrupos(invitadoEJB, Utils.getToken(httpServletRequest), Arrays.asList(cursos.split(",")).stream().map(c -> Long.parseLong(c)).collect(Collectors.toList()));
		System.out.println("getAssignmentsPorGrupos END, duracion: " + Math.floorDiv((new Date()).getTime()-startTime, 1000) + "s");
		return ret;
	}
	
	@GET
	@Path("/individual")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, List<AlumnoDTO>> getAssignmentsPorAlumnos(@QueryParam("cursos") String cursos) throws Exception {
		System.out.println("getAssignmentsPorAlumnos START");
		Long startTime = (new Date()).getTime();
			Map<String, List<AlumnoDTO>> ret = this.evaluacionesEJB.getAssignmentsPorAlumnos(invitadoEJB, Utils.getToken(httpServletRequest), Arrays.asList(cursos.split(",")).stream().map(c -> Long.parseLong(c)).collect(Collectors.toList()));
		System.out.println("getAssignmentsPorAlumnos END, duracion: " + Math.floorDiv((new Date()).getTime()-startTime, 1000) + "s");
		return ret;
	}
    
}

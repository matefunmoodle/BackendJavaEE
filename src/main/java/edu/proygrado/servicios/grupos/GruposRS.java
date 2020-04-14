package edu.proygrado.servicios.grupos;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import edu.proygrado.dto.ArchivoDTO;
import edu.proygrado.dto.CoursesDTO;
import edu.proygrado.ejb.GruposEJB;
import edu.proygrado.ejb.InvitadoEJB;
import edu.proygrado.modelo.GrupoPK;
import edu.proygrado.modelo.LiceoPK;
import edu.proygrado.utils.Utils;

@Stateless
@Path("/grupo")
public class GruposRS {
	
	@EJB
    private GruposEJB gruposEJB;
	
    @EJB
    private InvitadoEJB invitadoEJB;
    
    @Inject
    private HttpServletRequest httpServletRequest;
	
	@POST
    @Path("/{liceoId}/{anio}/{grado}/{grupo}/archivo")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void asignarArchivo(@PathParam("liceoId") Long liceoId, @PathParam("anio") Integer anio, @PathParam("grado") Integer grado, @PathParam("grupo") String grupo, ArchivoDTO archivoDTO) throws Exception{
		//FE: HaskellService.compartirArchivoGrupo -> post(SERVER+'/servicios/grupo/'+grupo.liceoId+'/'+grupo.anio+'/'+grupo.grado+'/'+grupo.grupo+'/archivo'
		LiceoPK lpk = new LiceoPK(liceoId);
		GrupoPK grupoPK = new GrupoPK(anio, grado, grupo, lpk);
        gruposEJB.agregarArchivoGrupo(archivoDTO.getId(), grupoPK);
    }
	
	@GET
	@Path("/members/{courseid}")
	@Produces(MediaType.APPLICATION_JSON)
	public CoursesDTO getCourseGroupsAndMembers(@PathParam("courseid") Long courseid) throws Exception {
		return this.gruposEJB.getCourseGroupsAndMembers(courseid, Utils.getToken(httpServletRequest), invitadoEJB);
	}
	
}

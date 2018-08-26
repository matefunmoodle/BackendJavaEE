package edu.proygrado.servicios.usuario;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import edu.proygrado.dto.BackupAlumnoDTO;
import edu.proygrado.dto.BackupDocenteDTO;
import edu.proygrado.dto.BackupUsuarioDTO;
import edu.proygrado.dto.ConfiguracionDTO;
import edu.proygrado.ejb.UsuarioEJB;
import edu.proygrado.matefun.MatefunException;

@Stateless
@Path("/usuario")
public class UsuarioRS {
	@EJB
    private UsuarioEJB usuarioEJB;
	
	@PUT
	@Path("{cedula}/configuracion")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ConfiguracionDTO actualizarConfiguracion(@PathParam("cedula") String cedula, ConfiguracionDTO configuracion) throws Exception {
        if(cedula.toLowerCase().equals("invitado")){
        	return configuracion;
        }else{
        	return usuarioEJB.actualizarConfiguracion(cedula,configuracion);
        }
    }
	
	@GET
	@Path("{cedula}/backup")
	@Produces(MediaType.APPLICATION_JSON)
	public BackupUsuarioDTO backupUsuario(@PathParam("cedula") String cedula) throws MatefunException{
		return usuarioEJB.respaldarUsuario(cedula);
	}
	
	@POST
	@Path("alumno/restore")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String restaurarAlumno(BackupAlumnoDTO backupAlumnoDTO) throws MatefunException{
		return usuarioEJB.restaurarAlumno(backupAlumnoDTO);
	}
	
	@POST
	@Path("docente/restore")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String restaurarDocente(BackupDocenteDTO backupDocenteDTO) throws MatefunException{
		return usuarioEJB.restaurarDocente(backupDocenteDTO);
	}
	
	@DELETE
	@Path("{cedula}")
	@Produces(MediaType.TEXT_PLAIN)
	public String eliminarUsuario(@PathParam("cedula") String cedula) throws MatefunException {
		return usuarioEJB.eliminarUsuario(cedula);
	}
}

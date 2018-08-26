package edu.proygrado.servicios.grupos;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import edu.proygrado.dto.ArchivoDTO;
import edu.proygrado.dto.GrupoDTO;
import edu.proygrado.ejb.GruposEJB;
import edu.proygrado.modelo.GrupoPK;
import edu.proygrado.modelo.LiceoPK;

@Stateless
@Path("/grupo")
public class GruposRS {
	@EJB
    private GruposEJB gruposEJB;
	
	@POST
    @Path("/{liceoId}/{anio}/{grado}/{grupo}/archivo")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void asignarArchivo(@PathParam("liceoId") Long liceoId, @PathParam("anio") Integer anio, @PathParam("grado") Integer grado, @PathParam("grupo") String grupo, ArchivoDTO archivoDTO) throws Exception{
		LiceoPK lpk = new LiceoPK(liceoId);
		GrupoPK grupoPK = new GrupoPK(anio, grado, grupo, lpk);
        gruposEJB.agregarArchivoGrupo(archivoDTO.getId(), grupoPK);
    }
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<GrupoDTO> getGruposDocente(@QueryParam("cedula") String cedula){
		return this.gruposEJB.getGruposDocente(cedula);
	}
	
	
}

package edu.proygrado.servicios.liceo;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import edu.proygrado.dto.LiceoDTO;
import edu.proygrado.dto.SimplePostResultDTO;
import edu.proygrado.ejb.LiceoEJB;
import edu.proygrado.ejb.LoginEJB;
import edu.proygrado.matefun.MatefunException;



@Stateless
@Path("/liceo")
public class LiceoRS{

	@EJB
    private LoginEJB loginEJB;
	
	@EJB
    private LiceoEJB liceoEJB;
    
    @POST
    @Path("/addNewSchool")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public SimplePostResultDTO addNewSchool(LiceoDTO input) throws MatefunException{
    	if (input != null)
    		return liceoEJB.addNewSchool(input);
    	throw new MatefunException("Valores de entrada nulos en el servicio 'addNewSchool'");
    }
    
    @POST
    @Path("/updateSchool")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public SimplePostResultDTO updateSchool(LiceoDTO input) throws Exception{

    	if (input != null)
    		return liceoEJB.updateSchool(input);
    	throw new MatefunException("Valores de entrada nulos en el servicio 'updateSchool'");
    }
    
    @DELETE
    @Path("/{liceoId}")
    @Produces(MediaType.APPLICATION_JSON)
    public SimplePostResultDTO eliminarLiceo(@PathParam("liceoId") String liceoId) throws Exception{
    	System.out.println("eliminarLiceo: " + liceoId);
    	if (liceoId!=null && !liceoId.isEmpty())
    		return liceoEJB.eliminarLiceo(liceoId);
    	throw new MatefunException("No se pudo eliminar liceo");
    }
    
    @GET
    @Path("/getAllLiceos")
    @Produces(MediaType.APPLICATION_JSON)
    public List<LiceoDTO> getAllLiceos(){
    	return liceoEJB.getAllLiceos();
    }

}

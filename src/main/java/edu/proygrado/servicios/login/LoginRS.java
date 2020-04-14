package edu.proygrado.servicios.login;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import edu.proygrado.dto.CredencialesDTO;
import edu.proygrado.dto.UsuarioDTO;
import edu.proygrado.ejb.LoginEJB;
import edu.proygrado.matefun.MatefunException;
import edu.proygrado.utils.Utils;

@Stateless
@Path("/login")
public class LoginRS{

	@EJB
    private LoginEJB loginEJB;
	
    @Inject
    private HttpServletRequest httpServletRequest;
	
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public UsuarioDTO login(CredencialesDTO credenciales) throws Exception{

    	if (!Utils.isNullOrEmpty(credenciales.getCedula()) && !Utils.isNullOrEmpty(credenciales.getPassword())) {
    		
    		Long startTime = (new Date()).getTime();
    		System.out.println("\nStart login: " + startTime);
    			UsuarioDTO ret = loginEJB.login(credenciales.getCedula(), credenciales.getPassword(), credenciales.getLiceo());
    		System.out.println("fin login, duracion: " + Math.floorDiv((new Date()).getTime()-startTime, 1000) + "s");
    		return ret;
    	}
    	else throw new MatefunException("Datos incorrectos para login");
    }
    
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/loginAdditionalInfo")
    public UsuarioDTO getLogindAdditionalInformation() throws Exception{
    	return loginEJB.getLogindAdditionalInformation(Utils.getToken(httpServletRequest));
    }
    
    @GET
    @Path("/datosDePrueba")
    public String cargarDatosDePrueba(){
    	return loginEJB.cargarDatosDePrueba();
    }
        
}

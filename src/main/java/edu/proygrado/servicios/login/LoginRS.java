package edu.proygrado.servicios.login;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import edu.proygrado.dto.CredencialesDTO;
import edu.proygrado.dto.UsuarioDTO;
import edu.proygrado.ejb.LoginEJB;
import edu.proygrado.matefun.MatefunException;
import edu.proygrado.matefun.MatefunUnauthorizedException;

@Stateless
@Path("/login")
public class LoginRS{

	@EJB
    private LoginEJB loginEJB;
	
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public UsuarioDTO login(CredencialesDTO credenciales) throws MatefunException{
    	System.out.println("LOGIN:"+loginEJB.toString());
    	UsuarioDTO user=  loginEJB.login(credenciales.getCedula(), credenciales.getPassword());
        return user;
    }
    
    @GET
    @Path("/datosDePrueba")
    public String cargarDatosDePrueba(){
    	return loginEJB.cargarDatosDePrueba();
    }
    
    

}

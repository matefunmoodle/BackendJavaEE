package edu.proygrado.matefun;

import java.io.IOException;
import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import edu.proygrado.ejb.LoginEJB;


@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

	@Inject
	LoginEJB login;

	@Override
	public void filter(ContainerRequestContext context) throws IOException {
		String path = context.getUriInfo().getPath();
		if(path.contains("login") || path.contains("getAllLiceos") || path.contains("getMatefunAdmin") ){
			return;
		}
		
		String authorizationHeader = context.getHeaderString(HttpHeaders.AUTHORIZATION);

		// Chequear existencia de cabezal
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			abort(context, Response.Status.UNAUTHORIZED);
			return;
		}

		// Extrae token de HTTP Authorization header
		String token = authorizationHeader.substring("Bearer ".length()).trim();

		if(!login.validarSesion(token)){
			abort(context, Response.Status.UNAUTHORIZED);
		}else{
			login.extendSession(token);
		}

	}

	private void abort(ContainerRequestContext context, Response.Status statusCode ) {
		context.abortWith(Response.status(statusCode).type(MediaType.TEXT_PLAIN)
				.entity("Error de seguridad").build());
	}

}
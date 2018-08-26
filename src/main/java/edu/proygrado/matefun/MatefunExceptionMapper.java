package edu.proygrado.matefun;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class MatefunExceptionMapper implements javax.ws.rs.ext.ExceptionMapper<MatefunException> {
	
	@Override
	public Response toResponse(MatefunException error) {
	    Response response;
	    if (error instanceof MatefunUnauthorizedException) {
	    	response = Response.status(Response.Status.UNAUTHORIZED)
	    			.entity(error.getMessage()).type("text/plain").build();
	    }else if (error instanceof MatefunForbiddenException){
	    	response = Response.status(Response.Status.FORBIDDEN)
	    			.entity(error.getMessage()).type("text/plain").build();
	    }else {
	    	response = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
	                .entity(error.getMessage()).type("text/plain").build();
	    }
	    return response;
	}
	
}

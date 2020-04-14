package edu.proygrado.servicios.cursos;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import edu.proygrado.dto.CoursesDTO;
import edu.proygrado.dto.CreateNewCourseInputDTO;
import edu.proygrado.dto.SimplePostResultDTO;
import edu.proygrado.ejb.CoursesEJB;
import edu.proygrado.matefun.MatefunException;
import edu.proygrado.utils.Utils;

@Stateless
@Path("/cursos")
public class CursosRS{

	@EJB
	private CoursesEJB coursesEJB;
   
    @Inject
    private HttpServletRequest httpServletRequest;
	
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<CoursesDTO> getAllCourses() throws Exception {
    	return coursesEJB.getAllCourses(Utils.getToken(httpServletRequest));
    }
    
    @DELETE
    @Path("/{cursoId}")
    @Produces(MediaType.APPLICATION_JSON)
    public SimplePostResultDTO deleteCourse(@PathParam("cursoId") long cursoId) throws Exception{
    	if ( cursoId != 0l )
    		return coursesEJB.deleteCourse(cursoId, Utils.getToken(httpServletRequest));
    	throw new MatefunException("valores de entrada nulos en el servicio 'deleteCourse'");
    }
    
    @POST
    @Path("/createNewCourse")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public SimplePostResultDTO createNewCourse(CreateNewCourseInputDTO input) throws Exception{
    	if (input != null)
    		return coursesEJB.createNewCourse(input, Utils.getToken(httpServletRequest));
    	throw new MatefunException("valores de entrada nulos en el servicio 'createNewCourse'");
    }
    
    @GET
    @Path("/user")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CoursesDTO> getAllUserCourses() throws Exception {
    	return coursesEJB.getAllUserCourses(Utils.getToken(httpServletRequest));
    }    
        
}

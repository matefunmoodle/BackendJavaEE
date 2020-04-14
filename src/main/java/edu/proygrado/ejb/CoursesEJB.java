package edu.proygrado.ejb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.json.JsonObject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import edu.proygrado.dto.CoursesDTO;
import edu.proygrado.dto.CreateNewCourseInputDTO;
import edu.proygrado.dto.MoodleCourseDTO;
import edu.proygrado.dto.SimplePostResultDTO;
import edu.proygrado.matefun.MatefunException;
import edu.proygrado.utils.MoodleConstants;
import edu.proygrado.utils.MoodleFunctions;
import edu.proygrado.utils.MoodleWS;

@Stateless
public class CoursesEJB {

	@PersistenceContext(unitName = "matefunDS")
	private EntityManager em;
	
	@EJB
	private InvitadoEJB invitadoEJB;
	
	@SuppressWarnings("serial")
	public JsonObject enrollInAExistentCourse(String roleid, String userid, String courseid, String token) throws Exception {
		Map<String, Object> paramsEnroll = new HashMap<String, Object>() {{
			put("enrolments[0][roleid]", roleid );
			put("enrolments[0][userid]", userid );
			put("enrolments[0][courseid]", courseid );
	    }};
	    return MoodleWS.GET(invitadoEJB, token, MoodleFunctions.enrol_manual_enrol_users, paramsEnroll);
	}

	public List<CoursesDTO> getAllUserCourses(String matefunToken) throws Exception {
		List<MoodleCourseDTO> userEnrolledCourses = invitadoEJB.getCoursesInfo(matefunToken).getEnrolledcourses();
		List<CoursesDTO> ret = new ArrayList<CoursesDTO>();
		for (MoodleCourseDTO c : userEnrolledCourses) 
			ret.add(new CoursesDTO(c));
	    return ret;
	}
	
	public List<CoursesDTO> getAllCourses(String token) throws Exception {
	    JsonObject result = MoodleWS.GET(invitadoEJB, token, MoodleFunctions.core_course_get_courses, new HashMap<String, Object>());
	    if (result.getBoolean(MoodleWS.IS_OK)) {
	    	List<CoursesDTO> resultList = new ArrayList<CoursesDTO>();
	    	for (int k=0; k<result.getJsonArray("result").size() ; k++)
	    		resultList.add(new CoursesDTO(result.getJsonArray("result").getJsonObject(k)));
	    	return resultList;
	    }
	    throw new MatefunException("No se puede obtener todos los cursos");
	}
	
	@SuppressWarnings("serial")
	public SimplePostResultDTO createNewCourse(CreateNewCourseInputDTO cursoData, String token) throws Exception {
		
		//parameters

		Map<String, Object> params = new HashMap<String, Object>() {{
			put("courses[0][fullname]", cursoData.getFullname() );
			put("courses[0][shortname]", cursoData.getShortname() );
			put("courses[0][categoryid]", 1 );
			put("courses[0][summary]", cursoData.getSummary() );
			put("courses[0][summaryformat]", 1 );
			put("courses[0][format]", cursoData.getFormat() );
			put("courses[0][showgrades]", 1 );
			put("courses[0][newsitems]", 5 );
			put("courses[0][maxbytes]", 0 );
			put("courses[0][showreports]", 0 );
			put("courses[0][groupmode]", 0 );
			put("courses[0][groupmodeforce]", 0 );
			put("courses[0][defaultgroupingid]", 0 );
	    }};
		
		JsonObject creationResponse = MoodleWS.POST(invitadoEJB, token, MoodleFunctions.core_course_create_courses, params);

		if (!creationResponse.getBoolean(MoodleWS.IS_OK)) {
			return new SimplePostResultDTO("{'courseCreation':'no', 'enroll':'no'}", false);
		}else {
			JsonObject result = creationResponse.getJsonArray("result").getJsonObject(0);
			Integer newCourseIdCreated = result.getInt("id");
			
		    JsonObject enrollResult = enrollInAExistentCourse(MoodleConstants.course_enroll_default_roleid, cursoData.getFirstProfessorUserId() , newCourseIdCreated.toString(), token);

		    if (enrollResult.getBoolean(MoodleWS.IS_OK)) {
				return new SimplePostResultDTO("{'courseCreation':'ok', 'enroll':'ok'}", true);
			}else {
				//enroll has failed, try to delete course
				Map<String, Object> paramsDelete = new HashMap<String, Object>() {{
					put("courseids[0]", newCourseIdCreated );
			    }};
				JsonObject deleteResult = MoodleWS.GET(invitadoEJB, token, MoodleFunctions.core_course_delete_courses, paramsDelete);				
				return new SimplePostResultDTO("Could not create course (enroll failed, course " + (deleteResult.getBoolean(MoodleWS.IS_OK) ? "" : "not ") + "delted)", false);
			}
		}
	}
	
	@SuppressWarnings("serial")
	public SimplePostResultDTO deleteCourse(Long courseid, String token) throws Exception {		
		Map<String, Object> paramsDelete = new HashMap<String, Object>() {{
			put("courseids[0]", courseid.toString() );
	    }};
	    JsonObject deleteResult = MoodleWS.GET(invitadoEJB, token, MoodleFunctions.core_course_delete_courses, paramsDelete);
		if (deleteResult.getBoolean(MoodleWS.IS_OK))
			return new SimplePostResultDTO("Curso eliminado con exito", true);
		else
			return new SimplePostResultDTO( deleteResult.getJsonObject("result").getJsonArray("warnings").getJsonObject(0).getString("warningcode") , false);		
	}

}

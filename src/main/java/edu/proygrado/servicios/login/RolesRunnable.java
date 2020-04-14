package edu.proygrado.servicios.login;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import javax.json.JsonObject;
import javax.ws.rs.client.ClientBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.proygrado.dto.MoodleCourseDTO;
import edu.proygrado.dto.MoodleCoursesInfoDTO;
import edu.proygrado.dto.MoodleRoleDTO;
import edu.proygrado.matefun.MatefunException;
import edu.proygrado.utils.MoodleConstants;
import edu.proygrado.utils.MoodleFunctions;
import edu.proygrado.utils.MoodleWS;
import edu.proygrado.utils.StringPair;
import edu.proygrado.utils.Utils;

public class RolesRunnable implements Runnable{

	MoodleCourseDTO course;
	Long userId;
	List<StringPair> allMoodleTokens;
	String moodleApiEndpoint;
	AtomicInteger atomI;
	Semaphore sem;

	public HashMap<Long, List<MoodleRoleDTO>> rolesByCourseId;
	
	public RolesRunnable(MoodleCourseDTO curso, List<StringPair> allMoodleTokens, Long userId, String moodleApiEndpoint, AtomicInteger atomI, Semaphore sem){
		this.course = curso;
		this.userId = userId;
		this.allMoodleTokens = allMoodleTokens;
		this.moodleApiEndpoint = moodleApiEndpoint;
		this.rolesByCourseId = null;
		this.atomI = atomI;
		this.sem = sem;
	}
	
	@Override
	public void run() {
		try {
			MoodleCoursesInfoDTO infoCursos = getCursesInfo(allMoodleTokens, userId, course.getId(), moodleApiEndpoint);
			
			this.course.setRoles(infoCursos!=null && infoCursos.getRoles()!=null ? infoCursos.getRoles() : new ArrayList<MoodleRoleDTO>());

			this.rolesByCourseId = new HashMap<Long,List<MoodleRoleDTO>>();
			this.rolesByCourseId.put( course.getId(), new ArrayList<MoodleRoleDTO>() );
			
			for (MoodleRoleDTO role : this.course.getRoles()) {
				if (MoodleConstants.rolesValidos.contains(role.getShortname().toLowerCase())) {
					this.rolesByCourseId.get(course.getId()).add(role);
				}
			}
			
		} catch (Exception e) {
			this.rolesByCourseId = null;
			this.course.setRoles(null);
			e.printStackTrace();
		}finally {
			//libero semaforo si corresponde
			if (this.atomI.addAndGet(-1)==0)
				this.sem.release();
		}
	}
	
	@SuppressWarnings("serial")
	public static MoodleCoursesInfoDTO getCursesInfo(List<StringPair> allTokens, Long userId, Long courseId, String moodleApiEndpoint) throws Exception {
		javax.ws.rs.client.Client c = ClientBuilder.newBuilder().build();
		List<MoodleCoursesInfoDTO> coursesInfo = null;
		JsonObject result = null;
	
		try {
			Map<String, Object> params = new HashMap<String, Object>() {{
												put("userlist[0][userid]", userId);
												put("userlist[0][courseid]", courseId);
											}};
			result = MoodleWS.GET(allTokens.get(0), moodleApiEndpoint, MoodleFunctions.core_user_get_course_user_profiles, params);
			if (result.getBoolean(MoodleWS.IS_OK)) {
				coursesInfo = (new ObjectMapper()).readValue(result.getJsonArray("result").toString(),
															new TypeReference<List<MoodleCoursesInfoDTO>>(){} );
				if (!Utils.isNullOrEmpty(coursesInfo) && coursesInfo.get(0)!=null && coursesInfo.get(0).getId() != null) {
					c.close();
					return coursesInfo.get(0);
				}else {
					throw new MatefunException("Error al obtener datos de los cursos Moodle, result: " + result);
				}
			}else {
				throw new MatefunException("Error al obtener datos de los cursos Moodle, result: " + result);
			}
		}finally {
			c.close();
		}
	} 
}

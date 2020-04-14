package edu.proygrado.servicios.login;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import javax.json.JsonArray;
import javax.json.JsonObject;
import edu.proygrado.dto.GrupoDTO;
import edu.proygrado.dto.MoodleCourseDTO;
import edu.proygrado.matefun.MatefunException;
import edu.proygrado.utils.MoodleFunctions;
import edu.proygrado.utils.MoodleWS;
import edu.proygrado.utils.StringPair;

public class GetCoursesRunnable implements Runnable{

	MoodleCourseDTO curso;
	Semaphore sem;
	Long userid;
	AtomicInteger atomI;
	String moodleApiEndpoint;
	StringPair moodleTokenToUse;
	
	public GetCoursesRunnable(MoodleCourseDTO curso, Semaphore sem, Long userId, AtomicInteger atomI, String moodleApiEndpoint, StringPair moodleTokenToUse) {
		this.curso = curso;
		this.sem = sem;
		this.userid = userId;
		this.atomI = atomI;
		this.moodleApiEndpoint = moodleApiEndpoint;
		this.moodleTokenToUse = moodleTokenToUse;
	}
	
	@SuppressWarnings("serial")
	@Override
	public void run() {
		try {
 			
			JsonObject result = MoodleWS.GET(this.moodleTokenToUse, moodleApiEndpoint, MoodleFunctions.core_group_get_course_user_groups, new HashMap<String, Object>() {{ put("courseid", curso.getId() ); put("userid", userid); }} );
			
			//JsonObject result = MoodleWS.setTokenAndPerformRequest(MoodleWS.GET, MoodleFunctions.core_group_get_course_user_groups, moodleApiEndpoint, new HashMap<String, Object>() {{ put("courseid", curso.getId() ); put("userid", userid); }}, this.moodleTokenToUse);
			if (result.getBoolean(MoodleWS.IS_OK)) {
				JsonArray grupos = result.getJsonObject("result").getJsonArray("groups");
				this.curso.setGrupos(new ArrayList<GrupoDTO>());
				for (int i = 0 ; i < grupos.size() ; i++) {
					JsonObject grupo = grupos.getJsonObject(i);
					curso.getGrupos().add(new GrupoDTO(grupo.getInt("id"), grupo.getString("name")));
				}
			}else {
				throw new MatefunException("error en core_group_get_course_user_groups: " + result);
			}

		} catch (Exception e) {
			this.curso.setGrupos(null);
			e.printStackTrace();
		}finally {
			//libero semaforo si corresponde
			if (this.atomI.addAndGet(-1)==0)
				sem.release();
		}
	}

}

package edu.proygrado.ejb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import javax.json.JsonArray;
import javax.json.JsonObject;
import edu.proygrado.dto.AlumnoDTO;
import edu.proygrado.dto.ArchivoDTO;
import edu.proygrado.dto.GrupoDTO;
import edu.proygrado.dto.UsuarioDTO;
import edu.proygrado.utils.MoodleFunctions;
import edu.proygrado.utils.MoodleWS;
import edu.proygrado.utils.Utils;

public class GroupRunnable implements Runnable{

	
	private AtomicInteger atomI;
	private InvitadoEJB invitadoEJB;
	private String matefunToken;

	public Long courseId;
	public Semaphore firsrBarrier;
	public Map<Integer, GrupoDTO> result; // <IdCurso , <idGrupo, Grupo> >
	public Exception errorDetail = null;
	
	public GroupRunnable(Long courseId, AtomicInteger atomI, Semaphore firsrBarrier, InvitadoEJB invitadoEJB, String matefunToken) {
		this.courseId = courseId;
		this.atomI = atomI;
		this.firsrBarrier = firsrBarrier;
		this.invitadoEJB = invitadoEJB;
		this.matefunToken = matefunToken;
	}
	
	private String obtenerNombreCurso(JsonArray enrolledcourses) {
		int i = 0;
		do {
			JsonObject ec = enrolledcourses.getJsonObject(i);
			if ( this.courseId.equals(new Long(ec.getInt("id"))))
				return !Utils.isNullOrEmpty(ec.getString("shortname")) ? ec.getString("shortname") : ec.getString("fullname");
		} while (++i < enrolledcourses.size());
		return "";
	}
	
	@SuppressWarnings("serial")
	@Override
	public void run() {
		
		try {
			
			Map<Integer, GrupoDTO> data = new HashMap<Integer, GrupoDTO>();
			this.errorDetail = null;
			
			Map<String, Object> params = new HashMap<String, Object>() {{
				put( "courseid" , courseId );
			}};
			
			JsonObject response = MoodleWS.GET(invitadoEJB, matefunToken, MoodleFunctions.core_enrol_get_enrolled_users, params, false);

			if (!response.getBoolean(MoodleWS.IS_OK)) {
				this.errorDetail = new Exception("Error en core_enrol_get_enrolled_users: " + response.toString());
				this.result = null;
			}else {
				JsonArray users = response.getJsonArray("result");
				String nombreCurso =  obtenerNombreCurso(users.getJsonObject(0).getJsonArray("enrolledcourses"));
				for (int i = 0; i < users.size(); ++i) {
					JsonObject user = users.getJsonObject(i);
					
					JsonArray grupos = user.getJsonArray("groups");
					for (int g = 0; g < grupos.size(); ++g) {
						JsonObject grp = grupos.getJsonObject(g);
						
						if (!data.containsKey(grp.getInt("id")))
							data.put(grp.getInt("id"), new GrupoDTO(2020, -1, grp.getString("name"), grp.getInt("id"), invitadoEJB.getLiceoId(matefunToken), Collections.synchronizedList(new ArrayList<ArchivoDTO>()), new ArrayList<AlumnoDTO>(), new ArrayList<UsuarioDTO>(), nombreCurso));
						data.get(grp.getInt("id")).getAlumnos().add(new AlumnoDTO(user, new ArrayList<ArchivoDTO>(), new ArrayList<ArchivoDTO>()));
					}
				}
				this.result = data;
			}
			
		} catch (Exception e) {
			this.errorDetail = e;
			this.result = null;
		}finally {
			if (this.atomI.addAndGet(-1)==0)
				this.firsrBarrier.release();
		}
	}
}

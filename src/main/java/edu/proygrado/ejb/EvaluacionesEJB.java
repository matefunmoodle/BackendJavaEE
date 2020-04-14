package edu.proygrado.ejb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import javax.ejb.Stateless;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import com.google.gson.Gson;

import edu.proygrado.dto.AlumnoDTO;
import edu.proygrado.dto.ArchivoDTO;
import edu.proygrado.dto.Assignment;
import edu.proygrado.dto.GrupoDTO;
import edu.proygrado.dto.MoodleCourseDTO;
import edu.proygrado.dto.SimplePostResultDTO;
import edu.proygrado.matefun.MatefunException;
import edu.proygrado.utils.MoodleConstants;
import edu.proygrado.utils.MoodleFunctions;
import edu.proygrado.utils.MoodleWS;
import edu.proygrado.utils.Utils;

@Stateless
public class EvaluacionesEJB {

	@PersistenceContext(unitName = "matefunDS")
	private EntityManager em;

	public void persist(Object object) {
		em.persist(object);
	}
	
	@SuppressWarnings("serial")
	public List<Assignment> getAllAssignments(String matefunToken, InvitadoEJB invitadoEJB) throws Exception{
		
		
		List<MoodleCourseDTO> moodleCourses = invitadoEJB.getCoursesInfo(matefunToken).getEnrolledcourses();
		
		List<Long> coursesIds = new LinkedList<Long>();
		
		for (MoodleCourseDTO moodleCourse : moodleCourses) {
			boolean soyAlumno = moodleCourse.getRoles().stream().anyMatch(r -> 
														MoodleConstants.rolesValidosAlumno.contains(r.getShortname()) ||
														MoodleConstants.rolesValidosAlumno.contains(r.getName())
													);
			if (soyAlumno)
				coursesIds.add(moodleCourse.getId());
		}
		
		if (coursesIds.isEmpty())
			throw new MatefunException("No hay assignments no vencidos en cursos en los que soy alumno");
		
		Map<String, Object> params = new HashMap<String, Object>() {{
			for (int i=0 ; i<coursesIds.size() ; i++)
				put("courseids["+i+"]", coursesIds.get(i) );
		}};
		
		
		JsonObject result = MoodleWS.GET(invitadoEJB, matefunToken, MoodleFunctions.mod_assign_get_assignments, params);
		
		if (result.getBoolean(MoodleWS.IS_OK)) {
			List<Assignment> output = new ArrayList<Assignment>();
			JsonArray courses = result.getJsonObject("result").getJsonArray("courses");
			Gson gson= new Gson();
			for (int c = 0 ; c < courses.size() ; c++ ) {
				JsonArray assignments = courses.getJsonObject(c).getJsonArray("assignments");
				for (int a = 0 ; a < assignments.size() ; a++ ) {
					Assignment ass = gson.fromJson(assignments.getJsonObject(a).toString(),Assignment.class);
					output.add(ass);					
				}
			}
			return output;
		}else{
			throw new MatefunException("falla en mod_assign_get_assignments: " + result);
		}
	}
	
	@SuppressWarnings("serial")
	public SimplePostResultDTO entregarArchivoParaEvaluacion(String matefunToken, InvitadoEJB invitadoEJB, ArchivosEJB archivosEJB, ArchivoDTO archivo, Integer assignmentId) throws Exception {

		Integer itemid = archivosEJB.createMoodleDraftFile(archivo, matefunToken, invitadoEJB, false, "/");
		System.out.println("itemID: " + itemid);
		Map<String, Object> params = new HashMap<String, Object>() {{
			put("assignmentid", assignmentId);
			put("plugindata[onlinetext_editor][text]", "sometext");
			put("plugindata[onlinetext_editor][format]", 0);
			put("plugindata[onlinetext_editor][itemid]", itemid);
			put("plugindata[files_filemanager]", itemid);
		}};
		
		
		JsonObject result = MoodleWS.POST(invitadoEJB, matefunToken, MoodleFunctions.mod_assign_save_submission, params, false);
		if (!result.getBoolean(MoodleWS.IS_OK))
			throw new MatefunException("error on mod_assign_save_submission:" + result.get("result").toString());
		return new SimplePostResultDTO("Archivo entregado con exito", true);
	}
	
	@SuppressWarnings("serial")
	public ArchivoDTO corregirAssignment(String matefunToken, InvitadoEJB invitadoEJB, ArchivosEJB archivosEJB, ArchivoDTO archivo) throws Exception {
		
		Map<String, Object> params = new HashMap<String, Object>() {{
			put("assignmentid", archivo.getEvaluacion().getAssignmentid()); //The assignment id to operate on
			put("userid", archivo.getEvaluacion().getUserid()); // The student id to operate on
			put("grade", archivo.getEvaluacion().getNota()!=null ? archivo.getEvaluacion().getNota() : 0); //The new grade for this user
			put("attemptnumber", archivo.getEvaluacion().getAttemptnumber() == null ? -1 : archivo.getEvaluacion().getAttemptnumber() ); //The attempt number (-1 means latest attempt)
			put("addattempt", archivo.getEvaluacion().getAddattempt() == null ? 0 : Utils.boolToInt(archivo.getEvaluacion().getAddattempt())); //Allow another attempt if the attempt reopen method is manual
			put("workflowstate", archivo.getEvaluacion().getWorkflowstate() == null ? "graded" : archivo.getEvaluacion().getWorkflowstate()); //The next marking workflow state
			put("applytoall", archivo.getEvaluacion().getApplytoall() == null ? 1 : Utils.boolToInt(archivo.getEvaluacion().getApplytoall()) ); //If true, this grade will be applied to all members of the group (for group assignments).
			put("plugindata[assignfeedbackcomments_editor][text]", archivo.getEvaluacion().getDescripcion()==null ? "Sin comentarios." : archivo.getEvaluacion().getDescripcion());
			put("plugindata[assignfeedbackcomments_editor][format]", 1);
		}};
		
		JsonObject result = MoodleWS.GET(invitadoEJB, matefunToken, MoodleFunctions.mod_assign_save_grade, params, false);
		if (!result.getBoolean(MoodleWS.IS_OK))
			throw new MatefunException("error en mod_assign_save_grade:" + result.get("result").toString());
		
		archivo.getEvaluacion().setCorregido(true);
		archivo.getEvaluacion().setWorkflowstate("Corregido");
		return archivo;
	}
	
	@SuppressWarnings("serial")
	public JsonObject getSubmissionStatus(String matefunToken, InvitadoEJB invitadoEJB, ArchivosEJB archivosEJB, Integer assignid, Integer userid, Integer groupid) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>() {{
			put("assignid", assignid);
			if (userid!=null ) put("userid", userid);
			if (groupid!=null) put("groupid", groupid);
		}};
		
		JsonObject result = MoodleWS.GET(invitadoEJB, matefunToken, MoodleFunctions.mod_assign_get_submission_status , params, false);
		if (result.getBoolean(MoodleWS.IS_OK)) {
			JsonObject lastattempt = result.getJsonObject("result").getJsonObject("lastattempt");
			System.out.println("lastattempt: " + lastattempt);
			return lastattempt;
		}
		throw new MatefunException ("Error en mod_assign_get_submission_status");
		
	}
	
	@SuppressWarnings("unchecked")
	public List<GrupoDTO> getAssignmentsPorGrupos(InvitadoEJB invitadoEJB, String matefunToken, List<Long> courseIds) throws Exception{
		
		AtomicInteger atomI = new AtomicInteger(courseIds.size() + 1);
		Semaphore firsrBarrier = new Semaphore(0);
		
		List<GroupRunnable> lgr = new ArrayList<GroupRunnable>();
		for (Long courseId : courseIds) {
			GroupRunnable gr = new GroupRunnable(courseId,  atomI, firsrBarrier, invitadoEJB, matefunToken);
			(new Thread(gr)).start();
			lgr.add(gr);
		}
		AssignmentRunnable ar = new AssignmentRunnable(courseIds, atomI, firsrBarrier, invitadoEJB, matefunToken, true);
		(new Thread(ar)).start();
		
		//System.out.println("GRUPAL: first barrier pedida");
		firsrBarrier.acquire();
		//System.out.println("GRUPAL: first barrier adquirida");
		
		// Check for errors ...
		if (ar.errorDetail != null) {
			ar.errorDetail.printStackTrace();
			throw ar.errorDetail;
		}
		for (GroupRunnable gr : lgr) {
			if (gr.errorDetail!=null) {
				gr.errorDetail.printStackTrace();
				throw gr.errorDetail;
			}
		}
		
		List<CourseProcessorRunnable> lcpr = new ArrayList<CourseProcessorRunnable>();
		
		atomI = new AtomicInteger(courseIds.size());
		Semaphore secondBarrier = new Semaphore(0);
		for (GroupRunnable gr : lgr) {
			CourseProcessorRunnable cpr = new CourseProcessorRunnable(invitadoEJB, matefunToken, gr, ar.result.get(gr.courseId), atomI , secondBarrier);
			(new Thread(cpr)).start();
			lcpr.add(cpr);
		}

		//System.out.println("GRUPAL: secondBarrier pedida");
		secondBarrier.acquire();
		//System.out.println("GRUPAL: secondBarrier adquirida");
		
		
		for (CourseProcessorRunnable cpr : lcpr) {
			if (cpr.errorDetail != null) {
				cpr.errorDetail.printStackTrace();
				throw cpr.errorDetail; 
			}
		}
		
		List<GrupoDTO> ret = new ArrayList<GrupoDTO>();
		
		for (CourseProcessorRunnable cpr : lcpr) {
			ret.addAll( (List<GrupoDTO>) cpr.result );
		}
		
		//System.out.println("GRUPAL: Fin metodo OK");
		return ret;
	}
	
	private static boolean esAlumno(JsonObject user) {
		if (user.containsKey("roles")) {
			JsonArray roles = user.getJsonArray("roles");
			for (int i=0 ; i<roles.size() ; i++)
				if (roles.getJsonObject(i).getString("shortname").equals("student"))
					return true;
		}
		return false;
	}
	
	@SuppressWarnings({ "serial", "unchecked" })
	public Map<String, List<AlumnoDTO>> getAssignmentsPorAlumnos(InvitadoEJB invitadoEJB, String matefunToken, List<Long> courseIds) throws Exception{
		
		Semaphore firsrBarrier = new Semaphore(0);
		AssignmentRunnable ar = new AssignmentRunnable(courseIds, new AtomicInteger(1), firsrBarrier, invitadoEJB, matefunToken, false);
		(new Thread(ar)).start();
		
		Map<Long, Object> resultado = new ConcurrentHashMap<Long,Object>();
		
		//System.out.println("comienza: inicia parallel stream");
		courseIds.parallelStream().forEach( courseId -> {
			try {
				
				JsonObject response = MoodleWS.GET(invitadoEJB, matefunToken, MoodleFunctions.core_enrol_get_enrolled_users, new HashMap<String, Object>() {{ put( "courseid" , courseId ); }}, false);
				if (!response.getBoolean(MoodleWS.IS_OK)) {
					throw new Exception("Error en core_enrol_get_enrolled_users: " + response.toString());
				}else {
					List<AlumnoDTO> listaCursoActual = new ArrayList<AlumnoDTO>();
					JsonArray users = response.getJsonArray("result");
					for (int i = 0; i < users.size(); ++i) {
						if (!esAlumno(users.getJsonObject(i)))
							continue;
						listaCursoActual.add(new AlumnoDTO(users.getJsonObject(i), new ArrayList<ArchivoDTO>(), new ArrayList<ArchivoDTO>()));
					}
					resultado.put(courseId, listaCursoActual);
					//System.out.println("stream OK");
				}
			} catch (Exception e) {
				//System.out.println("stream exp");
				resultado.put(courseId, e);
			}
		});
		
		//System.out.println("INDIVIDUAL: Termina parallel stream");
		
		Map<String, List<AlumnoDTO>> alumnosPorCurso = new HashMap<String,List<AlumnoDTO>>();
		for (Map.Entry<Long, Object> entry : resultado.entrySet()) {
			if (entry.getValue() instanceof Exception)
				throw (Exception)entry.getValue();
			alumnosPorCurso.put(entry.getKey().toString(), Collections.synchronizedList((List<AlumnoDTO>)entry.getValue()));
		}
		
		//System.out.println("INDIVIDUAL: firsrBarrier pedida");
		firsrBarrier.acquire();
		//System.out.println("INDIVIDUAL: firsrBarrier adquirida");
		
		// Check for errors ...
		if (ar.errorDetail != null)
			throw ar.errorDetail;
		

		List<CourseProcessorRunnable> lcpr = new ArrayList<CourseProcessorRunnable>();
		AtomicInteger atomI = new AtomicInteger(courseIds.size());
		Semaphore secondBarrier = new Semaphore(0);
		for (Long courseId : courseIds) {
			CourseProcessorRunnable cpr = new CourseProcessorRunnable (invitadoEJB, matefunToken, alumnosPorCurso.get(courseId.toString()), ar.result.get(courseId), atomI, secondBarrier);
			(new Thread(cpr)).start();
			lcpr.add(cpr);
		}

		//System.out.println("GRUPAL: secondBarrier pedida");
		secondBarrier.acquire();
		//System.out.println("GRUPAL: secondBarrier adquirida");
		
		for (CourseProcessorRunnable cpr : lcpr) {
			if (cpr.errorDetail != null) {
				cpr.errorDetail.printStackTrace();
				throw cpr.errorDetail; 
			}
		}		
		
		return alumnosPorCurso;
	}
	
}

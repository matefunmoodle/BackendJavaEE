package edu.proygrado.ejb;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import edu.proygrado.utils.MoodleFunctions;
import java.util.concurrent.Semaphore;
import edu.proygrado.utils.MoodleWS;
import edu.proygrado.utils.Utils;
import javax.json.JsonObject;
import javax.json.JsonArray;

public class AssignmentRunnable implements Runnable{

	private List<Long> courseIds;
	private AtomicInteger atomI;
	private InvitadoEJB invitadoEJB;
	private String matefunToken;
	private boolean groupAssignments;

	public Semaphore firsrBarrier;
	public Map<Long, List<JsonObject>> result; // <courseId , List<Assignments>>
	public Exception errorDetail;
	
	public AssignmentRunnable (List<Long> courseIds, AtomicInteger atomI, Semaphore firsrBarrier, InvitadoEJB invitadoEJB, String matefunToken, boolean groupAssignments) {
		this.courseIds = courseIds;
		this.atomI = atomI;
		this.firsrBarrier = firsrBarrier;
		this.invitadoEJB = invitadoEJB;
		this.matefunToken = matefunToken;
		this.groupAssignments = groupAssignments;
	}
	
	@SuppressWarnings("serial")
	@Override
	public void run() {

		this.result = new HashMap<Long, List<JsonObject>>();
		this.errorDetail = null;
		
		try {

			Map<String, Object> params = new HashMap<String, Object>() {{
				for (int i=0 ; i<courseIds.size() ; i++ )
					put( "courseids["+i+"]" , courseIds.get(i) );
			}};
			
			JsonObject response = MoodleWS.GET(invitadoEJB, matefunToken, MoodleFunctions.mod_assign_get_assignments, params, false);
			if (!response.getBoolean(MoodleWS.IS_OK)) {
				this.errorDetail = new Exception("Error en mod_assign_get_assignments: " + response.toString());
				this.result = null;
			}else {
				JsonArray courses = response.getJsonObject("result").getJsonArray("courses");
				for (int c = 0 ; c < courses.size() ; c++ ) {
					Long key = new Long(courses.getJsonObject(c).getInt("id"));
					this.result.put(key, new ArrayList<JsonObject>());
					JsonArray assignments = courses.getJsonObject(c).getJsonArray("assignments");
					for (int idx = 0 ; idx < assignments.size(); idx++ ) {
						if (!Utils.isValidAssignment(assignments.getJsonObject(idx), this.groupAssignments))
							continue;
						this.result.get(key).add(assignments.getJsonObject(idx));
					}
				}
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

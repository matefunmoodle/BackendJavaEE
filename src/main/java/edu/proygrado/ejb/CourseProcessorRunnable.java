package edu.proygrado.ejb;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Semaphore;
import javax.json.JsonArray;
import javax.json.JsonObject;
import edu.proygrado.dto.AlumnoDTO;
import edu.proygrado.dto.ArchivoDTO;
import edu.proygrado.dto.EvaluacionDTO;
import edu.proygrado.dto.GrupoDTO;
import edu.proygrado.dto.TipoArchivo;
import edu.proygrado.modelo.EstadoArchivo;
import edu.proygrado.utils.MoodleFunctions;
import java.util.concurrent.atomic.AtomicInteger;
import edu.proygrado.utils.MoodleWS;
import edu.proygrado.utils.Pair;
import edu.proygrado.utils.Utils;

public class CourseProcessorRunnable implements Runnable {
	
	private InvitadoEJB invitadoEJB;
	private String matefunToken;
	private Map<Integer, GrupoDTO> grupos;
	private List<AlumnoDTO> alumnos;
	private List<JsonObject> allAssignments;
	private AtomicInteger atomI;
	private Semaphore secondBarrier;
	private boolean esGrupal;
	
	@SuppressWarnings("rawtypes")
	public List result;
	public Exception errorDetail = null;
	
	public CourseProcessorRunnable (InvitadoEJB invitadoEJB, String matefunToken, GroupRunnable gr, List<JsonObject> allAssignments, AtomicInteger atomI, Semaphore secondBarrier) {
		this.invitadoEJB = invitadoEJB;
		this.matefunToken = matefunToken;
		this.grupos = gr.result;
		this.allAssignments = allAssignments;
		this.atomI = atomI;
		this.secondBarrier = secondBarrier;
		this.esGrupal = true;
	}
	
	public CourseProcessorRunnable (InvitadoEJB invitadoEJB, String matefunToken, List<AlumnoDTO> alumnos, List<JsonObject> allAssignments, AtomicInteger atomI, Semaphore secondBarrier) {
		this.invitadoEJB = invitadoEJB;
		this.matefunToken = matefunToken;
		this.alumnos = alumnos;
		this.allAssignments = allAssignments;
		this.atomI = atomI;
		this.secondBarrier = secondBarrier;
		this.esGrupal = false;
	}
	
	@SuppressWarnings({ "serial", "unchecked" })
	@Override
	public void run() {
		this.errorDetail = null;
		try {
			
			List<Pair<JsonObject, Object>> allData = new ArrayList<Pair<JsonObject, Object>>();
			
			if (this.esGrupal) {
				for (GrupoDTO grupo : grupos.values()) {
					for (JsonObject assignment : allAssignments) {
						allData.add( new Pair<JsonObject, Object>(assignment, grupo) );
					}
				}
			}else {
				for (AlumnoDTO alumno : this.alumnos) {
					for (JsonObject assignment : allAssignments) {
						allData.add( new Pair<JsonObject, Object>(assignment, alumno ) );
					}
				}
			}
			
			//System.out.println((this.esGrupal ? "GRUPAL" : "INDIVIDUAL") + ": Inicio paralell stream");
			AtomicInteger countExp = new AtomicInteger(0);
			
			allData.parallelStream().forEach( pair -> {
				JsonObject assignment = pair.getFirst();
				
				//usado en caso grupal
				GrupoDTO grupo = this.esGrupal ? (GrupoDTO)pair.getSecond() : null;
				//usado en caso no grupal
				AlumnoDTO alumno = this.esGrupal ? ((GrupoDTO)pair.getSecond()).getAlumnos().get(0) : ((AlumnoDTO)pair.getSecond());
				
				Map<String, Object> params = new HashMap<String, Object>() {{
					put( "assignid", assignment.getInt("id"));
					put( "userid", alumno.getMoodleUserId());
					put( "groupid", 0);
				}};
				JsonObject response = null;
				try {
					response = MoodleWS.GET(invitadoEJB, matefunToken, MoodleFunctions.mod_assign_get_submission_status, params, false);					
					//System.out.println(String.format((this.esGrupal ? "GRUPAL" : "INDIVIDUAL") + ": [assign:%d] Response: %s",  assignment.getInt("id"), response));
					if (!response.getBoolean(MoodleWS.IS_OK)) {
						this.errorDetail = new Exception("Error en mod_assign_get_assignments: " + response.toString());
						this.result = null;
					}else {

						Integer idDocenteQueCorrigio = null;
						String nota = null;
						Integer fechaCorreccion = null;
						String descripcion = null; 
						
						JsonObject result = response.getJsonObject("result");
						if (result.containsKey("lastattempt")) {
							JsonObject lastattempt = result.getJsonObject("lastattempt");
							
							boolean corregido = lastattempt.containsKey("graded") && lastattempt.getBoolean("graded");
							if (corregido) {
								if (result.containsKey("feedback")) {
									JsonObject feedback = result.getJsonObject("feedback");
									if (feedback.containsKey("grade")) {
										JsonObject grade = feedback.getJsonObject("grade");
										idDocenteQueCorrigio = grade.getInt("grader");
										nota = grade.getString("grade");
										fechaCorreccion = grade.getInt("timemodified");
									}
									if (feedback.containsKey("plugins")) {
										JsonObject commentsObject = Utils.filterByParam(feedback.getJsonArray("plugins"), "type", "comments");
										if (commentsObject!=null && commentsObject.containsKey("editorfields")) {
											JsonObject submissionCommentsObject = Utils.filterByParam(commentsObject.getJsonArray("editorfields"), "name", "comments");
											descripcion = submissionCommentsObject!=null ? submissionCommentsObject.getString("text") : null;
										}
									}
								}
							}

							Long userId = lastattempt.containsKey("submission") && lastattempt.getJsonObject("submission").containsKey("userid") ?
										new Long( lastattempt.getJsonObject("submission").getInt("userid")) : null;
										
							String submitionToUseStr =  this.esGrupal ? "teamsubmission" : "submission";
							if (lastattempt.containsKey(submitionToUseStr)) {
								JsonObject submition = lastattempt.getJsonObject(submitionToUseStr);
								if (submition.containsKey("plugins")) {
									JsonArray plugins = submition.getJsonArray("plugins");
									JsonArray fileareas = Utils.filterByParam(plugins, "type", "file").getJsonArray("fileareas");
									if (fileareas!=null) {
										JsonArray allFilesSubmitted = Utils.filterByParam(fileareas, "area", "submission_files").getJsonArray("files");
										
										for (short f=0; f<allFilesSubmitted.size(); f++) {
											JsonObject file = allFilesSubmitted.getJsonObject(f);
											
											ArchivoDTO nuevoArchivo = new ArchivoDTO(
																(new Random()).nextLong(), //id
																file.getString("filename"), //nombre
																new Date(file.getInt("timemodified")), //fechaCreacion
																null, //contenido
																invitadoEJB.getUsuario(matefunToken).getCedula(), //cedulaCreador
																false, //editable
																false, //eliminado
																-1, //padreId
																-1l, //archivoOrigenId
																false, //directorio
																corregido ? EstadoArchivo.Corregido.toString() : EstadoArchivo.Entregado.toString(), //estado
																file.getString("fileurl"), //moodleFilePath
																new EvaluacionDTO(new Long(assignment.getInt("id")), userId, nota, -1l, false, corregido ? EstadoArchivo.Corregido.toString() : EstadoArchivo.Entregado.toString(), descripcion, corregido, idDocenteQueCorrigio, fechaCorreccion, assignment.getString("name"), this.esGrupal),
																TipoArchivo.ENTREGA, //tipo
																false, //puedeCompartir
																"" //directorioMatefun
																);
											
											if (this.esGrupal)
												grupo.getArchivos().add(nuevoArchivo);
											else
												alumno.getArchivos().add(nuevoArchivo);
										}
									}
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					if (countExp.addAndGet(1)==1) {
						this.errorDetail = e;
						this.result = null;
					}
				}
			});

			//System.out.println((this.esGrupal ? "GRUPAL" : "INDIVIDUAL") + ": Fin paralell stream");
			if (this.errorDetail==null) {
				// Si no hubo error en el paralell foreach ...
				this.result = new ArrayList<Object>();
				result.addAll( this.esGrupal ? grupos.values() : this.alumnos);
			}
		}catch (Exception e) {
				this.result = null;
				this.errorDetail = e;
		}finally {
			if (this.atomI.addAndGet(-1)==0)
				this.secondBarrier.release();
		}	
	}
}

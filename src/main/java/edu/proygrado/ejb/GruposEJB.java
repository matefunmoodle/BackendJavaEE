package edu.proygrado.ejb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import edu.proygrado.dto.AlumnoDTO;
import edu.proygrado.dto.CoursesDTO;
import edu.proygrado.dto.GrupoDTO;
import edu.proygrado.dto.MoodleCourseDTO;
import edu.proygrado.dto.UsuarioDTO;
import edu.proygrado.matefun.MatefunException;
import edu.proygrado.modelo.Archivo;
import edu.proygrado.modelo.Grupo;
import edu.proygrado.modelo.GrupoPK;
import edu.proygrado.utils.MoodleFunctions;
import edu.proygrado.utils.MoodleWS;
import edu.proygrado.utils.Utils;

@Stateless
public class GruposEJB {

	@PersistenceContext(unitName = "matefunDS")
	private EntityManager em;

	public void agregarArchivoGrupo(long archivoId, GrupoPK grupoPK) throws Exception {
		Grupo grupo = em.find(Grupo.class, grupoPK);
		if (grupo == null) {
			throw new Exception(
					"No existe el grupo " + grupoPK.getAnio() + " " + grupoPK.getGrado() + grupoPK.getGrupo());
		}
		Archivo archivo = em.find(Archivo.class, archivoId);
		if (archivo == null) {
			throw new Exception("No existe el archivo" + archivoId);
		}
		if (!grupo.getArchivos().contains(archivo)) {
			grupo.addArchivo(archivo);
		}
	}
	
	
	private List<GrupoDTO> obtenerTodosLosGruposDeUnCurso(InvitadoEJB invitadoEJB , String matefunToken, Map<String, Object> params, String... courseName) throws Exception{
		JsonObject response = MoodleWS.GET(invitadoEJB, matefunToken, MoodleFunctions.core_group_get_course_groups, params);
		List<GrupoDTO> gruposRet = new ArrayList<GrupoDTO>();
		if (!response.getBoolean(MoodleWS.IS_OK)) {
			throw new MatefunException("no se puede obtener los grupos de un curso (obtenerTodosLosGruposDeUnCurso) -> " + response.toString());
		}else {
			JsonArray grupos = response.getJsonArray("result");
			for (int i = 0; i < grupos.size(); ++i) {
				JsonObject grupo = grupos.getJsonObject(i);
				gruposRet.add(new GrupoDTO(-1, -1, grupo.getString("name"), grupo.getInt("id"), invitadoEJB.getLiceoId(matefunToken), null, new ArrayList<AlumnoDTO>(), new ArrayList<UsuarioDTO>() , courseName));
			}
			return gruposRet;
		}		
	}
	
	private String extractRoles(JsonObject user) {
		JsonArray roles = user.getJsonArray("roles");
		String[] rolesArr = new String[roles.size()];
		for (int j = 0 ; j < roles.size() ; j++)
			rolesArr[j] = !Utils.isNullOrEmpty( roles.getJsonObject(j).getString("shortname") ) ?
							roles.getJsonObject(j).getString("shortname") :
							roles.getJsonObject(j).getString("name");
		
		return String.join(", ", rolesArr);
	}
	
	@SuppressWarnings("serial")
	public CoursesDTO getCourseGroupsAndMembers(Long courseid, String matefunToken, InvitadoEJB invitadoEJB) throws Exception {
		//TODO: esto hay que arreglar pensando en que puedo ser alumno y docente a la vez (en el mismo courseid?)
		Map<String, Object> params = new HashMap<String, Object>() {{ put("courseid", courseid); }};
		
		MoodleCourseDTO moodleCourse = (invitadoEJB.getCoursesInfo(matefunToken).getEnrolledcourses().stream()
				  .filter(ec -> ec.getId().equals(courseid) )
				  .findFirst()
				  .orElse(null));
		
		
		CoursesDTO ret = new CoursesDTO(courseid,
										moodleCourse.getShortname(),
										moodleCourse.getFullname(),
										Utils.isNullOrEmpty(moodleCourse.getFullname()) ? moodleCourse.getShortname() : moodleCourse.getFullname(),
										new ArrayList<UsuarioDTO>(),
										obtenerTodosLosGruposDeUnCurso(invitadoEJB, matefunToken, params, !Utils.isNullOrEmpty(moodleCourse.getShortname()) ? moodleCourse.getShortname() : moodleCourse.getFullname()) );

		JsonObject response = MoodleWS.GET(invitadoEJB, matefunToken, MoodleFunctions.core_enrol_get_enrolled_users, params, true);
		if (!response.getBoolean(MoodleWS.IS_OK)) {
			throw new MatefunException("error en llamada core_enrol_get_enrolled_users: -> " + response.toString());
		}else{

			JsonArray users = response.getJsonArray("result");
			for (int i = 0; i < users.size(); ++i) {
				JsonObject user = users.getJsonObject(i);
				
				UsuarioDTO usuario = new UsuarioDTO();

				usuario.setCedula(null);
				usuario.setMoodleUserId(new Long(user.getInt("id")));
				usuario.setNombre( user.containsKey("firstname") ? user.getString("firstname") : (user.containsKey("fullname") ? user.getString("fullname").split(" ")[0] : "NO-FIRSTNAME") );
				usuario.setApellido( user.containsKey("lastname") ? user.getString("lastname") : (user.containsKey("fullname") ? user.getString("fullname").split(" ")[1] : "NO-LASTNAME") );
				usuario.setToken(null);
				usuario.setConfiguracion(null);
				usuario.setTipo(extractRoles(user));
				
				JsonArray gruposUsuario = user.getJsonArray("groups");
				for (int g=0 ; g<gruposUsuario.size() ; g++ ) {
					int idGrupo = gruposUsuario.getJsonObject(g).getInt("id");
					GrupoDTO grupoUsuario = ret.getGrupos().stream()
							  .filter(gr -> gr.getGrupoId() == idGrupo )
							  .findFirst()
							  .orElse(null);

					grupoUsuario.getParticipantes().add(usuario);
					if (usuario.getTipo().indexOf("student") != -1) {
						AlumnoDTO a = new AlumnoDTO();
						a.setMoodleUserId(usuario.getMoodleUserId());
						a.setCedula(usuario.getCedula());
						a.setNombre(usuario.getNombre());
						a.setApellido(usuario.getApellido());
						grupoUsuario.getAlumnos().add(a);
					}

				}

				ret.getParticipantes().add(usuario);
			}			
		}
		return ret;
	}

}

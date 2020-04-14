package edu.proygrado.matefun;

import java.util.Date;
import java.util.List;

import edu.proygrado.dto.MoodleCoursesInfoDTO;
import edu.proygrado.modelo.Archivo;
import edu.proygrado.modelo.Usuario;
import edu.proygrado.utils.StringPair;

public class InvitadoSesion {
	private Date creada;
	private List<Archivo> archivos;
	private List<Archivo> archivosCompartidos;
	private List<Archivo> archivosGrupo;
	private Usuario usuario;
	private String moodleApiEndpoint;
	private Long liceoId;
	
	private MoodleCoursesInfoDTO coursesInfo;
	private Long moodleWebServicesUserId;
	private List<StringPair> allMoodleTokens;
	
	public InvitadoSesion(Usuario usuario, String moodleApiEndpoint, List<StringPair> allMoodleTokens, Long liceoId, MoodleCoursesInfoDTO coursesInfo, Long moodleWebServicesUserId) {
		this.creada = new Date();
		this.liceoId = liceoId;
		this.usuario = usuario;
		this.moodleApiEndpoint = moodleApiEndpoint;
		this.allMoodleTokens = allMoodleTokens;
		this.coursesInfo = coursesInfo;
		this.moodleWebServicesUserId = moodleWebServicesUserId;
	}
	
	public List<StringPair> getAllMoodleTokens() {
		return allMoodleTokens;
	}

	public void setAllMoodleTokens(List<StringPair> allMoodleTokens) {
		this.allMoodleTokens = allMoodleTokens;
	}
	
	public Date getCreada() {
		return creada;
	}
	public void setCreada(Date creada) {
		this.creada = creada;
	}
	public List<Archivo> getArchivos() {
		return archivos;
	}
	public void setArchivos(List<Archivo> archivos) {
		this.archivos = archivos;
	}
	public List<Archivo> getArchivosCompartidos() {
		return archivosCompartidos;
	}
	public void setArchivosCompartidos(List<Archivo> archivosCompartidos) {
		this.archivosCompartidos = archivosCompartidos;
	}
	public List<Archivo> getArchivosGrupo() {
		return archivosGrupo;
	}
	public void setArchivosGrupo(List<Archivo> archivosGrupo) {
		this.archivosGrupo = archivosGrupo;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getMoodleApiEndpoint() {
		return moodleApiEndpoint;
	}

	public void setMoodleApiEndpoint(String moodleApiEndpoint) {
		this.moodleApiEndpoint = moodleApiEndpoint;
	}
	
	public long getMoodleUserId() {
		return coursesInfo.getId();
	}

	public Long getLiceoId() {
		return liceoId;
	}

	public void setLiceoId(Long liceoId) {
		this.liceoId = liceoId;
	}

	public long getMoodleWebServicesUserId() {
		return moodleWebServicesUserId;
	}
	
	public void setMoodleWebServicesUserId(Long moodleWebServicesUserId) {
		this.moodleWebServicesUserId = moodleWebServicesUserId;
	}

	public MoodleCoursesInfoDTO getCoursesInfo() {
		return coursesInfo;
	}

	public void setCoursesInfo(MoodleCoursesInfoDTO coursesInfo) {
		this.coursesInfo = coursesInfo;
	}


}

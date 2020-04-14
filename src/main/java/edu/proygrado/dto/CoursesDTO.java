package edu.proygrado.dto;

import java.util.List;

import javax.json.JsonObject;

public class CoursesDTO {
	
	Long id;
	String shortname;
	String fullname;
	String displayname;
	List<UsuarioDTO> participantes;
	List<GrupoDTO> grupos;
	
	public CoursesDTO() {
		super();
	}
	
	public CoursesDTO(MoodleCourseDTO c) {
		this.id = c.getId();
		this.shortname = c.getShortname();
		this.fullname = c.getFullname();
		this.grupos = c.getGrupos();
		this.participantes = null;
	}
	
	public CoursesDTO(JsonObject obj) {
		super();
		this.id = (long) obj.getInt("id");
		this.shortname = obj.getString("shortname");
		this.fullname = obj.getString("fullname");
		this.displayname = obj.getString("displayname");
	}
	
	public CoursesDTO(Long id, String shortname, String fullname, String displayname, List<UsuarioDTO> participantes, List<GrupoDTO> grupos) {
		super();
		this.id = id;
		this.shortname = shortname;
		this.fullname = fullname;
		this.displayname = displayname;
		this.participantes = participantes;
		this.grupos = grupos;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getShortname() {
		return shortname;
	}
	public void setShortname(String shortname) {
		this.shortname = shortname;
	}
	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	public String getDisplayname() {
		return displayname;
	}
	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}

	public List<UsuarioDTO> getParticipantes() {
		return participantes;
	}

	public void setParticipantes(List<UsuarioDTO> participantes) {
		this.participantes = participantes;
	}

	public List<GrupoDTO> getGrupos() {
		return grupos;
	}

	public void setGrupos(List<GrupoDTO> grupos) {
		this.grupos = grupos;
	}
	

}

package edu.proygrado.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MoodleCourseDTO {
	private Long id;
	private String fullname;
	private String shortname;
	private List<GrupoDTO> grupos;
	private List<MoodleRoleDTO> roles;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	public String getShortname() {
		return shortname;
	}
	public void setShortname(String shortname) {
		this.shortname = shortname;
	}
	public List<GrupoDTO> getGrupos() {
		return grupos;
	}
	public void setGrupos(List<GrupoDTO> grupos) {
		this.grupos = grupos;
	}
	public List<MoodleRoleDTO> getRoles() {
		return roles;
	}
	public void setRoles(List<MoodleRoleDTO> roles) {
		this.roles = roles;
	}
	
}

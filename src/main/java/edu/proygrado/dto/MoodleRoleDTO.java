package edu.proygrado.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MoodleRoleDTO {
	private Long roleid;
	private String name;
	private String shortname;
	private Long sortorder;
	
	public Long getRoleid() {
		return roleid;
	}
	public void setRoleid(Long roleid) {
		this.roleid = roleid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getShortname() {
		return shortname;
	}
	public void setShortname(String shortname) {
		this.shortname = shortname;
	}
	public Long getSortorder() {
		return sortorder;
	}
	public void setSortorder(Long sortorder) {
		this.sortorder = sortorder;
	}
}

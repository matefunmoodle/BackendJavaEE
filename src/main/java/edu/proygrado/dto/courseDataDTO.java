package edu.proygrado.dto;

import java.io.Serializable;

public class courseDataDTO implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Long courseId;
	private String courseName;
	private Long roleId;
	private String roleName;
	
	public courseDataDTO() {
	}
	
	public courseDataDTO(Long courseId, String courseName, Long roleId, String roleName) {
		this.courseId = courseId;
		this.courseName = courseName;
		this.roleId = roleId;
		this.roleName = roleName;
	}

	public Long getCourseId() {
		return courseId;
	}

	public void setCourseId(Long courseId) {
		this.courseId = courseId;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

}

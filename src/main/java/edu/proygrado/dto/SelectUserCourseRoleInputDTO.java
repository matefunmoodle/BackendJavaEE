package edu.proygrado.dto;

public class SelectUserCourseRoleInputDTO {

	String token;
	String roleId;
	String courseId;
	
	public SelectUserCourseRoleInputDTO() {}
	
	public SelectUserCourseRoleInputDTO(String roleId, String courseId) {
		this.roleId = roleId;
		this.courseId = courseId;
	}
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}

}

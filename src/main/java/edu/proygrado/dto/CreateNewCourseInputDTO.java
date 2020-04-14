package edu.proygrado.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateNewCourseInputDTO {

	String fullname;
	String shortname;
	String summary;
	String format;
	String startdate;
	String enddate;
	String numsections;
	
	//enrollment
	String firstProfessorUserId;
	String firstProfessorRoleId;

	public CreateNewCourseInputDTO() {
		super();
	}

	public CreateNewCourseInputDTO(String fullname, String shortname, String summary, String format,
			String startdate, String enddate, String numsections, String firstProfessorUserId,
			String firstProfessorRoleId) {
		super();
		this.fullname = fullname;
		this.shortname = shortname;
		this.summary = summary;
		this.format = format;
		this.startdate = startdate;
		this.enddate = enddate;
		this.numsections = numsections;
		this.firstProfessorUserId = firstProfessorUserId;
		this.firstProfessorRoleId = firstProfessorRoleId;
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

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getStartdate() {
		return startdate;
	}

	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}

	public String getEnddate() {
		return enddate;
	}

	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}

	public String getNumsections() {
		return numsections;
	}

	public void setNumsections(String numsections) {
		this.numsections = numsections;
	}

	public String getFirstProfessorUserId() {
		return firstProfessorUserId;
	}

	public void setFirstProfessorUserId(String firstProfessorUserId) {
		this.firstProfessorUserId = firstProfessorUserId;
	}

	public String getFirstProfessorRoleId() {
		return firstProfessorRoleId;
	}

	public void setFirstProfessorRoleId(String firstProfessorRoleId) {
		this.firstProfessorRoleId = firstProfessorRoleId;
	}
	
}

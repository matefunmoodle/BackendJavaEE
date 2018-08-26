package edu.proygrado.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MoodleCoursesInfoDTO {
	private Long id;
	private String username;
	private String firstname;
	private String lastname;
	private String fullname;
	private String email;
	private String department;
	private Date firstaccess;
	private Date lastaccess;
	private String auth;
	private Boolean suspended;
	private Boolean confirmed;
	private String lang;
	private String theme;
	private String timezone;
	private Long mailformat;
	private String description;
	private Long descriptionformat;
	private String city;
	private String country;
	private String profileimageurlsmall;
	private String profileimageurl;
	private List<Object> preferences;
	private List<Object> groups;
	private List<MoodleRoleDTO> roles;
	private List<MoodleCourseDTO> enrolledcourses;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public Date getFirstaccess() {
		return firstaccess;
	}
	public void setFirstaccess(Date firstaccess) {
		this.firstaccess = firstaccess;
	}
	public Date getLastaccess() {
		return lastaccess;
	}
	public void setLastaccess(Date lastaccess) {
		this.lastaccess = lastaccess;
	}
	public String getAuth() {
		return auth;
	}
	public void setAuth(String auth) {
		this.auth = auth;
	}
	public Boolean getSuspended() {
		return suspended;
	}
	public void setSuspended(Boolean suspended) {
		this.suspended = suspended;
	}
	public Boolean getConfirmed() {
		return confirmed;
	}
	public void setConfirmed(Boolean confirmed) {
		this.confirmed = confirmed;
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	public String getTheme() {
		return theme;
	}
	public void setTheme(String theme) {
		this.theme = theme;
	}
	public String getTimezone() {
		return timezone;
	}
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	public Long getMailformat() {
		return mailformat;
	}
	public void setMailformat(Long mailformat) {
		this.mailformat = mailformat;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Long getDescriptionformat() {
		return descriptionformat;
	}
	public void setDescriptionformat(Long descriptionformat) {
		this.descriptionformat = descriptionformat;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getProfileimageurlsmall() {
		return profileimageurlsmall;
	}
	public void setProfileimageurlsmall(String profileimageurlsmall) {
		this.profileimageurlsmall = profileimageurlsmall;
	}
	public String getProfileimageurl() {
		return profileimageurl;
	}
	public void setProfileimageurl(String profileimageurl) {
		this.profileimageurl = profileimageurl;
	}
	public List<Object> getPreferences() {
		return preferences;
	}
	public void setPreferences(List<Object> preferences) {
		this.preferences = preferences;
	}
	public List<Object> getGroups() {
		return groups;
	}
	public void setGroups(List<Object> groups) {
		this.groups = groups;
	}
	public List<MoodleRoleDTO> getRoles() {
		return roles;
	}
	public void setRoles(List<MoodleRoleDTO> roles) {
		this.roles = roles;
	}
	public List<MoodleCourseDTO> getEnrolledcourses() {
		return enrolledcourses;
	}
	public void setEnrolledcourses(List<MoodleCourseDTO> enrolledcourses) {
		this.enrolledcourses = enrolledcourses;
	}
}

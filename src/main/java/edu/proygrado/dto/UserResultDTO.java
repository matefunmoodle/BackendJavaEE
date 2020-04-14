package edu.proygrado.dto;

import javax.json.JsonObject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import edu.proygrado.utils.Utils;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResultDTO {
	Long id;
	String username;
	String firstname;
	String lastname;
	String fullname;
	String email;
	String profileimageurlsmall;
	
	public UserResultDTO(JsonObject obj) {
		this.id = (long) obj.getInt("id");
		this.username = Utils.getOrDefault(obj, "username");
		this.firstname = Utils.getOrDefault(obj, "firstname");
		this.lastname = Utils.getOrDefault(obj, "lastname");
		this.fullname = Utils.getOrDefault(obj, "fullname");
		this.email = Utils.getOrDefault(obj, "email");
		this.profileimageurlsmall = Utils.getOrDefault(obj, "profileimageurlsmall");
	}
	
	public UserResultDTO() { }
	
	
	public UserResultDTO(Long id, String username, String firstname, String lastname, String fullname, String email,
			String profileimageurlsmall) {
		this.id = id;
		this.username = username;
		this.firstname = firstname;
		this.lastname = lastname;
		this.fullname = fullname;
		this.email = email;
		this.profileimageurlsmall = profileimageurlsmall;
	}
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
	public String getProfileimageurlsmall() {
		return profileimageurlsmall;
	}
	public void setProfileimageurlsmall(String profileimageurlsmall) {
		this.profileimageurlsmall = profileimageurlsmall;
	}
}

package edu.proygrado.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MoodleTokensDTO {
	private String token;
	private String privatetoken;
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getPrivatetoken() {
		return privatetoken;
	}
	public void setPrivatetoken(String privatetoken) {
		this.privatetoken = privatetoken;
	}
}

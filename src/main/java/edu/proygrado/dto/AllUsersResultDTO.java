package edu.proygrado.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AllUsersResultDTO {
	
	List<UserResultDTO> users;
	List<WarningDTO> warnings;
	
	public AllUsersResultDTO() { }
	
	public AllUsersResultDTO(List<UserResultDTO> users, List<WarningDTO> warnings) {
		this.users = users;
		this.warnings = warnings;
	}
	public List<UserResultDTO> getUsers() {
		return users;
	}
	public void setUsers(List<UserResultDTO> users) {
		this.users = users;
	}
	public List<WarningDTO> getWarnings() {
		return warnings;
	}
	public void setWarnings(List<WarningDTO> warnings) {
		this.warnings = warnings;
	}

}

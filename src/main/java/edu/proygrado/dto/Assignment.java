package edu.proygrado.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
class Config {
	public String plugin;
	public String subtype;
	public String name;
	public String value;
}


@JsonIgnoreProperties(ignoreUnknown = true)
public class Assignment {
	
	public Long id;
	public Long course;
	public String name;
	public Long duedate;
	public Long allowsubmissionsfromdate;
	public Long grade;
	public Long timemodified;
	public Long cutoffdate;
	public Long gradingduedate;
	public Long completionsubmit;
	public Long teamsubmission;
	public Long requireallteammemberssubmit;
	public Long maxattempts;
	
	public List<Config> configs;

}

package edu.proygrado.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WarningDTO {
	String item;
	Integer itemid;
	String warningcode;
	String message;
	
	public WarningDTO() { }
	
	public WarningDTO(String item, Integer itemid, String warningcode, String message) {
		this.item = item;
		this.itemid = itemid;
		this.warningcode = warningcode;
		this.message = message;
	}
	public Integer getItemid() {
		return itemid;
	}

	public void setItemid(Integer itemid) {
		this.itemid = itemid;
	}

	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getWarningcode() {
		return warningcode;
	}
	public void setWarningcode(String warningcode) {
		this.warningcode = warningcode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}

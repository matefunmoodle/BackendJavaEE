package edu.proygrado.dto;

public class SimplePostResultDTO {
	String message;
	Boolean wasOk;
	Object result;
	
	public SimplePostResultDTO(String message, Boolean wasOk){
		this.message = message;
		this.wasOk = wasOk;
		this.result = null;
	}
	
	public SimplePostResultDTO(String message, Boolean wasOk, Object result){
		this.message = message;
		this.wasOk = wasOk;
		this.result = result;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Boolean getWasOk() {
		return wasOk;
	}

	public void setWasOk(Boolean wasOk) {
		this.wasOk = wasOk;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}
}

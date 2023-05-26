package com.aspire.miniaspireuserloansapp.model.dto.authentication;

public class RegistrationResponse {
	
	private Boolean registered;
	
	private String message;

	public RegistrationResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RegistrationResponse(Boolean registered, String message) {
		super();
		this.registered = registered;
		this.message = message;
	}

	public Boolean getRegistered() {
		return registered;
	}

	public void setRegistered(Boolean registered) {
		this.registered = registered;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}

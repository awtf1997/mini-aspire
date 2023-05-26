package com.aspire.miniaspireuserloansapp.model.dto.authentication;

public class AuthenticationResponse {
	
	private Boolean authenticated;
	
	private String message;

	public AuthenticationResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AuthenticationResponse(Boolean authenticated, String message) {
		super();
		this.authenticated = authenticated;
		this.message = message;
	}

	public Boolean getAuthenticated() {
		return authenticated;
	}

	public void setAuthenticated(Boolean authenticated) {
		this.authenticated = authenticated;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}

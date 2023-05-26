package com.aspire.miniaspireuserloansapp.service;

import com.aspire.miniaspireuserloansapp.model.dto.authentication.AuthenticationRequest;
import com.aspire.miniaspireuserloansapp.model.dto.authentication.AuthenticationResponse;
import com.aspire.miniaspireuserloansapp.model.dto.authentication.RegistrationRequest;
import com.aspire.miniaspireuserloansapp.model.dto.authentication.RegistrationResponse;

public interface AuthService {

	AuthenticationResponse authenticateUser(AuthenticationRequest authReq);

	RegistrationResponse registerUser(RegistrationRequest regnReq);
	
	Boolean checkIfUserExists(String username);
	
	Boolean checkIfUserExists(String username, String password);

	String getUserRole(String username);
	
}

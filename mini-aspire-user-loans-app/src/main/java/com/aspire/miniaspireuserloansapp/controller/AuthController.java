package com.aspire.miniaspireuserloansapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aspire.miniaspireuserloansapp.constants.AuthConstants;
import com.aspire.miniaspireuserloansapp.model.dto.authentication.AuthenticationRequest;
import com.aspire.miniaspireuserloansapp.model.dto.authentication.AuthenticationResponse;
import com.aspire.miniaspireuserloansapp.model.dto.authentication.RegistrationRequest;
import com.aspire.miniaspireuserloansapp.model.dto.authentication.RegistrationResponse;
import com.aspire.miniaspireuserloansapp.service.AuthService;

@RestController
@RequestMapping(path = "/api/v1/mini-aspire/login")
public class AuthController {
	
	@Autowired
	private AuthService authService;
	
	@PostMapping(path = "/authenticate")
	public ResponseEntity<AuthenticationResponse> authenticateUser(@RequestBody AuthenticationRequest authReq) {
		ResponseEntity<AuthenticationResponse> authResp = null;
		try {
			AuthenticationResponse responseBody = authService.authenticateUser(authReq);
			if (responseBody.getAuthenticated()) {
				HttpHeaders respHeaders = new HttpHeaders();
				respHeaders.add(HttpHeaders.AUTHORIZATION, responseBody.getMessage());
				responseBody.setMessage(AuthConstants.AUTHENTICATION_SUCCESS);
				authResp = ResponseEntity.status(HttpStatus.OK).headers(respHeaders).body(responseBody);
			}
			else {
				responseBody.setMessage(AuthConstants.AUTHENTICATION_FAILED + ", " + responseBody.getMessage());
				authResp = ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseBody);
			}
		} catch (Exception e) {
			AuthenticationResponse responseBody = new AuthenticationResponse(
					false, AuthConstants.AUTHENTICATION_FAILED);
			authResp = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
		}
		return authResp;
	}
	
	@PostMapping(path = "/register")
	public ResponseEntity<RegistrationResponse> registerUser(@RequestBody RegistrationRequest regnReq) {
		ResponseEntity<RegistrationResponse> regnResp = null;
		try {
			RegistrationResponse responseBody = authService.registerUser(regnReq);
			if (responseBody.getRegistered()) {
				HttpHeaders respHeaders = new HttpHeaders();
				respHeaders.add(HttpHeaders.AUTHORIZATION, responseBody.getMessage());
				responseBody.setMessage(AuthConstants.REGISTRATION_SUCCESS);
				regnResp = ResponseEntity.status(HttpStatus.OK).headers(respHeaders).body(responseBody);
			}
			else {
				responseBody.setMessage(AuthConstants.REGISTRATION_FAILED + ", " + responseBody.getMessage());
				regnResp = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
			}
		} catch (Exception e) {
			RegistrationResponse responseBody = new RegistrationResponse(
					false, AuthConstants.REGISTRATION_FAILED);
			regnResp = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
		}
		return regnResp;
	}

}

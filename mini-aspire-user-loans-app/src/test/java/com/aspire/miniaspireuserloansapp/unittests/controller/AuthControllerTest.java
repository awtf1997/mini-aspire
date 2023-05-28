package com.aspire.miniaspireuserloansapp.unittests.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.aspire.miniaspireuserloansapp.constants.AuthConstants;
import com.aspire.miniaspireuserloansapp.controller.AuthController;
import com.aspire.miniaspireuserloansapp.model.dto.authentication.AuthenticationRequest;
import com.aspire.miniaspireuserloansapp.model.dto.authentication.AuthenticationResponse;
import com.aspire.miniaspireuserloansapp.model.dto.authentication.RegistrationRequest;
import com.aspire.miniaspireuserloansapp.model.dto.authentication.RegistrationResponse;
import com.aspire.miniaspireuserloansapp.service.AuthService;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
	
	@Mock
	private AuthService authService; 
	
	@InjectMocks
	private AuthController authController;
	
	private String testJwtToken;

	@BeforeEach
	void setUp() throws Exception {
		testJwtToken = "Test Jwt Token";
	}

	@Test
	void testAuthenticateUser_Success() {
		when(authService.authenticateUser(any(AuthenticationRequest.class))).thenReturn(new AuthenticationResponse(true, testJwtToken));
		ResponseEntity<AuthenticationResponse> authResp = authController.authenticateUser(new AuthenticationRequest("abc", "123457890"));
		assertEquals(HttpStatus.OK, authResp.getStatusCode());
		assertEquals(testJwtToken, authResp.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0));
		assertTrue(authResp.getBody().getAuthenticated());
		assertEquals(AuthConstants.AUTHENTICATION_SUCCESS, authResp.getBody().getMessage());
	}
	
	@Test
	void testAuthenticateUser_Fail() {
		when(authService.authenticateUser(any(AuthenticationRequest.class))).thenReturn(new AuthenticationResponse(false, AuthConstants.INCORRECT_CREDENTIALS));
		ResponseEntity<AuthenticationResponse> authResp = authController.authenticateUser(new AuthenticationRequest("abc", "123457890"));
		assertEquals(HttpStatus.FORBIDDEN, authResp.getStatusCode());
		assertFalse(authResp.getBody().getAuthenticated());
		assertEquals(AuthConstants.AUTHENTICATION_FAILED + ", " + AuthConstants.INCORRECT_CREDENTIALS, authResp.getBody().getMessage());
		
		when(authService.authenticateUser(any(AuthenticationRequest.class))).thenReturn(new AuthenticationResponse(false, AuthConstants.SERVER_FAILURE));
		authResp = authController.authenticateUser(new AuthenticationRequest("abc", "123457890"));
		assertEquals(HttpStatus.FORBIDDEN, authResp.getStatusCode());
		assertFalse(authResp.getBody().getAuthenticated());
		assertEquals(AuthConstants.AUTHENTICATION_FAILED + ", " + AuthConstants.SERVER_FAILURE, authResp.getBody().getMessage());
	}
	
	@Test
	void testAuthenticateUser_ThrowsException() {
		when(authService.authenticateUser(any(AuthenticationRequest.class))).thenThrow(new RuntimeException("Test exception"));
		ResponseEntity<AuthenticationResponse> authResp = authController.authenticateUser(new AuthenticationRequest("abc", "123457890"));
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, authResp.getStatusCode());
		assertFalse(authResp.getBody().getAuthenticated());
		assertEquals(AuthConstants.AUTHENTICATION_FAILED, authResp.getBody().getMessage());
	}

	@Test
	void testRegisterUser_Success() {
		when(authService.registerUser(any(RegistrationRequest.class))).thenReturn(new RegistrationResponse(true, testJwtToken));
		ResponseEntity<RegistrationResponse> regnResp = authController.registerUser(new RegistrationRequest("abc", "1234567890", AuthConstants.CUSTOMER_ROLE));
		assertEquals(HttpStatus.OK, regnResp.getStatusCode());
		assertEquals(testJwtToken, regnResp.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0));
		assertTrue(regnResp.getBody().getRegistered());
		assertEquals(AuthConstants.REGISTRATION_SUCCESS, regnResp.getBody().getMessage());
	}
	
	@Test
	void testRegisterUser_Fail() {
		when(authService.registerUser(any(RegistrationRequest.class))).thenReturn(new RegistrationResponse(false, AuthConstants.USERNAME_ALREADY_EXISTS));
		ResponseEntity<RegistrationResponse> regnResp = authController.registerUser(new RegistrationRequest("abc", "123457890", AuthConstants.CUSTOMER_ROLE));
		assertEquals(HttpStatus.BAD_REQUEST, regnResp.getStatusCode());
		assertFalse(regnResp.getBody().getRegistered());
		assertEquals(AuthConstants.REGISTRATION_FAILED + ", " + AuthConstants.USERNAME_ALREADY_EXISTS, regnResp.getBody().getMessage());
		
		when(authService.registerUser(any(RegistrationRequest.class))).thenReturn(new RegistrationResponse(false, AuthConstants.INVALID_PASSWORD));
		regnResp = authController.registerUser(new RegistrationRequest("abc", "1234", AuthConstants.CUSTOMER_ROLE));
		assertEquals(HttpStatus.BAD_REQUEST, regnResp.getStatusCode());
		assertFalse(regnResp.getBody().getRegistered());
		assertEquals(AuthConstants.REGISTRATION_FAILED + ", " + AuthConstants.INVALID_PASSWORD, regnResp.getBody().getMessage());
		
		when(authService.registerUser(any(RegistrationRequest.class))).thenReturn(new RegistrationResponse(false, AuthConstants.INVALID_ROLE));
		regnResp = authController.registerUser(new RegistrationRequest("abc", "1234908765", "INVALID ROLE"));
		assertEquals(HttpStatus.BAD_REQUEST, regnResp.getStatusCode());
		assertFalse(regnResp.getBody().getRegistered());
		assertEquals(AuthConstants.REGISTRATION_FAILED + ", " + AuthConstants.INVALID_ROLE, regnResp.getBody().getMessage());
		
		when(authService.registerUser(any(RegistrationRequest.class))).thenReturn(new RegistrationResponse(false, AuthConstants.SERVER_FAILURE));
		regnResp = authController.registerUser(new RegistrationRequest("abc", "1234908765", AuthConstants.ADMIN_ROLE));
		assertEquals(HttpStatus.BAD_REQUEST, regnResp.getStatusCode());
		assertFalse(regnResp.getBody().getRegistered());
		assertEquals(AuthConstants.REGISTRATION_FAILED + ", " + AuthConstants.SERVER_FAILURE, regnResp.getBody().getMessage());
	}
	
	@Test
	void testRegisterUser_ThrowsException() {
		when(authService.registerUser(any(RegistrationRequest.class))).thenThrow(new RuntimeException("Test exception"));
		ResponseEntity<RegistrationResponse> regnResp = authController.registerUser(new RegistrationRequest("abc", "123457890", AuthConstants.CUSTOMER_ROLE));
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, regnResp.getStatusCode());
		assertFalse(regnResp.getBody().getRegistered());
		assertEquals(AuthConstants.REGISTRATION_FAILED, regnResp.getBody().getMessage());
	}

}

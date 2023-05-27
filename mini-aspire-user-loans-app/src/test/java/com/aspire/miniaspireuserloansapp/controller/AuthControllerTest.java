package com.aspire.miniaspireuserloansapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import com.aspire.miniaspireuserloansapp.model.dto.authentication.AuthenticationRequest;
import com.aspire.miniaspireuserloansapp.model.dto.authentication.AuthenticationResponse;
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
		when(authService.authenticateUser(any(AuthenticationRequest.class))).thenReturn(new AuthenticationResponse(false, null));
		ResponseEntity<AuthenticationResponse> authResp = authController.authenticateUser(new AuthenticationRequest("abc", "123457890"));
		assertEquals(HttpStatus.OK, authResp.getStatusCode());
		assertEquals(testJwtToken, authResp.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0));
		assertTrue(authResp.getBody().getAuthenticated());
		assertEquals(AuthConstants.AUTHENTICATION_SUCCESS, authResp.getBody().getMessage());
	}
	
	@Test
	void testAuthenticateUser_ThrowsException() {
		
	}

	@Test
	void testRegisterUser_Success() {
		
	}
	
	@Test
	void testRegisterUser_Fail() {
		
	}
	
	@Test
	void testRegisterUser_ThrowsException() {
		
	}

}

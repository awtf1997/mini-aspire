package com.aspire.miniaspireuserloansapp.unittests.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aspire.miniaspireuserloansapp.constants.AuthConstants;
import com.aspire.miniaspireuserloansapp.model.dto.authentication.AuthenticationRequest;
import com.aspire.miniaspireuserloansapp.model.dto.authentication.AuthenticationResponse;
import com.aspire.miniaspireuserloansapp.model.dto.authentication.RegistrationRequest;
import com.aspire.miniaspireuserloansapp.model.dto.authentication.RegistrationResponse;
import com.aspire.miniaspireuserloansapp.model.entity.User;
import com.aspire.miniaspireuserloansapp.repository.UserRepository;
import com.aspire.miniaspireuserloansapp.service.impl.AuthServiceImpl;
import com.aspire.miniaspireuserloansapp.utils.JwtTokenUtil;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private JwtTokenUtil jwtTokenUtil;
	
	@InjectMocks
	private AuthServiceImpl authService;
	
	private AuthenticationRequest authReq;
	private RegistrationRequest regnReq;
	private User user;
	private String testJwtToken ;
	private Exception exception;
	
	
	@BeforeEach
	void setUp() throws Exception {
		authReq = new AuthenticationRequest("abc", "12345890");
		regnReq = new RegistrationRequest("abc", "12345890", AuthConstants.ADMIN_ROLE);
		user = new User("abc", "12345890", "ADMIN");
		testJwtToken = "testJwtToken";
		exception = new RuntimeException("Test Exception");
	}
	
	@Test
	void testAuthenticateUserSuccess() {
		Optional<User> userOptional = Optional.of(user);
		when(userRepository.findUser(authReq.getUsername(), authReq.getPassword())).thenReturn(userOptional);
		when (jwtTokenUtil.generateToken(user)).thenReturn(testJwtToken);
		AuthenticationResponse authResp = authService.authenticateUser(authReq);
		assertTrue(authResp.getAuthenticated());
		assertEquals(testJwtToken, authResp.getMessage());
	}
	
	@Test
	void testAuthenticateUserFailIncorrectCreds() {
		Optional<User> userOptional = Optional.ofNullable(null);
		when(userRepository.findUser(authReq.getUsername(), authReq.getPassword())).thenReturn(userOptional);
		AuthenticationResponse authResp = authService.authenticateUser(authReq);
		assertFalse(authResp.getAuthenticated());
		assertEquals(AuthConstants.INCORRECT_CREDENTIALS, authResp.getMessage());
	}
	
	@Test
	void testAuthenticateUserFailServerFailure() {
		when(userRepository.findUser(authReq.getUsername(), authReq.getPassword())).thenThrow(exception);
		AuthenticationResponse authResp = authService.authenticateUser(authReq);
		assertFalse(authResp.getAuthenticated());
		assertEquals(AuthConstants.SERVER_FAILURE, authResp.getMessage());
	}

	@Test
	void testRegisterUserSuccess() {
		Optional<User> userOptional = Optional.ofNullable(null);
		when(userRepository.findUser(regnReq.getUsername())).thenReturn(userOptional);
		when(userRepository.save(any(User.class))).thenReturn(null);
		when (jwtTokenUtil.generateToken(any(User.class))).thenReturn(testJwtToken);
		RegistrationResponse regnResp = authService.registerUser(regnReq);
		assertTrue(regnResp.getRegistered());
		assertEquals(testJwtToken, regnResp.getMessage());
	}
	
	@Test
	void testRegisterUserFailUsernameAlreadyExists() {
		Optional<User> userOptional = Optional.of(user);
		when(userRepository.findUser(regnReq.getUsername())).thenReturn(userOptional);
		RegistrationResponse regnResp = authService.registerUser(regnReq);
		assertFalse(regnResp.getRegistered());
		assertEquals(AuthConstants.USERNAME_ALREADY_EXISTS, regnResp.getMessage());
	}
	
	@Test
	void testRegisterUserFailInvalidPassword() {
		Optional<User> userOptional = Optional.ofNullable(null);
		when(userRepository.findUser(regnReq.getUsername())).thenReturn(userOptional);
		regnReq.setPassword("123");
		RegistrationResponse regnResp = authService.registerUser(regnReq);
		assertFalse(regnResp.getRegistered());
		assertEquals(AuthConstants.INVALID_PASSWORD + ": MINIMUM PASSWORD LENGTH SHOULD BE "
				+ AuthConstants.PASSWORD_MINIMUM_LENGTH, regnResp.getMessage());
		when(userRepository.findUser(regnReq.getUsername())).thenReturn(userOptional);
		regnReq.setPassword("12345678910111213141516");
		regnResp = authService.registerUser(regnReq);
		assertFalse(regnResp.getRegistered());
		assertEquals(AuthConstants.INVALID_PASSWORD + ": MAXIMUM PASSWORD LENGTH SHOULD BE "
				+ AuthConstants.PASSWORD_MAXIMUM_LENGTH, regnResp.getMessage());
	}
	
	@Test
	void testRegisterUserFailInvalidRole() {
		Optional<User> userOptional = Optional.ofNullable(null);
		when(userRepository.findUser(regnReq.getUsername())).thenReturn(userOptional);
		regnReq.setRole("TEST_ROLE");
		RegistrationResponse regnResp = authService.registerUser(regnReq);
		assertFalse(regnResp.getRegistered());
		assertEquals(AuthConstants.INVALID_ROLE, regnResp.getMessage());
	}
	
	@Test
	void testRegisterUserFailServerFailure() {
		when(userRepository.findUser(regnReq.getUsername())).thenThrow(exception);
		RegistrationResponse regnResp = authService.registerUser(regnReq);
		assertFalse(regnResp.getRegistered());
		assertEquals(AuthConstants.SERVER_FAILURE, regnResp.getMessage());
	}

	@Test
	void testCheckIfUserExistsStringString_WhenUserExists() {
		Optional<User> userOptional = Optional.of(user);
		when(userRepository.findUser("abc", "1234567890")).thenReturn(userOptional);
		Boolean userExists = authService.checkIfUserExists("abc", "1234567890");
		assertTrue(userExists);
	}
	
	@Test
	void testCheckIfUserExistsStringString_WhenUserDoesntExists() {
		Optional<User> userOptional = Optional.ofNullable(null);
		when(userRepository.findUser("abc", "1234")).thenReturn(userOptional);
		Boolean userExists = authService.checkIfUserExists("abc", "1234");
		assertFalse(userExists);
	}
	
	@Test
	void testCheckIfUserExistsStringString_ServerFailure() {
		when(userRepository.findUser("abc", "1234")).thenThrow(exception);
		Boolean userExists = authService.checkIfUserExists("abc", "1234");
		assertFalse(userExists);
	}
	
	@Test
	void testCheckIfUserExistsString_WhenUserExists() {
		Optional<User> userOptional = Optional.of(user);
		when(userRepository.findUser("abc")).thenReturn(userOptional);
		Boolean userExists = authService.checkIfUserExists("abc");
		assertTrue(userExists);
	}
	
	@Test
	void testCheckIfUserExistsString_WhenUserDoesntExists() {
		Optional<User> userOptional = Optional.ofNullable(null);
		when(userRepository.findUser("abc")).thenReturn(userOptional);
		Boolean userExists = authService.checkIfUserExists("abc");
		assertFalse(userExists);
	}
	
	@Test
	void testCheckIfUserExistsString_ServerFailure() {
		when(userRepository.findUser("abc")).thenThrow(exception);
		Boolean userExists = authService.checkIfUserExists("abc");
		assertFalse(userExists);
	}

	@Test
	void testGetUserRole() {
		Optional<User> userOptional = Optional.of(user);
		when(userRepository.findUser("abc")).thenReturn(userOptional);
		String userRole = authService.getUserRole("abc");
		assertEquals(AuthConstants.ADMIN_ROLE, userRole);
	}

}

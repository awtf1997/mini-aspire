package com.aspire.miniaspireuserloansapp.integrationtests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.aspire.miniaspireuserloansapp.MiniAspireUserLoansAppApplication;
import com.aspire.miniaspireuserloansapp.constants.AuthConstants;
import com.aspire.miniaspireuserloansapp.model.dto.authentication.AuthenticationRequest;
import com.aspire.miniaspireuserloansapp.model.dto.authentication.AuthenticationResponse;
import com.aspire.miniaspireuserloansapp.model.dto.authentication.RegistrationRequest;
import com.aspire.miniaspireuserloansapp.model.dto.authentication.RegistrationResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = MiniAspireUserLoansAppApplication.class)
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
public class AuthenticationFlowTests {
	
	@Autowired
	private MockMvc mockMvc;
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	@Test
	@Order(value = 1)
	void testAuthenticateAnUnregisteredUser() throws Exception {
		AuthenticationRequest authReq = new AuthenticationRequest("abc", "1234567890");
		MvcResult mvcResult = mockMvc.perform(
				MockMvcRequestBuilders
				.post("/api/v1/mini-aspire/login/authenticate")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(authReq))
				.accept(MediaType.APPLICATION_JSON)
			).andReturn();
		AuthenticationResponse authResp = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), AuthenticationResponse.class);
		assertEquals(HttpStatus.FORBIDDEN.value(), mvcResult.getResponse().getStatus());
		assertFalse(authResp.getAuthenticated());
		assertEquals(AuthConstants.AUTHENTICATION_FAILED + ", " + AuthConstants.INCORRECT_CREDENTIALS, authResp.getMessage());
	}
	
	@Test
	@Order(value = 2)
	void testRegisterANewUser_InvalidPassword() throws Exception {
		RegistrationRequest regnReq1 = new RegistrationRequest("abc", "1234", AuthConstants.ADMIN_ROLE);
		MvcResult mvcResult1 = mockMvc.perform(
				MockMvcRequestBuilders
				.post("/api/v1/mini-aspire/login/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(regnReq1))
				.accept(MediaType.APPLICATION_JSON)
			).andReturn();
		assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult1.getResponse().getStatus());
		RegistrationResponse regnResp1 = objectMapper.readValue(mvcResult1.getResponse().getContentAsString(), RegistrationResponse.class);
		assertFalse(regnResp1.getRegistered());
		assertEquals(AuthConstants.REGISTRATION_FAILED + ", " + AuthConstants.INVALID_PASSWORD + ": MINIMUM PASSWORD LENGTH SHOULD BE "
				+ AuthConstants.PASSWORD_MINIMUM_LENGTH, regnResp1.getMessage());
		
		RegistrationRequest regnReq2 = new RegistrationRequest("abc", "12345678910111213141516", AuthConstants.ADMIN_ROLE);
		MvcResult mvcResult2 = mockMvc.perform(
				MockMvcRequestBuilders
				.post("/api/v1/mini-aspire/login/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(regnReq2))
				.accept(MediaType.APPLICATION_JSON)
			).andReturn();
		assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult2.getResponse().getStatus());
		RegistrationResponse regnResp2 = objectMapper.readValue(mvcResult2.getResponse().getContentAsString(), RegistrationResponse.class);
		assertFalse(regnResp2.getRegistered());
		assertEquals(AuthConstants.REGISTRATION_FAILED + ", " + AuthConstants.INVALID_PASSWORD + ": MAXIMUM PASSWORD LENGTH SHOULD BE "
				+ AuthConstants.PASSWORD_MAXIMUM_LENGTH, regnResp2.getMessage());
	}
	
	@Test
	@Order(value = 3)
	void testRegisterANewUser_InvalidRole() throws Exception {
		RegistrationRequest regnReq = new RegistrationRequest("abc", "123456789", "INVALID ROLE");
		MvcResult mvcResult = mockMvc.perform(
				MockMvcRequestBuilders
				.post("/api/v1/mini-aspire/login/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(regnReq))
				.accept(MediaType.APPLICATION_JSON)
			).andReturn();
		assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
		RegistrationResponse regnResp = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), RegistrationResponse.class);
		assertFalse(regnResp.getRegistered());
		assertEquals(AuthConstants.REGISTRATION_FAILED + ", " + AuthConstants.INVALID_ROLE, regnResp.getMessage());
	}
	
	@Test
	@Order(value = 4)
	void testRegisterANewUser() throws Exception {
		// user currently not in system
		RegistrationRequest regnReq = new RegistrationRequest("abc", "1234567890", AuthConstants.ADMIN_ROLE);
		MvcResult mvcResult = mockMvc.perform(
				MockMvcRequestBuilders
				.post("/api/v1/mini-aspire/login/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(regnReq))
				.accept(MediaType.APPLICATION_JSON)
			).andReturn();
		assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
		String jwtToken = mvcResult.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
		assertNotNull(jwtToken);
		assertTrue(jwtToken.length() > 0);
		RegistrationResponse regnResp = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), RegistrationResponse.class);
		assertTrue(regnResp.getRegistered());
		assertEquals(AuthConstants.REGISTRATION_SUCCESS, regnResp.getMessage());
	}

	@Test
	@Order(value = 5)
	void testRegisterANewUser_InvalidUsername() throws Exception {
		// user currently not in system
		RegistrationRequest regnReq = new RegistrationRequest("abc", "1234567890", AuthConstants.ADMIN_ROLE);
		MvcResult mvcResult = mockMvc.perform(
				MockMvcRequestBuilders
				.post("/api/v1/mini-aspire/login/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(regnReq))
				.accept(MediaType.APPLICATION_JSON)
			).andReturn();
		assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
		RegistrationResponse regnResp = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), RegistrationResponse.class);
		assertFalse(regnResp.getRegistered());
		assertEquals(AuthConstants.REGISTRATION_FAILED + ", " + AuthConstants.USERNAME_ALREADY_EXISTS, regnResp.getMessage());
	}
	
	@Test
	@Order(value = 6)
	void testAuthenticateARegisteredUser() throws Exception {
		AuthenticationRequest authReq = new AuthenticationRequest("abc", "1234567890");
		MvcResult mvcResult = mockMvc.perform(
				MockMvcRequestBuilders
				.post("/api/v1/mini-aspire/login/authenticate")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(authReq))
				.accept(MediaType.APPLICATION_JSON)
			).andReturn();
		assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
		String jwtToken = mvcResult.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
		assertNotNull(jwtToken);
		assertTrue(jwtToken.length() > 0);
		AuthenticationResponse authResp = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), AuthenticationResponse.class);
		assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
		assertTrue(authResp.getAuthenticated());
		assertEquals(AuthConstants.AUTHENTICATION_SUCCESS, authResp.getMessage());
	}
	
}

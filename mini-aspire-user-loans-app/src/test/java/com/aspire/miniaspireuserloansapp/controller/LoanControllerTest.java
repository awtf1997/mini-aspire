package com.aspire.miniaspireuserloansapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.aspire.miniaspireuserloansapp.constants.AuthConstants;
import com.aspire.miniaspireuserloansapp.constants.LoanConstants;
import com.aspire.miniaspireuserloansapp.model.dto.loans.LoanDto;
import com.aspire.miniaspireuserloansapp.model.dto.loans.UserLoans;
import com.aspire.miniaspireuserloansapp.service.AuthService;
import com.aspire.miniaspireuserloansapp.service.LoanService;
import com.aspire.miniaspireuserloansapp.utils.JwtTokenUtil;

@ExtendWith(MockitoExtension.class)
class LoanControllerTest {
	
	@Mock
	private AuthService authService;
	
	@Mock
	private LoanService loanService;
	
	@Mock
	private JwtTokenUtil jwtTokenUtil; 
	
	@InjectMocks
	private LoanController loanController;
	
	private String jwtToken;
	private String username;
	private Exception exception;
	
	@BeforeEach
	void setUp() throws Exception {
		jwtToken = "TestJwtToken";
		username = "abc";
		exception = new RuntimeException("Test exception");
	}

	@Test
	void testGetLoanDetails_DelegateToLoanService_Success() {
		when(authService.checkIfUserExists("abc")).thenReturn(true);
		when(authService.getUserRole("abc")).thenReturn(AuthConstants.CUSTOMER_ROLE);
		when(jwtTokenUtil.validateToken(jwtToken, "abc", AuthConstants.CUSTOMER_ROLE)).thenReturn(true);
		when(loanService.getLoansOfUser("abc")).thenReturn(new UserLoans("abc", new ArrayList<LoanDto>(), LoanConstants.LOANS_FETCH_SUCCESS));
		ResponseEntity<UserLoans> resp = loanController.getLoanDetails(jwtToken, username);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		UserLoans userLoans = resp.getBody();
		assertEquals("abc", userLoans.getUsername());
		assertEquals(LoanConstants.LOANS_FETCH_SUCCESS, userLoans.getMessage());
		List<LoanDto> loanDtos = userLoans.getLoans();
		assertEquals(0, loanDtos.size());
	}
	
	@Test
	void testGetLoanDetails_DelegateToLoanService_Fail_InvalidUser() {
		when(authService.checkIfUserExists("abc")).thenReturn(false);
		ResponseEntity<UserLoans> resp = loanController.getLoanDetails(jwtToken, username);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		UserLoans userLoans = resp.getBody();
		assertEquals("abc", userLoans.getUsername());
		assertEquals(LoanConstants.INVALID_USERNAME, userLoans.getMessage());
		List<LoanDto> loanDtos = userLoans.getLoans();
		assertNull(loanDtos);
	}
	
	@Test
	void testGetLoanDetails_DelegateToLoanService_Fail_InvalidJwtToken() {
		when(authService.checkIfUserExists("abc")).thenReturn(true);
		when(authService.getUserRole("abc")).thenReturn(AuthConstants.CUSTOMER_ROLE);
		when(jwtTokenUtil.validateToken(jwtToken, "abc", AuthConstants.CUSTOMER_ROLE)).thenReturn(false);
		ResponseEntity<UserLoans> resp = loanController.getLoanDetails(jwtToken, username);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		UserLoans userLoans = resp.getBody();
		assertEquals("abc", userLoans.getUsername());
		assertEquals(LoanConstants.INVALID_AUTH_TOKEN, userLoans.getMessage());
		List<LoanDto> loanDtos = userLoans.getLoans();
		assertNull(loanDtos);
	}
	
	@Test
	void testGetLoanDetails_DelegateToLoanService_Fail_ThrowExceptionByLoanService() {
		when(authService.checkIfUserExists("abc")).thenReturn(true);
		when(authService.getUserRole("abc")).thenReturn(AuthConstants.CUSTOMER_ROLE);
		when(jwtTokenUtil.validateToken(jwtToken, "abc", AuthConstants.CUSTOMER_ROLE)).thenReturn(true);
		when(loanService.getLoansOfUser("abc")).thenThrow(exception);
		ResponseEntity<UserLoans> resp = loanController.getLoanDetails(jwtToken, username);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
		UserLoans userLoans = resp.getBody();
		assertEquals("abc", userLoans.getUsername());
		assertEquals(LoanConstants.SERVER_FAILURE, userLoans.getMessage());
		List<LoanDto> loanDtos = userLoans.getLoans();
		assertNull(loanDtos);
	}

	@Test
	void testApplyForNewLoan() {
	}

	@Test
	void testApproveLoan() {
	}

	@Test
	void testRepayLoan() {
	}

}

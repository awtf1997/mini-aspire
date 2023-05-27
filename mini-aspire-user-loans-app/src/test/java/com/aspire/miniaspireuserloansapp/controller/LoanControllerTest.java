package com.aspire.miniaspireuserloansapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
import com.aspire.miniaspireuserloansapp.model.dto.loans.LoanApplicationRequest;
import com.aspire.miniaspireuserloansapp.model.dto.loans.LoanApplicationResponse;
import com.aspire.miniaspireuserloansapp.model.dto.loans.LoanApprovalRequest;
import com.aspire.miniaspireuserloansapp.model.dto.loans.LoanApprovalResponse;
import com.aspire.miniaspireuserloansapp.model.dto.loans.LoanDto;
import com.aspire.miniaspireuserloansapp.model.dto.loans.LoanRepaymentRequest;
import com.aspire.miniaspireuserloansapp.model.dto.loans.LoanRepaymentResponse;
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
		when(authService.checkIfUserExists(any(String.class))).thenReturn(true);
		when(authService.getUserRole(any(String.class))).thenReturn(AuthConstants.CUSTOMER_ROLE);
		when(jwtTokenUtil.validateToken(any(String.class), any(String.class), any(String.class))).thenReturn(true);
		when(loanService.getLoansOfUser(any(String.class))).thenReturn(new UserLoans("abc", new ArrayList<LoanDto>(), LoanConstants.LOANS_FETCH_SUCCESS));
		ResponseEntity<UserLoans> resp = loanController.getLoanDetails(jwtToken, username);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		UserLoans userLoans = resp.getBody();
		assertEquals("abc", userLoans.getUsername());
		assertEquals(LoanConstants.LOANS_FETCH_SUCCESS, userLoans.getMessage());
		List<LoanDto> loanDtos = userLoans.getLoans();
		assertEquals(0, loanDtos.size());
	}
	
	@Test
	void testGetLoanDetails_Fail_InvalidUser() {
		when(authService.checkIfUserExists(any(String.class))).thenReturn(false);
		ResponseEntity<UserLoans> resp = loanController.getLoanDetails(jwtToken, username);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		UserLoans userLoans = resp.getBody();
		assertEquals("abc", userLoans.getUsername());
		assertEquals(LoanConstants.INVALID_USERNAME, userLoans.getMessage());
		List<LoanDto> loanDtos = userLoans.getLoans();
		assertNull(loanDtos);
	}
	
	@Test
	void testGetLoanDetails_Fail_InvalidJwtToken() {
		when(authService.checkIfUserExists(any(String.class))).thenReturn(true);
		when(authService.getUserRole(any(String.class))).thenReturn(AuthConstants.CUSTOMER_ROLE);
		when(jwtTokenUtil.validateToken(any(String.class), any(String.class), any(String.class))).thenReturn(false);
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
		when(authService.checkIfUserExists(any(String.class))).thenReturn(true);
		when(authService.getUserRole(any(String.class))).thenReturn(AuthConstants.CUSTOMER_ROLE);
		when(jwtTokenUtil.validateToken(any(String.class), any(String.class), any(String.class))).thenReturn(true);
		when(loanService.getLoansOfUser(any(String.class))).thenThrow(exception);
		ResponseEntity<UserLoans> resp = loanController.getLoanDetails(jwtToken, username);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
		UserLoans userLoans = resp.getBody();
		assertEquals("abc", userLoans.getUsername());
		assertEquals(LoanConstants.SERVER_FAILURE, userLoans.getMessage());
		List<LoanDto> loanDtos = userLoans.getLoans();
		assertNull(loanDtos);
	}

	@Test
	void testApplyForNewLoan_DelegateToLoanService_Success() {
		when(authService.checkIfUserExists(any(String.class))).thenReturn(true);
		when(authService.getUserRole(any(String.class))).thenReturn(AuthConstants.CUSTOMER_ROLE);
		when(jwtTokenUtil.validateToken(any(String.class), any(String.class), any(String.class))).thenReturn(true);
		when(loanService.applyForNewLoan(any(LoanApplicationRequest.class))).thenReturn(new LoanApplicationResponse(true, 1, new LoanDto(), LoanConstants.LOAN_APPLICATION_SUCCESS));
		ResponseEntity<LoanApplicationResponse> resp = loanController.applyForNewLoan(jwtToken, new LoanApplicationRequest(username, 1000D, 5));
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		LoanApplicationResponse loanApplicationResp = resp.getBody();
		assertTrue(loanApplicationResp.getIsApplied());
		assertEquals(1, loanApplicationResp.getLoanId());
		assertNotNull(loanApplicationResp.getLoanDto());
		assertEquals(LoanConstants.LOAN_APPLICATION_SUCCESS, loanApplicationResp.getMessage());
	}
	
	@Test
	void testApplyForNewLoan_Fail_InvalidUser() {
		when(authService.checkIfUserExists(any(String.class))).thenReturn(false);
		ResponseEntity<LoanApplicationResponse> resp = loanController.applyForNewLoan(jwtToken, new LoanApplicationRequest(username, 1000D, 5));
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		LoanApplicationResponse loanApplicationResp = resp.getBody();
		assertFalse(loanApplicationResp.getIsApplied());
		assertNull(loanApplicationResp.getLoanId());
		assertNull(loanApplicationResp.getLoanDto());
		assertEquals(LoanConstants.INVALID_USERNAME, loanApplicationResp.getMessage());
	}
	
	@Test
	void testApplyForNewLoan_Fail_InvalidJwtToken() {
		when(authService.checkIfUserExists(any(String.class))).thenReturn(true);
		when(authService.getUserRole(any(String.class))).thenReturn(AuthConstants.CUSTOMER_ROLE);
		when(jwtTokenUtil.validateToken(any(String.class), any(String.class), any(String.class))).thenReturn(false);
		ResponseEntity<LoanApplicationResponse> resp = loanController.applyForNewLoan(jwtToken, new LoanApplicationRequest(username, 1000D, 5));
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		LoanApplicationResponse loanApplicationResp = resp.getBody();
		assertFalse(loanApplicationResp.getIsApplied());
		assertNull(loanApplicationResp.getLoanId());
		assertNull(loanApplicationResp.getLoanDto());
		assertEquals(LoanConstants.INVALID_AUTH_TOKEN, loanApplicationResp.getMessage());
	}
	
	@Test
	void testApplyForNewLoan_DelegateToLoanService_Fail_ThrowExceptionByLoanService() {
		when(authService.checkIfUserExists(any(String.class))).thenReturn(true);
		when(authService.getUserRole(any(String.class))).thenReturn(AuthConstants.CUSTOMER_ROLE);
		when(jwtTokenUtil.validateToken(any(String.class), any(String.class), any(String.class))).thenReturn(true);
		when(loanService.applyForNewLoan(any(LoanApplicationRequest.class))).thenThrow(exception);
		ResponseEntity<LoanApplicationResponse> resp = loanController.applyForNewLoan(jwtToken, new LoanApplicationRequest(username, 1000D, 5));
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
		LoanApplicationResponse loanApplicationResp = resp.getBody();
		assertFalse(loanApplicationResp.getIsApplied());
		assertNull(loanApplicationResp.getLoanId());
		assertNull(loanApplicationResp.getLoanDto());
		assertEquals(LoanConstants.SERVER_FAILURE, loanApplicationResp.getMessage());
	}

	@Test
	void testApproveLoan_DelegateToLoanService_Success() {
		when(authService.checkIfUserExists(any(String.class))).thenReturn(true);
		when(authService.getUserRole(any(String.class))).thenReturn(AuthConstants.ADMIN_ROLE);
		when(jwtTokenUtil.validateToken(any(String.class), any(String.class), any(String.class))).thenReturn(true);
		when(loanService.approveLoans(any(List.class))).thenReturn(new LoanApprovalResponse(""));
		ResponseEntity<LoanApprovalResponse> resp = loanController.approveLoan(jwtToken, new LoanApprovalRequest(username, new ArrayList<Integer>()));
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		LoanApprovalResponse loanApprovalResp = resp.getBody();
		assertNotNull(loanApprovalResp);
	}
	
	@Test
	void testApproveLoan_Fail_InvalidUser() {
		when(authService.checkIfUserExists(any(String.class))).thenReturn(false);
		ResponseEntity<LoanApprovalResponse> resp = loanController.approveLoan(jwtToken, new LoanApprovalRequest(username, new ArrayList<Integer>()));
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		LoanApprovalResponse loanApprovalResp = resp.getBody();
		assertEquals(LoanConstants.INVALID_USERNAME, loanApprovalResp.getApprovalMessages());
	}
	
	@Test
	void testApproveLoan_Fail_InvalidRole() {
		when(authService.checkIfUserExists(any(String.class))).thenReturn(true);
		when(authService.getUserRole(any(String.class))).thenReturn(AuthConstants.CUSTOMER_ROLE);
		ResponseEntity<LoanApprovalResponse> resp = loanController.approveLoan(jwtToken, new LoanApprovalRequest(username, new ArrayList<Integer>()));
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		LoanApprovalResponse loanApprovalResp = resp.getBody();
		assertEquals(LoanConstants.NOT_AN_ADMIN, loanApprovalResp.getApprovalMessages());
	}
	
	@Test
	void testApproveLoan_Fail_InvalidJwtToken() {
		when(authService.checkIfUserExists(any(String.class))).thenReturn(true);
		when(authService.getUserRole(any(String.class))).thenReturn(AuthConstants.ADMIN_ROLE);
		when(jwtTokenUtil.validateToken(any(String.class), any(String.class), any(String.class))).thenReturn(false);
		ResponseEntity<LoanApprovalResponse> resp = loanController.approveLoan(jwtToken, new LoanApprovalRequest(username, new ArrayList<Integer>()));
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		LoanApprovalResponse loanApprovalResp = resp.getBody();
		assertEquals(LoanConstants.INVALID_AUTH_TOKEN, loanApprovalResp.getApprovalMessages());
	}
	
	@Test
	void testApproveLoan_DelegateToLoanService_Fail_ThrowExceptionByLoanService() {
		when(authService.checkIfUserExists(any(String.class))).thenReturn(true);
		when(authService.getUserRole(any(String.class))).thenReturn(AuthConstants.ADMIN_ROLE);
		when(jwtTokenUtil.validateToken(any(String.class), any(String.class), any(String.class))).thenReturn(true);
		when(loanService.approveLoans(any(List.class))).thenThrow(exception);
		ResponseEntity<LoanApprovalResponse> resp = loanController.approveLoan(jwtToken, new LoanApprovalRequest(username, new ArrayList<Integer>()));
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
		LoanApprovalResponse loanApprovalResp = resp.getBody();
		assertEquals(LoanConstants.SERVER_FAILURE, loanApprovalResp.getApprovalMessages());
	}

	@Test
	void testRepayLoan_DelegateToLoanService_Success() {
		when(authService.checkIfUserExists(any(String.class))).thenReturn(true);
		when(authService.getUserRole(any(String.class))).thenReturn(AuthConstants.CUSTOMER_ROLE);
		when(jwtTokenUtil.validateToken(any(String.class), any(String.class), any(String.class))).thenReturn(true);
		when(loanService.repayLoan(any(LoanRepaymentRequest.class))).thenReturn(new LoanRepaymentResponse(true, new LoanDto(), LoanConstants.LOAN_REPAYMENT_SUCCESS));
		ResponseEntity<LoanRepaymentResponse> resp = loanController.repayLoan(jwtToken, new LoanRepaymentRequest(username, 1, 500D));
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		LoanRepaymentResponse loanRepaymentResponse = resp.getBody();
		assertTrue(loanRepaymentResponse.getIsRepaymentSuccessful());
		assertNotNull(loanRepaymentResponse.getLoanDto());
		assertEquals(LoanConstants.LOAN_REPAYMENT_SUCCESS, loanRepaymentResponse.getMessage());
	}
	
	@Test
	void testRepayLoan_Fail_InvalidUser() {
		when(authService.checkIfUserExists(any(String.class))).thenReturn(false);
		ResponseEntity<LoanRepaymentResponse> resp = loanController.repayLoan(jwtToken, new LoanRepaymentRequest(username, 1, 500D));
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		LoanRepaymentResponse loanRepaymentResponse = resp.getBody();
		assertFalse(loanRepaymentResponse.getIsRepaymentSuccessful());
		assertNull(loanRepaymentResponse.getLoanDto());
		assertEquals(LoanConstants.INVALID_USERNAME, loanRepaymentResponse.getMessage());
	}
	
	@Test
	void testRepayLoan_Fail_InvalidJwtToken() {
		when(authService.checkIfUserExists(any(String.class))).thenReturn(true);
		when(authService.getUserRole(any(String.class))).thenReturn(AuthConstants.CUSTOMER_ROLE);
		when(jwtTokenUtil.validateToken(any(String.class), any(String.class), any(String.class))).thenReturn(false);
		ResponseEntity<LoanRepaymentResponse> resp = loanController.repayLoan(jwtToken, new LoanRepaymentRequest(username, 1, 500D));
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		LoanRepaymentResponse loanRepaymentResponse = resp.getBody();
		assertFalse(loanRepaymentResponse.getIsRepaymentSuccessful());
		assertNull(loanRepaymentResponse.getLoanDto());
		assertEquals(LoanConstants.INVALID_AUTH_TOKEN, loanRepaymentResponse.getMessage());
	}
	
	@Test
	void testRepayLoan_DelegateToLoanService_Fail_ThrowExceptionByLoanService() {
		when(authService.checkIfUserExists(any(String.class))).thenReturn(true);
		when(authService.getUserRole(any(String.class))).thenReturn(AuthConstants.CUSTOMER_ROLE);
		when(jwtTokenUtil.validateToken(any(String.class), any(String.class), any(String.class))).thenReturn(true);
		when(loanService.repayLoan(any(LoanRepaymentRequest.class))).thenThrow(exception);
		ResponseEntity<LoanRepaymentResponse> resp = loanController.repayLoan(jwtToken, new LoanRepaymentRequest(username, 1, 500D));
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
		LoanRepaymentResponse loanRepaymentResponse = resp.getBody();
		assertFalse(loanRepaymentResponse.getIsRepaymentSuccessful());
		assertNull(loanRepaymentResponse.getLoanDto());
		assertEquals(LoanConstants.SERVER_FAILURE, loanRepaymentResponse.getMessage());
	}

}

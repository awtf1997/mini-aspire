package com.aspire.miniaspireuserloansapp.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aspire.miniaspireuserloansapp.constants.AuthConstants;
import com.aspire.miniaspireuserloansapp.constants.LoanConstants;
import com.aspire.miniaspireuserloansapp.model.dto.loans.LoanApplicationRequest;
import com.aspire.miniaspireuserloansapp.model.dto.loans.LoanApplicationResponse;
import com.aspire.miniaspireuserloansapp.model.dto.loans.LoanApprovalRequest;
import com.aspire.miniaspireuserloansapp.model.dto.loans.LoanApprovalResponse;
import com.aspire.miniaspireuserloansapp.model.dto.loans.LoanRepaymentRequest;
import com.aspire.miniaspireuserloansapp.model.dto.loans.LoanRepaymentResponse;
import com.aspire.miniaspireuserloansapp.model.dto.loans.UserLoans;
import com.aspire.miniaspireuserloansapp.service.AuthService;
import com.aspire.miniaspireuserloansapp.service.LoanService;
import com.aspire.miniaspireuserloansapp.utils.JwtTokenUtil;

@RestController
@RequestMapping(path = "/api/v1/mini-aspire/loan")
public class LoanController {
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private LoanService loanService;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil; 
	
	@GetMapping(path = "/{username}")
	public ResponseEntity<UserLoans> getLoanDetails(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String jwtToken, 
			@PathVariable(name = "username") String username) {
		ResponseEntity<UserLoans> resp = null;
		try {
			Boolean isUserValid = authService.checkIfUserExists(username);
			if (!isUserValid) {
				UserLoans loans = new UserLoans(username, null, LoanConstants.INVALID_USERNAME);
				resp = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(loans);
				return resp;
			}
			String role = authService.getUserRole(username);
			Boolean isTokenValid = jwtTokenUtil.validateToken(jwtToken, username, role);
			if (!isTokenValid) {
				UserLoans loans = new UserLoans(username, null, LoanConstants.INVALID_AUTH_TOKEN);
				resp = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(loans);
				return resp;
			}
			UserLoans loans = loanService.getLoansOfUser(username);
			resp = ResponseEntity.status(HttpStatus.OK).body(loans);
		} catch (Exception e) {
			e.printStackTrace();
			UserLoans loans = new UserLoans(username, null, LoanConstants.SERVER_FAILURE);
			resp = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(loans);
		}
		return resp;
	}
	
	@PostMapping(path = "/apply")
	public ResponseEntity<LoanApplicationResponse> applyForNewLoan(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String jwtToken, 
			@RequestBody LoanApplicationRequest loanApplicationRequest) {
		ResponseEntity<LoanApplicationResponse> resp = null;
		String username = loanApplicationRequest.getUsername();
		try {
			Boolean isUserValid = authService.checkIfUserExists(username);
			if (!isUserValid) {
				LoanApplicationResponse loanApplicationResponse = 
						new LoanApplicationResponse(false, null, null, LoanConstants.INVALID_USERNAME);
				resp = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(loanApplicationResponse);
				return resp;
			}
			String role = authService.getUserRole(username);
			Boolean isTokenValid = jwtTokenUtil.validateToken(jwtToken, username, role);
			if (!isTokenValid) {
				LoanApplicationResponse loanApplicationResponse = 
						new LoanApplicationResponse(false, null, null, LoanConstants.INVALID_AUTH_TOKEN);
				resp = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(loanApplicationResponse);
				return resp;
			}
			LoanApplicationResponse loanApplicationResponse = loanService.applyForNewLoan(loanApplicationRequest);
			resp = ResponseEntity.status(HttpStatus.OK).body(loanApplicationResponse);
		} catch (Exception e) {
			LoanApplicationResponse loanApplicationResponse = 
					new LoanApplicationResponse(false, null, null, LoanConstants.SERVER_FAILURE);
			resp = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(loanApplicationResponse);
		}
		return resp;
	}
	
	@PutMapping(path = "/activate")
	public ResponseEntity<LoanApprovalResponse> approveLoan(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String jwtToken,
			@RequestBody LoanApprovalRequest loanApprovalRequest) {
		ResponseEntity<LoanApprovalResponse> resp = null;
		String username = loanApprovalRequest.getUsername();
		try {
			Boolean isUserValid = authService.checkIfUserExists(username);
			if (!isUserValid) {
				LoanApprovalResponse loanApprovalResponse = 
						new LoanApprovalResponse(LoanConstants.INVALID_USERNAME);
				resp = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(loanApprovalResponse);
				return resp;
			}
			String role = authService.getUserRole(username);
			if (!role.equals(AuthConstants.ADMIN_ROLE)) {
				LoanApprovalResponse loanApprovalResponse = 
						new LoanApprovalResponse(LoanConstants.NOT_AN_ADMIN);
				resp = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(loanApprovalResponse);
				return resp;
			}
			Boolean isTokenValid = jwtTokenUtil.validateToken(jwtToken, username, role);
			if (!isTokenValid) {
				LoanApprovalResponse loanApprovalResponse = 
						new LoanApprovalResponse(LoanConstants.INVALID_AUTH_TOKEN);
				resp = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(loanApprovalResponse);
				return resp;
			}
			LoanApprovalResponse loanApprovalResponse = loanService.approveLoans(loanApprovalRequest.getLoanIds());
			resp = ResponseEntity.status(HttpStatus.OK).body(loanApprovalResponse);
		} catch (Exception e) {
			LoanApprovalResponse loanApprovalResponse = 
					new LoanApprovalResponse(LoanConstants.SERVER_FAILURE);
			resp = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(loanApprovalResponse);
		}
		return resp;
	}
	
	@PutMapping(path = "/repay")
	public ResponseEntity<LoanRepaymentResponse> repayLoan(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String jwtToken,
			@RequestBody LoanRepaymentRequest loanRepaymentRequest) {
		ResponseEntity<LoanRepaymentResponse> resp = null;
		String username = loanRepaymentRequest.getUsername();
		try {
			Boolean isUserValid = authService.checkIfUserExists(username);
			if (!isUserValid) {
				LoanRepaymentResponse loanRepaymentResponse = 
						new LoanRepaymentResponse(false, null, LoanConstants.INVALID_USERNAME);
				resp = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(loanRepaymentResponse);
				return resp;
			}
			String role = authService.getUserRole(username);
			Boolean isTokenValid = jwtTokenUtil.validateToken(jwtToken, username, role);
			if (!isTokenValid) {
				LoanRepaymentResponse loanRepaymentResponse =  
						new LoanRepaymentResponse(false, null, LoanConstants.INVALID_AUTH_TOKEN);
				resp = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(loanRepaymentResponse);
				return resp;
			}
			LoanRepaymentResponse loanRepaymentResponse = loanService.repayLoan(loanRepaymentRequest);
			resp = ResponseEntity.status(HttpStatus.OK).body(loanRepaymentResponse);
		} catch (Exception e) {
			LoanRepaymentResponse loanRepaymentResponse = 
					new LoanRepaymentResponse(false, null, LoanConstants.SERVER_FAILURE);
			resp = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(loanRepaymentResponse);
		}
		return resp;
	}
	
	
}

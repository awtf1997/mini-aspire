package com.aspire.miniaspireuserloansapp.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aspire.miniaspireuserloansapp.constants.AuthConstants;
import com.aspire.miniaspireuserloansapp.constants.LoanConstants;
import com.aspire.miniaspireuserloansapp.model.dto.loans.Emi;
import com.aspire.miniaspireuserloansapp.model.dto.loans.LoanApplicationRequest;
import com.aspire.miniaspireuserloansapp.model.dto.loans.LoanApplicationResponse;
import com.aspire.miniaspireuserloansapp.model.dto.loans.LoanApprovalResponse;
import com.aspire.miniaspireuserloansapp.model.dto.loans.LoanDto;
import com.aspire.miniaspireuserloansapp.model.dto.loans.LoanRepaymentRequest;
import com.aspire.miniaspireuserloansapp.model.dto.loans.LoanRepaymentResponse;
import com.aspire.miniaspireuserloansapp.model.dto.loans.UserLoans;
import com.aspire.miniaspireuserloansapp.model.entity.Loan;
import com.aspire.miniaspireuserloansapp.model.entity.User;
import com.aspire.miniaspireuserloansapp.repository.LoanRepository;
import com.aspire.miniaspireuserloansapp.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class LoanServiceImplTest {
	
	@Mock
	private UserRepository userRepository;

	@Mock
	private LoanRepository loanRepository;
	
	@InjectMocks
	private LoanServiceImpl loanService;
	
	private User user;
	private User user1;
	
	private Loan loan1;
	private LoanDto loan1Dto;
	private List<Emi> loan1DtoEmis;
	
	private LoanApplicationRequest loanApplyReq;
	private Loan loan2;
	private LoanDto loan2Dto;
	private List<Emi> loan2DtoEmis;
	
	private LoanRepaymentRequest loan1RepaymentReq1;
	private LoanDto loan1DtoAfterARepayment;
	private List<Emi> loan1DtoEmisAfterARepayment;
	
	private LoanRepaymentRequest loan1RepaymentReq2;
	private LoanRepaymentRequest loan1RepaymentReq3;
	private LoanRepaymentRequest loan1RepaymentReq4;
	
	private Exception exception;
	
	@BeforeEach
	void setUp() throws Exception {
		user = new User("abc", "1234567890", AuthConstants.ADMIN_ROLE);
		user1 = new User("cde", "123478901", AuthConstants.CUSTOMER_ROLE);
		
		exception = new RuntimeException("Test exception");
		
		loan1 = new Loan(user, true, 1000D, 500D, 3, 2, "02-06-2023/09-06-2023/16-06-2023", "500.0/");
		loan1.setId(1);
		loan1Dto = new LoanDto("abc", 1000D, 500D, 3, 2, true);
		loan1DtoEmis = List.of(new Emi(500D, "02-06-2023", true), new Emi(250D, "09-06-2023", false), new Emi(250D, "16-06-2023", false));
		
		loanApplyReq = new LoanApplicationRequest("abc", 2000D, 5);
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		loan2DtoEmis = new ArrayList<>();
		StringBuilder dateString = new StringBuilder("");
		for (int i = 0; i < 5; i++) {
			date.setDate(date.getDate() + 7);
			dateString.append(df.format(date));
			if (i < 4) dateString.append("/");
			loan2DtoEmis.add(new Emi(400D, df.format(date), false));
		}
		loan2 = new Loan(user, false, 2000D, 0D, 5, 5, dateString.toString(), "");		
		loan2.setId(2);
		loan2Dto = new LoanDto("abc", 2000D, 0D, 5, 5, false);
		
		loan1RepaymentReq1 = new LoanRepaymentRequest("abc", 1, 250D);
		loan1DtoAfterARepayment = new LoanDto("abc", 1000D, 750D, 3, 1, true);
		loan1DtoEmisAfterARepayment = List.of(new Emi(500D, "02-06-2023", true), new Emi(250D, "09-06-2023", true), new Emi(250D, "16-06-2023", false));
		
		loan1RepaymentReq2 = new LoanRepaymentRequest("cde", 1, 250D);
		loan1RepaymentReq3 = new LoanRepaymentRequest("abc", 1, 200D);
		loan1RepaymentReq4 = new LoanRepaymentRequest("abc", 1, 600D);
		
	}

	@Test
	void testGetLoansOfUser_FetchSuccess() {
		Optional<User> userOptional = Optional.of(user);
		when(userRepository.findUser("abc")).thenReturn(userOptional);
		when(loanRepository.findAllByUserAndIsActive(user, true)).thenReturn(List.of(loan1));
		UserLoans userLoans = loanService.getLoansOfUser("abc");
		assertEquals("abc", userLoans.getUsername());
		assertEquals(LoanConstants.LOANS_FETCH_SUCCESS, userLoans.getMessage());
		List<LoanDto> fetchedLoanDtos = userLoans.getLoans();
		assertEquals(1, fetchedLoanDtos.size());
		LoanDto fetchedLoanDto = fetchedLoanDtos.get(0);
		assertEquals(loan1Dto.getUsername(), fetchedLoanDto.getUsername());
		assertEquals(loan1Dto.getDisbersedAmount(), fetchedLoanDto.getDisbersedAmount());
		assertEquals(loan1Dto.getRepayedAmount(), fetchedLoanDto.getRepayedAmount());
		assertEquals(loan1Dto.getTotalTerm(), fetchedLoanDto.getTotalTerm());
		assertEquals(loan1Dto.getRemainingTerm(), fetchedLoanDto.getRemainingTerm());
		assertEquals(loan1Dto.getIsActive(), fetchedLoanDto.getIsActive());
		assertEquals(loan1DtoEmis.size(), fetchedLoanDto.getEmis().size());
		List<Emi> fetchedEmis = fetchedLoanDto.getEmis();
		for (int i = 0; i < 3; i++) {
			Emi loan1ActualEmi = loan1DtoEmis.get(i);
			Emi loan1FetchedEmi = fetchedEmis.get(i);
			assertEquals(loan1ActualEmi.getEmiAmount(), loan1FetchedEmi.getEmiAmount());
			assertEquals(loan1ActualEmi.getEmiDate(), loan1FetchedEmi.getEmiDate());
			assertEquals(loan1ActualEmi.getIsPaid(), loan1FetchedEmi.getIsPaid());
		}
	}
	
	@Test
	void testGetLoansOfUser_FetchFail_ThrowExceptionWhenTryingToFetchUserData() {
		when(userRepository.findUser("abc")).thenThrow(exception);
		try {
			loanService.getLoansOfUser("abc");
		} catch (Exception e) {
			assertTrue(e instanceof RuntimeException);
			assertEquals("Test exception", e.getMessage());
		}
	}
	
	@Test
	void testGetLoansOfUser_FetchFail_ThrowExceptionWhenTryingToFetchLoanData() {
		Optional<User> userOptional = Optional.of(user);
		when(userRepository.findUser("abc")).thenReturn(userOptional);
		when(loanRepository.findAllByUserAndIsActive(user, true)).thenThrow(exception);
		try {
			loanService.getLoansOfUser("abc");
		} catch (Exception e) {
			assertTrue(e instanceof RuntimeException);
			assertEquals("Test exception", e.getMessage());
		}
	}
	
	@Test
	void testApplyForNewLoan_Success() {
		Optional<User> userOptional = Optional.of(user);
		when(userRepository.findUser("abc")).thenReturn(userOptional);
		when(loanRepository.save(any(Loan.class))).thenReturn(loan2);
		LoanApplicationResponse loanApplyResp = loanService.applyForNewLoan(loanApplyReq);
		assertTrue(loanApplyResp.getIsApplied());
		assertEquals(2, loanApplyResp.getLoanId());
		assertEquals(LoanConstants.LOAN_APPLICATION_SUCCESS, loanApplyResp.getMessage());
		LoanDto fetchedDto = loanApplyResp.getLoanDto();
		assertEquals(loan2Dto.getUsername(), fetchedDto.getUsername());
		assertEquals(loan2Dto.getDisbersedAmount(), fetchedDto.getDisbersedAmount());
		assertEquals(loan2Dto.getRepayedAmount(), fetchedDto.getRepayedAmount());
		assertEquals(loan2Dto.getTotalTerm(), fetchedDto.getTotalTerm());
		assertEquals(loan2Dto.getRemainingTerm(), fetchedDto.getRemainingTerm());
		assertEquals(loan2Dto.getIsActive(), fetchedDto.getIsActive());
		List<Emi> fetchedEmis = fetchedDto.getEmis();
		assertEquals(loan2DtoEmis.size(), fetchedEmis.size());
		for (int i = 0; i < loan2DtoEmis.size(); i++) {
			Emi loan2ActualEmi = loan2DtoEmis.get(i);
			Emi loan2FetchedEmi = fetchedEmis.get(i);
			assertEquals(loan2ActualEmi.getEmiAmount(), loan2FetchedEmi.getEmiAmount());
			assertEquals(loan2ActualEmi.getEmiDate(), loan2FetchedEmi.getEmiDate());
			assertEquals(loan2ActualEmi.getIsPaid(), loan2FetchedEmi.getIsPaid());
		}
	}
	
	@Test
	void testApplyForNewLoan_Fail_ThrowExceptionWhenTryingToFetchUserData() {
		when(userRepository.findUser("abc")).thenThrow(exception);
		try {
			loanService.applyForNewLoan(loanApplyReq);
		} catch (Exception e) {
			assertTrue(e instanceof RuntimeException);
			assertEquals("Test exception", e.getMessage());
		}
	}
	
	@Test
	void testApplyForNewLoan_Fail_ThrowExceptionWhenTryingToPersistLoanData() {
		Optional<User> userOptional = Optional.of(user);
		when(userRepository.findUser("abc")).thenReturn(userOptional);
		when(loanRepository.save(any(Loan.class))).thenThrow(exception);
		try {
			loanService.applyForNewLoan(loanApplyReq);
		} catch (Exception e) {
			assertTrue(e instanceof RuntimeException);
			assertEquals("Test exception", e.getMessage());
		}
	}

	@Test
	void testApproveLoans_Success() {
		Optional<Loan> loan1Optional = Optional.of(loan1);
		when(loanRepository.findById(1)).thenReturn(loan1Optional);
		Optional<Loan> loan2Optional = Optional.of(loan2);
		when(loanRepository.findById(2)).thenReturn(loan2Optional);
		when(loanRepository.save(loan2)).thenReturn(null);
		Optional<Loan> loan3Optional = Optional.ofNullable(null);
		when(loanRepository.findById(3)).thenReturn(loan3Optional);
		List<Integer> loanIds = List.of(1, 2, 3);
		LoanApprovalResponse loanApprovalResp = loanService.approveLoans(loanIds);
		assertTrue(loan2.getIsActive());
		assertNotNull(loanApprovalResp.getApprovalMessages());
		assertTrue(loanApprovalResp.getApprovalMessages().length() > 0);
		assertEquals("1: " + LoanConstants.LOAN_ALREADY_APPROVED 
				+ ",2: " + LoanConstants.LOAN_APPROVAL_SUCCESS 
				+ ",3: " + LoanConstants.INVALID_LOAN_ID + ",", loanApprovalResp.getApprovalMessages());
	}
	
	@Test
	void testApproveLoans_Fail_ThrowExceptionWhilePersistingLoanId2Approval() {
		Optional<Loan> loan1Optional = Optional.of(loan1);
		when(loanRepository.findById(1)).thenReturn(loan1Optional);
		Optional<Loan> loan2Optional = Optional.of(loan2);
		when(loanRepository.findById(2)).thenReturn(loan2Optional);
		when(loanRepository.save(loan2)).thenThrow(exception);
		Optional<Loan> loan3Optional = Optional.ofNullable(null);
		when(loanRepository.findById(3)).thenReturn(loan3Optional);
		List<Integer> loanIds = List.of(1, 2, 3);
		LoanApprovalResponse loanApprovalResp = loanService.approveLoans(loanIds);
		assertTrue(loan2.getIsActive());
		assertNotNull(loanApprovalResp.getApprovalMessages());
		assertTrue(loanApprovalResp.getApprovalMessages().length() > 0);
		assertEquals("1: " + LoanConstants.LOAN_ALREADY_APPROVED 
				+ ",2: " + LoanConstants.LOAN_APPROVAL_FAILED
				+ ",3: " + LoanConstants.INVALID_LOAN_ID + ",", loanApprovalResp.getApprovalMessages());
	}
	
	@Test
	void testRepayLoan_RepayLoan1_Success() {
		Optional<Loan> loan1Optional = Optional.of(loan1);
		when(loanRepository.findById(1)).thenReturn(loan1Optional);
		Optional<User> userOptional = Optional.of(user);
		when(userRepository.findUser("abc")).thenReturn(userOptional);
		when(loanRepository.save(loan1)).thenReturn(loan1);
		LoanRepaymentResponse loanRepaymentResp = loanService.repayLoan(loan1RepaymentReq1);
		assertEquals("500.0/250.0/", loan1.getRepayments());
		assertTrue(loanRepaymentResp.getIsRepaymentSuccessful());
		assertEquals(LoanConstants.LOAN_REPAYMENT_SUCCESS, loanRepaymentResp.getMessage());
		LoanDto fetchedLoanDto = loanRepaymentResp.getLoanDto();
		assertEquals(loan1DtoAfterARepayment.getUsername(), fetchedLoanDto.getUsername());
		assertEquals(loan1DtoAfterARepayment.getDisbersedAmount(), fetchedLoanDto.getDisbersedAmount());
		assertEquals(loan1DtoAfterARepayment.getRepayedAmount(), fetchedLoanDto.getRepayedAmount());
		assertEquals(loan1DtoAfterARepayment.getTotalTerm(), fetchedLoanDto.getTotalTerm());
		assertEquals(loan1DtoAfterARepayment.getRemainingTerm(), fetchedLoanDto.getRemainingTerm());
		assertEquals(loan1DtoAfterARepayment.getIsActive(), fetchedLoanDto.getIsActive());
		assertEquals(loan1DtoEmisAfterARepayment.size(), fetchedLoanDto.getEmis().size());
		List<Emi> fetchedEmis = fetchedLoanDto.getEmis();
		for (int i = 0; i < 3; i++) {
			Emi loan1ActualEmi = loan1DtoEmisAfterARepayment.get(i);
			Emi loan1FetchedEmi = fetchedEmis.get(i);
			assertEquals(loan1ActualEmi.getEmiAmount(), loan1FetchedEmi.getEmiAmount());
			assertEquals(loan1ActualEmi.getEmiDate(), loan1FetchedEmi.getEmiDate());
			assertEquals(loan1ActualEmi.getIsPaid(), loan1FetchedEmi.getIsPaid());
		}
	}
	
	@Test
	void testRepayLoan_RepayLoan1_Fail_InvalidLoanId() {
		Optional<Loan> loan1Optional = Optional.ofNullable(null);
		when(loanRepository.findById(1)).thenReturn(loan1Optional);
		LoanRepaymentResponse loanRepaymentResp = loanService.repayLoan(loan1RepaymentReq1);
		assertFalse(loanRepaymentResp.getIsRepaymentSuccessful());
		assertEquals(LoanConstants.INVALID_LOAN_ID, loanRepaymentResp.getMessage());
		assertNull(loanRepaymentResp.getLoanDto());
	}
	
	@Test
	void testRepayLoan_RepayLoan1_Fail_UserDifferentAndNotAnAdmin() {
		Optional<Loan> loan1Optional = Optional.of(loan1);
		when(loanRepository.findById(1)).thenReturn(loan1Optional);
		Optional<User> user1Optional = Optional.of(user1);
		when(userRepository.findUser("cde")).thenReturn(user1Optional);
		LoanRepaymentResponse loanRepaymentResp = loanService.repayLoan(loan1RepaymentReq2);
		assertFalse(loanRepaymentResp.getIsRepaymentSuccessful());
		assertEquals(LoanConstants.USER_NOT_PERMITTED_TO_ACCESS_THIS_LOAN, loanRepaymentResp.getMessage());
		assertNull(loanRepaymentResp.getLoanDto());
	}
	
	@Test
	void testRepayLoan_RepayLoan1_Fail_RepaymentAmountLessThanEmi() {
		Optional<Loan> loan1Optional = Optional.of(loan1);
		when(loanRepository.findById(1)).thenReturn(loan1Optional);
		Optional<User> userOptional = Optional.of(user);
		when(userRepository.findUser("abc")).thenReturn(userOptional);
		LoanRepaymentResponse loanRepaymentResp = loanService.repayLoan(loan1RepaymentReq3);
		assertFalse(loanRepaymentResp.getIsRepaymentSuccessful());
		assertEquals(LoanConstants.REPAYMENT_AMOUNT_LESS_THAN_EMI_AMOUNT, loanRepaymentResp.getMessage());
		assertNull(loanRepaymentResp.getLoanDto());
	}
	
	@Test
	void testRepayLoan_RepayLoan1_Fail_RepaymentAmountMoreThanRemainingEmi() {
		Optional<Loan> loan1Optional = Optional.of(loan1);
		when(loanRepository.findById(1)).thenReturn(loan1Optional);
		Optional<User> userOptional = Optional.of(user);
		when(userRepository.findUser("abc")).thenReturn(userOptional);
		LoanRepaymentResponse loanRepaymentResp = loanService.repayLoan(loan1RepaymentReq4);
		assertFalse(loanRepaymentResp.getIsRepaymentSuccessful());
		assertEquals(LoanConstants.REPAYMENT_AMOUNT_MORE_THAN_REMAINING_AMOUNT, loanRepaymentResp.getMessage());
		assertNull(loanRepaymentResp.getLoanDto());
	}
	
	@Test
	void testRepayLoan_RepayLoan1_Fail_ThrowExceptionWhenPersistingLoanRepayment() {
		Optional<Loan> loan1Optional = Optional.of(loan1);
		when(loanRepository.findById(1)).thenReturn(loan1Optional);
		Optional<User> userOptional = Optional.of(user);
		when(userRepository.findUser("abc")).thenReturn(userOptional);
		when(loanRepository.save(loan1)).thenThrow(exception);
		try{
			loanService.repayLoan(loan1RepaymentReq1);
		} catch (Exception e) {
			assertTrue(e instanceof RuntimeException);
			assertEquals("Test exception", e.getMessage());
		}
	}

}

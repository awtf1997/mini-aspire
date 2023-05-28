package com.aspire.miniaspireuserloansapp.integrationtests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
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
import com.aspire.miniaspireuserloansapp.constants.LoanConstants;
import com.aspire.miniaspireuserloansapp.model.dto.loans.Emi;
import com.aspire.miniaspireuserloansapp.model.dto.loans.LoanApplicationRequest;
import com.aspire.miniaspireuserloansapp.model.dto.loans.LoanApplicationResponse;
import com.aspire.miniaspireuserloansapp.model.dto.loans.LoanApprovalRequest;
import com.aspire.miniaspireuserloansapp.model.dto.loans.LoanApprovalResponse;
import com.aspire.miniaspireuserloansapp.model.dto.loans.LoanDto;
import com.aspire.miniaspireuserloansapp.model.dto.loans.LoanRepaymentRequest;
import com.aspire.miniaspireuserloansapp.model.dto.loans.LoanRepaymentResponse;
import com.aspire.miniaspireuserloansapp.model.dto.loans.UserLoans;
import com.aspire.miniaspireuserloansapp.model.entity.User;
import com.aspire.miniaspireuserloansapp.repository.LoanRepository;
import com.aspire.miniaspireuserloansapp.repository.UserRepository;
import com.aspire.miniaspireuserloansapp.utils.JwtTokenUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = MiniAspireUserLoansAppApplication.class)
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
public class LoanFlowTests {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private LoanRepository loanRepository;
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	private User user1 = new User("abc", "1234567890", AuthConstants.ADMIN_ROLE);
	private String jwtTokenOfUser1;
	
	private User user2 = new User("cde", "123478901", AuthConstants.CUSTOMER_ROLE);
	private String jwtTokenOfUser2;
	
	@BeforeEach
	void setUp() {
		if (userRepository.findUser(user1.getUsername()).isEmpty()) userRepository.save(user1);
		if (userRepository.findUser(user2.getUsername()).isEmpty()) userRepository.save(user2);
		jwtTokenOfUser1 = jwtTokenUtil.generateToken(user1);
		jwtTokenOfUser2 = jwtTokenUtil.generateToken(user2);
	}
	
	@Test
	@Order(1)
	void testGetLoans() throws Exception {
		MvcResult mvcResult1 = mockMvc.perform(
				MockMvcRequestBuilders
				.get("/api/v1/mini-aspire/loan/abc")
				.header(HttpHeaders.AUTHORIZATION, jwtTokenOfUser1)
				.accept(MediaType.APPLICATION_JSON)
			).andReturn();
		assertEquals(HttpStatus.OK.value(), mvcResult1.getResponse().getStatus());
		UserLoans user1Loans = objectMapper.readValue(mvcResult1.getResponse().getContentAsString(), UserLoans.class);
		assertEquals("abc", user1Loans.getUsername());
		assertEquals(LoanConstants.LOANS_FETCH_SUCCESS, user1Loans.getMessage());
		assertNotNull(user1Loans.getLoans());
		assertTrue(user1Loans.getLoans().size() == 0);
		
		MvcResult mvcResult2 = mockMvc.perform(
				MockMvcRequestBuilders
				.get("/api/v1/mini-aspire/loan/cde")
				.header(HttpHeaders.AUTHORIZATION, jwtTokenOfUser2)
				.accept(MediaType.APPLICATION_JSON)
			).andReturn();
		assertEquals(HttpStatus.OK.value(), mvcResult2.getResponse().getStatus());
		UserLoans user2Loans = objectMapper.readValue(mvcResult2.getResponse().getContentAsString(), UserLoans.class);
		assertEquals("cde", user2Loans.getUsername());
		assertEquals(LoanConstants.LOANS_FETCH_SUCCESS, user2Loans.getMessage());
		assertNotNull(user2Loans.getLoans());
		assertTrue(user2Loans.getLoans().size() == 0);
	}
	
	@Test
	@Order(2)
	void testApplyNewLoan() throws JsonProcessingException, Exception {
		LoanApplicationRequest loanApplyReq = new LoanApplicationRequest("cde", 2000D, 5);
		MvcResult mvcResult = mockMvc.perform(
				MockMvcRequestBuilders
				.post("/api/v1/mini-aspire/loan/apply")
				.header(HttpHeaders.AUTHORIZATION, jwtTokenOfUser2)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loanApplyReq))
				.accept(MediaType.APPLICATION_JSON)
			).andReturn();
		assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
		LoanApplicationResponse loanApplyResp = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), LoanApplicationResponse.class);
		assertTrue(loanApplyResp.getIsApplied());
		assertEquals(1, loanApplyResp.getLoanId());
		assertEquals(LoanConstants.LOAN_APPLICATION_SUCCESS, loanApplyResp.getMessage());
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		List<Emi> loanDtoEmis = new ArrayList<>();
		StringBuilder dateString = new StringBuilder("");
		for (int i = 0; i < 5; i++) {
			date.setDate(date.getDate() + 7);
			dateString.append(df.format(date));
			if (i < 4) dateString.append("/");
			loanDtoEmis.add(new Emi(400D, df.format(date), false));
		}
		LoanDto loanDto = new LoanDto("cde", 2000D, 0D, 5, 5, false);
		LoanDto fetchedDto = loanApplyResp.getLoanDto();
		List<Emi> fetchedEmis = fetchedDto.getEmis();
		assertEquals(loanDto.getUsername(), fetchedDto.getUsername());
		assertEquals(loanDto.getDisbersedAmount(), fetchedDto.getDisbersedAmount());
		assertEquals(loanDto.getRepayedAmount(), fetchedDto.getRepayedAmount());
		assertEquals(loanDto.getTotalTerm(), fetchedDto.getTotalTerm());
		assertEquals(loanDto.getRemainingTerm(), fetchedDto.getRemainingTerm());
		assertEquals(loanDto.getIsActive(), fetchedDto.getIsActive());
		for (int i = 0; i < loanDtoEmis.size(); i++) {
			Emi loan2ActualEmi = loanDtoEmis.get(i);
			Emi loan2FetchedEmi = fetchedEmis.get(i);
			assertEquals(loan2ActualEmi.getEmiAmount(), loan2FetchedEmi.getEmiAmount());
			assertEquals(loan2ActualEmi.getEmiDate(), loan2FetchedEmi.getEmiDate());
			assertEquals(loan2ActualEmi.getIsPaid(), loan2FetchedEmi.getIsPaid());
		}
	}
	
	@Test
	@Order(3)
	void testApproveNewLoanByNonAdmin() throws JsonProcessingException, Exception {
		LoanApprovalRequest loanApprovalReq = new LoanApprovalRequest("cde", List.of(1, 2, 3));
		MvcResult mvcResult = mockMvc.perform(
				MockMvcRequestBuilders
				.put("/api/v1/mini-aspire/loan/activate")
				.header(HttpHeaders.AUTHORIZATION, jwtTokenOfUser2)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loanApprovalReq))
				.accept(MediaType.APPLICATION_JSON)
			).andReturn();
		assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
		LoanApprovalResponse loanApprovalResp = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), LoanApprovalResponse.class);
		assertEquals(LoanConstants.NOT_AN_ADMIN, loanApprovalResp.getApprovalMessages());
	}
	
	@Test
	@Order(4)
	void testApproveNewLoanByAdmin() throws JsonProcessingException, Exception {
		LoanApprovalRequest loanApprovalReq = new LoanApprovalRequest("abc", List.of(1, 2, 3));
		MvcResult mvcResult1 = mockMvc.perform(
				MockMvcRequestBuilders
				.put("/api/v1/mini-aspire/loan/activate")
				.header(HttpHeaders.AUTHORIZATION, jwtTokenOfUser1)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loanApprovalReq))
				.accept(MediaType.APPLICATION_JSON)
			).andReturn();
		assertEquals(HttpStatus.OK.value(), mvcResult1.getResponse().getStatus());
		LoanApprovalResponse loanApprovalResp = objectMapper.readValue(mvcResult1.getResponse().getContentAsString(), LoanApprovalResponse.class);
		assertEquals("1: " + LoanConstants.LOAN_APPROVAL_SUCCESS + ",2: " + LoanConstants.INVALID_LOAN_ID + ",3: " + LoanConstants.INVALID_LOAN_ID + ",", 
				loanApprovalResp.getApprovalMessages());
		
		MvcResult mvcResult2 = mockMvc.perform(
				MockMvcRequestBuilders
				.get("/api/v1/mini-aspire/loan/cde")
				.header(HttpHeaders.AUTHORIZATION, jwtTokenOfUser2)
				.accept(MediaType.APPLICATION_JSON)
			).andReturn();
		assertEquals(HttpStatus.OK.value(), mvcResult2.getResponse().getStatus());
		UserLoans user2Loans = objectMapper.readValue(mvcResult2.getResponse().getContentAsString(), UserLoans.class);
		assertEquals("cde", user2Loans.getUsername());
		assertEquals(LoanConstants.LOANS_FETCH_SUCCESS, user2Loans.getMessage());
		assertNotNull(user2Loans.getLoans());
		assertEquals(1, user2Loans.getLoans().size());
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		List<Emi> loanDtoEmis = new ArrayList<>();
		StringBuilder dateString = new StringBuilder("");
		for (int i = 0; i < 5; i++) {
			date.setDate(date.getDate() + 7);
			dateString.append(df.format(date));
			if (i < 4) dateString.append("/");
			loanDtoEmis.add(new Emi(400D, df.format(date), false));
		}
		LoanDto loanDto = new LoanDto("cde", 2000D, 0D, 5, 5, true);
		LoanDto fetchedDto = user2Loans.getLoans().get(0);
		List<Emi> fetchedEmis = fetchedDto.getEmis();
		assertEquals(loanDto.getUsername(), fetchedDto.getUsername());
		assertEquals(loanDto.getDisbersedAmount(), fetchedDto.getDisbersedAmount());
		assertEquals(loanDto.getRepayedAmount(), fetchedDto.getRepayedAmount());
		assertEquals(loanDto.getTotalTerm(), fetchedDto.getTotalTerm());
		assertEquals(loanDto.getRemainingTerm(), fetchedDto.getRemainingTerm());
		assertEquals(loanDto.getIsActive(), fetchedDto.getIsActive());
		for (int i = 0; i < loanDtoEmis.size(); i++) {
			Emi loan2ActualEmi = loanDtoEmis.get(i);
			Emi loan2FetchedEmi = fetchedEmis.get(i);
			assertEquals(loan2ActualEmi.getEmiAmount(), loan2FetchedEmi.getEmiAmount());
			assertEquals(loan2ActualEmi.getEmiDate(), loan2FetchedEmi.getEmiDate());
			assertEquals(loan2ActualEmi.getIsPaid(), loan2FetchedEmi.getIsPaid());
		} 
		
	}
	
	@Test
	@Order(5)
	void testRepayLoanByUser_AmountLessThanEMI() throws JsonProcessingException, Exception {
		LoanRepaymentRequest loanRepayReq = new LoanRepaymentRequest("cde", 1, 100D);
		MvcResult mvcResult1 = mockMvc.perform(
				MockMvcRequestBuilders
				.put("/api/v1/mini-aspire/loan/repay")
				.header(HttpHeaders.AUTHORIZATION, jwtTokenOfUser2)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loanRepayReq))
				.accept(MediaType.APPLICATION_JSON)
			).andReturn();
		assertEquals(HttpStatus.OK.value(), mvcResult1.getResponse().getStatus());
		LoanRepaymentResponse loanRepayResp = objectMapper.readValue(mvcResult1.getResponse().getContentAsString(), LoanRepaymentResponse.class);
		assertFalse(loanRepayResp.getIsRepaymentSuccessful());
		assertEquals(LoanConstants.REPAYMENT_AMOUNT_LESS_THAN_EMI_AMOUNT, loanRepayResp.getMessage());
		
		MvcResult mvcResult2 = mockMvc.perform(
				MockMvcRequestBuilders
				.get("/api/v1/mini-aspire/loan/cde")
				.header(HttpHeaders.AUTHORIZATION, jwtTokenOfUser2)
				.accept(MediaType.APPLICATION_JSON)
			).andReturn();
		assertEquals(HttpStatus.OK.value(), mvcResult2.getResponse().getStatus());
		UserLoans user2Loans = objectMapper.readValue(mvcResult2.getResponse().getContentAsString(), UserLoans.class);
		assertEquals("cde", user2Loans.getUsername());
		assertEquals(LoanConstants.LOANS_FETCH_SUCCESS, user2Loans.getMessage());
		assertNotNull(user2Loans.getLoans());
		assertEquals(1, user2Loans.getLoans().size());
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		List<Emi> loanDtoEmis = new ArrayList<>();
		StringBuilder dateString = new StringBuilder("");
		for (int i = 0; i < 5; i++) {
			date.setDate(date.getDate() + 7);
			dateString.append(df.format(date));
			if (i < 4) dateString.append("/");
			loanDtoEmis.add(new Emi(400D, df.format(date), false));
		}
		LoanDto loanDto = new LoanDto("cde", 2000D, 0D, 5, 5, true);
		LoanDto fetchedDto = user2Loans.getLoans().get(0);
		List<Emi> fetchedEmis = fetchedDto.getEmis();
		assertEquals(loanDto.getUsername(), fetchedDto.getUsername());
		assertEquals(loanDto.getDisbersedAmount(), fetchedDto.getDisbersedAmount());
		assertEquals(loanDto.getRepayedAmount(), fetchedDto.getRepayedAmount());
		assertEquals(loanDto.getTotalTerm(), fetchedDto.getTotalTerm());
		assertEquals(loanDto.getRemainingTerm(), fetchedDto.getRemainingTerm());
		assertEquals(loanDto.getIsActive(), fetchedDto.getIsActive());
		for (int i = 0; i < loanDtoEmis.size(); i++) {
			Emi loan2ActualEmi = loanDtoEmis.get(i);
			Emi loan2FetchedEmi = fetchedEmis.get(i);
			assertEquals(loan2ActualEmi.getEmiAmount(), loan2FetchedEmi.getEmiAmount());
			assertEquals(loan2ActualEmi.getEmiDate(), loan2FetchedEmi.getEmiDate());
			assertEquals(loan2ActualEmi.getIsPaid(), loan2FetchedEmi.getIsPaid());
		}
	}
	
	@Test
	@Order(6)
	void testRepayLoanByUser_AmountMoreThanRemainingAmount() throws JsonProcessingException, Exception {
		LoanRepaymentRequest loanRepayReq = new LoanRepaymentRequest("cde", 1, 10000D);
		MvcResult mvcResult1 = mockMvc.perform(
				MockMvcRequestBuilders
				.put("/api/v1/mini-aspire/loan/repay")
				.header(HttpHeaders.AUTHORIZATION, jwtTokenOfUser2)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loanRepayReq))
				.accept(MediaType.APPLICATION_JSON)
			).andReturn();
		assertEquals(HttpStatus.OK.value(), mvcResult1.getResponse().getStatus());
		LoanRepaymentResponse loanRepayResp = objectMapper.readValue(mvcResult1.getResponse().getContentAsString(), LoanRepaymentResponse.class);
		assertFalse(loanRepayResp.getIsRepaymentSuccessful());
		assertEquals(LoanConstants.REPAYMENT_AMOUNT_MORE_THAN_REMAINING_AMOUNT, loanRepayResp.getMessage());
		
		MvcResult mvcResult2 = mockMvc.perform(
				MockMvcRequestBuilders
				.get("/api/v1/mini-aspire/loan/cde")
				.header(HttpHeaders.AUTHORIZATION, jwtTokenOfUser2)
				.accept(MediaType.APPLICATION_JSON)
			).andReturn();
		assertEquals(HttpStatus.OK.value(), mvcResult2.getResponse().getStatus());
		UserLoans user2Loans = objectMapper.readValue(mvcResult2.getResponse().getContentAsString(), UserLoans.class);
		assertEquals("cde", user2Loans.getUsername());
		assertEquals(LoanConstants.LOANS_FETCH_SUCCESS, user2Loans.getMessage());
		assertNotNull(user2Loans.getLoans());
		assertEquals(1, user2Loans.getLoans().size());
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		List<Emi> loanDtoEmis = new ArrayList<>();
		StringBuilder dateString = new StringBuilder("");
		for (int i = 0; i < 5; i++) {
			date.setDate(date.getDate() + 7);
			dateString.append(df.format(date));
			if (i < 4) dateString.append("/");
			loanDtoEmis.add(new Emi(400D, df.format(date), false));
		}
		LoanDto loanDto = new LoanDto("cde", 2000D, 0D, 5, 5, true);
		LoanDto fetchedDto = user2Loans.getLoans().get(0);
		List<Emi> fetchedEmis = fetchedDto.getEmis();
		assertEquals(loanDto.getUsername(), fetchedDto.getUsername());
		assertEquals(loanDto.getDisbersedAmount(), fetchedDto.getDisbersedAmount());
		assertEquals(loanDto.getRepayedAmount(), fetchedDto.getRepayedAmount());
		assertEquals(loanDto.getTotalTerm(), fetchedDto.getTotalTerm());
		assertEquals(loanDto.getRemainingTerm(), fetchedDto.getRemainingTerm());
		assertEquals(loanDto.getIsActive(), fetchedDto.getIsActive());
		for (int i = 0; i < loanDtoEmis.size(); i++) {
			Emi loan2ActualEmi = loanDtoEmis.get(i);
			Emi loan2FetchedEmi = fetchedEmis.get(i);
			assertEquals(loan2ActualEmi.getEmiAmount(), loan2FetchedEmi.getEmiAmount());
			assertEquals(loan2ActualEmi.getEmiDate(), loan2FetchedEmi.getEmiDate());
			assertEquals(loan2ActualEmi.getIsPaid(), loan2FetchedEmi.getIsPaid());
		}
	}
	
	@Test
	@Order(7)
	void testRepayLoanByUserDifferentFromLoanuserAndNotAnAdmin() throws JsonProcessingException, Exception {
		User user3 = new User("efg", "1234567890111222", AuthConstants.CUSTOMER_ROLE);
		String jwtTokenOfUser3 = jwtTokenUtil.generateToken(user3);
		user3 = userRepository.save(user3);
		
		LoanRepaymentRequest loanRepayReq = new LoanRepaymentRequest("efg", 1, 10000D);
		MvcResult mvcResult1 = mockMvc.perform(
				MockMvcRequestBuilders
				.put("/api/v1/mini-aspire/loan/repay")
				.header(HttpHeaders.AUTHORIZATION, jwtTokenOfUser3)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loanRepayReq))
				.accept(MediaType.APPLICATION_JSON)
			).andReturn();
		assertEquals(HttpStatus.OK.value(), mvcResult1.getResponse().getStatus());
		LoanRepaymentResponse loanRepayResp = objectMapper.readValue(mvcResult1.getResponse().getContentAsString(), LoanRepaymentResponse.class);
		assertFalse(loanRepayResp.getIsRepaymentSuccessful());
		assertEquals(LoanConstants.USER_NOT_PERMITTED_TO_ACCESS_THIS_LOAN, loanRepayResp.getMessage());
		
		MvcResult mvcResult2 = mockMvc.perform(
				MockMvcRequestBuilders
				.get("/api/v1/mini-aspire/loan/cde")
				.header(HttpHeaders.AUTHORIZATION, jwtTokenOfUser2)
				.accept(MediaType.APPLICATION_JSON)
			).andReturn();
		assertEquals(HttpStatus.OK.value(), mvcResult2.getResponse().getStatus());
		UserLoans user2Loans = objectMapper.readValue(mvcResult2.getResponse().getContentAsString(), UserLoans.class);
		assertEquals("cde", user2Loans.getUsername());
		assertEquals(LoanConstants.LOANS_FETCH_SUCCESS, user2Loans.getMessage());
		assertNotNull(user2Loans.getLoans());
		assertEquals(1, user2Loans.getLoans().size());
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		List<Emi> loanDtoEmis = new ArrayList<>();
		StringBuilder dateString = new StringBuilder("");
		for (int i = 0; i < 5; i++) {
			date.setDate(date.getDate() + 7);
			dateString.append(df.format(date));
			if (i < 4) dateString.append("/");
			loanDtoEmis.add(new Emi(400D, df.format(date), false));
		}
		LoanDto loanDto = new LoanDto("cde", 2000D, 0D, 5, 5, true);
		LoanDto fetchedDto = user2Loans.getLoans().get(0);
		List<Emi> fetchedEmis = fetchedDto.getEmis();
		assertEquals(loanDto.getUsername(), fetchedDto.getUsername());
		assertEquals(loanDto.getDisbersedAmount(), fetchedDto.getDisbersedAmount());
		assertEquals(loanDto.getRepayedAmount(), fetchedDto.getRepayedAmount());
		assertEquals(loanDto.getTotalTerm(), fetchedDto.getTotalTerm());
		assertEquals(loanDto.getRemainingTerm(), fetchedDto.getRemainingTerm());
		assertEquals(loanDto.getIsActive(), fetchedDto.getIsActive());
		for (int i = 0; i < loanDtoEmis.size(); i++) {
			Emi loan2ActualEmi = loanDtoEmis.get(i);
			Emi loan2FetchedEmi = fetchedEmis.get(i);
			assertEquals(loan2ActualEmi.getEmiAmount(), loan2FetchedEmi.getEmiAmount());
			assertEquals(loan2ActualEmi.getEmiDate(), loan2FetchedEmi.getEmiDate());
			assertEquals(loan2ActualEmi.getIsPaid(), loan2FetchedEmi.getIsPaid());
		}
		
		userRepository.delete(user3);
	}
	
	@Test
	@Order(8)
	void testRepayLoanByUser() throws JsonProcessingException, Exception {
		LoanRepaymentRequest loanRepayReq = new LoanRepaymentRequest("cde", 1, 400D);
		MvcResult mvcResult1 = mockMvc.perform(
				MockMvcRequestBuilders
				.put("/api/v1/mini-aspire/loan/repay")
				.header(HttpHeaders.AUTHORIZATION, jwtTokenOfUser2)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loanRepayReq))
				.accept(MediaType.APPLICATION_JSON)
			).andReturn();
		assertEquals(HttpStatus.OK.value(), mvcResult1.getResponse().getStatus());
		LoanRepaymentResponse loanRepayResp = objectMapper.readValue(mvcResult1.getResponse().getContentAsString(), LoanRepaymentResponse.class);
		assertTrue(loanRepayResp.getIsRepaymentSuccessful());
		assertEquals(LoanConstants.LOAN_REPAYMENT_SUCCESS, loanRepayResp.getMessage());
		
		MvcResult mvcResult2 = mockMvc.perform(
				MockMvcRequestBuilders
				.get("/api/v1/mini-aspire/loan/cde")
				.header(HttpHeaders.AUTHORIZATION, jwtTokenOfUser2)
				.accept(MediaType.APPLICATION_JSON)
			).andReturn();
		assertEquals(HttpStatus.OK.value(), mvcResult2.getResponse().getStatus());
		UserLoans user2Loans = objectMapper.readValue(mvcResult2.getResponse().getContentAsString(), UserLoans.class);
		assertEquals("cde", user2Loans.getUsername());
		assertEquals(LoanConstants.LOANS_FETCH_SUCCESS, user2Loans.getMessage());
		assertNotNull(user2Loans.getLoans());
		assertEquals(1, user2Loans.getLoans().size());
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		List<Emi> loanDtoEmis = new ArrayList<>();
		StringBuilder dateString = new StringBuilder("");
		for (int i = 0; i < 5; i++) {
			date.setDate(date.getDate() + 7);
			dateString.append(df.format(date));
			if (i < 4) dateString.append("/");
			if (i == 0) loanDtoEmis.add(new Emi(400D, df.format(date), true));
			else loanDtoEmis.add(new Emi(400D, df.format(date), false));
		}
		LoanDto loanDto = new LoanDto("cde", 2000D, 400D, 5, 4, true);
		LoanDto fetchedDto = user2Loans.getLoans().get(0);
		List<Emi> fetchedEmis = fetchedDto.getEmis();
		assertEquals(loanDto.getUsername(), fetchedDto.getUsername());
		assertEquals(loanDto.getDisbersedAmount(), fetchedDto.getDisbersedAmount());
		assertEquals(loanDto.getRepayedAmount(), fetchedDto.getRepayedAmount());
		assertEquals(loanDto.getTotalTerm(), fetchedDto.getTotalTerm());
		assertEquals(loanDto.getRemainingTerm(), fetchedDto.getRemainingTerm());
		assertEquals(loanDto.getIsActive(), fetchedDto.getIsActive());
		for (int i = 0; i < loanDtoEmis.size(); i++) {
			Emi loan2ActualEmi = loanDtoEmis.get(i);
			Emi loan2FetchedEmi = fetchedEmis.get(i);
			assertEquals(loan2ActualEmi.getEmiAmount(), loan2FetchedEmi.getEmiAmount());
			assertEquals(loan2ActualEmi.getEmiDate(), loan2FetchedEmi.getEmiDate());
			assertEquals(loan2ActualEmi.getIsPaid(), loan2FetchedEmi.getIsPaid());
		}
	}
	
	@Test
	@Order(9)
	void testRepayLoanByUserThroughAnAdmin() throws JsonProcessingException, Exception {
		LoanRepaymentRequest loanRepayReq = new LoanRepaymentRequest("abc", 1, 400D);
		MvcResult mvcResult1 = mockMvc.perform(
				MockMvcRequestBuilders
				.put("/api/v1/mini-aspire/loan/repay")
				.header(HttpHeaders.AUTHORIZATION, jwtTokenOfUser1)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loanRepayReq))
				.accept(MediaType.APPLICATION_JSON)
			).andReturn();
		assertEquals(HttpStatus.OK.value(), mvcResult1.getResponse().getStatus());
		LoanRepaymentResponse loanRepayResp = objectMapper.readValue(mvcResult1.getResponse().getContentAsString(), LoanRepaymentResponse.class);
		assertTrue(loanRepayResp.getIsRepaymentSuccessful());
		assertEquals(LoanConstants.LOAN_REPAYMENT_SUCCESS, loanRepayResp.getMessage());
		
		MvcResult mvcResult2 = mockMvc.perform(
				MockMvcRequestBuilders
				.get("/api/v1/mini-aspire/loan/cde")
				.header(HttpHeaders.AUTHORIZATION, jwtTokenOfUser2)
				.accept(MediaType.APPLICATION_JSON)
			).andReturn();
		assertEquals(HttpStatus.OK.value(), mvcResult2.getResponse().getStatus());
		UserLoans user2Loans = objectMapper.readValue(mvcResult2.getResponse().getContentAsString(), UserLoans.class);
		assertEquals("cde", user2Loans.getUsername());
		assertEquals(LoanConstants.LOANS_FETCH_SUCCESS, user2Loans.getMessage());
		assertNotNull(user2Loans.getLoans());
		assertEquals(1, user2Loans.getLoans().size());
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		List<Emi> loanDtoEmis = new ArrayList<>();
		StringBuilder dateString = new StringBuilder("");
		for (int i = 0; i < 5; i++) {
			date.setDate(date.getDate() + 7);
			dateString.append(df.format(date));
			if (i < 4) dateString.append("/");
			if (i < 2) loanDtoEmis.add(new Emi(400D, df.format(date), true));
			else loanDtoEmis.add(new Emi(400D, df.format(date), false));
		}
		LoanDto loanDto = new LoanDto("cde", 2000D, 800D, 5, 3, true);
		LoanDto fetchedDto = user2Loans.getLoans().get(0);
		List<Emi> fetchedEmis = fetchedDto.getEmis();
		assertEquals(loanDto.getUsername(), fetchedDto.getUsername());
		assertEquals(loanDto.getDisbersedAmount(), fetchedDto.getDisbersedAmount());
		assertEquals(loanDto.getRepayedAmount(), fetchedDto.getRepayedAmount());
		assertEquals(loanDto.getTotalTerm(), fetchedDto.getTotalTerm());
		assertEquals(loanDto.getRemainingTerm(), fetchedDto.getRemainingTerm());
		assertEquals(loanDto.getIsActive(), fetchedDto.getIsActive());
		for (int i = 0; i < loanDtoEmis.size(); i++) {
			Emi loan2ActualEmi = loanDtoEmis.get(i);
			Emi loan2FetchedEmi = fetchedEmis.get(i);
			assertEquals(loan2ActualEmi.getEmiAmount(), loan2FetchedEmi.getEmiAmount());
			assertEquals(loan2ActualEmi.getEmiDate(), loan2FetchedEmi.getEmiDate());
			assertEquals(loan2ActualEmi.getIsPaid(), loan2FetchedEmi.getIsPaid());
		}
	}
	
}

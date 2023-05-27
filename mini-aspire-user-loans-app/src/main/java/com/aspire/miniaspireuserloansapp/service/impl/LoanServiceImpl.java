package com.aspire.miniaspireuserloansapp.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aspire.miniaspireuserloansapp.constants.AuthConstants;
import com.aspire.miniaspireuserloansapp.constants.LoanConstants;
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
import com.aspire.miniaspireuserloansapp.service.LoanService;

@Service
public class LoanServiceImpl implements LoanService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private LoanRepository loanRepository;
	
	@Override
	public UserLoans getLoansOfUser(String username) {
		// TODO Auto-generated method stub
		User user = userRepository.findUser(username).get();
		List<Loan> loans = loanRepository.findAllByUserAndIsActive(user, true);
		List<LoanDto> loanDtos = convertLoansToLoanDtos(loans);
		UserLoans userLoans = new UserLoans(username, loanDtos, LoanConstants.LOANS_FETCH_SUCCESS);
		return userLoans;
	}

	private List<LoanDto> convertLoansToLoanDtos(List<Loan> loans) {
		// TODO Auto-generated method stub
		List<LoanDto> loanDtos = new ArrayList<>();
		if (loans != null) {
			for (Loan loan : loans) {
				LoanDto loanDto = new LoanDto(loan.getUser().getUsername(), loan.getDisbersedAmount(), 
						loan.getRepayedAmount(), loan.getTotalTerm(), loan.getRemainingTerm(), loan.getIsActive());
				loanDto.setEmis(loan.getEmiDates(), loan.getRepayments());
				loanDtos.add(loanDto);
			}
		}
		return loanDtos;
	}

	@Override
	public LoanApplicationResponse applyForNewLoan(LoanApplicationRequest loanApplicationRequest) {
		// TODO Auto-generated method stub
		String username = loanApplicationRequest.getUsername();
		User user = userRepository.findUser(username).get(); 
		Double loanAmount = loanApplicationRequest.getLoanAmount();
		Integer loanTerm = loanApplicationRequest.getTerm();
		StringBuilder emiDateString = new StringBuilder("");
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		Date emiDate = new Date();
		for (int i = 1; i <= loanTerm; i++) {
			emiDate.setDate(emiDate.getDate() + 7);
			emiDateString.append(df.format(emiDate));
			if (i < loanTerm) emiDateString.append("/");
		}
		Loan loan = new Loan(user, false, loanAmount, 0D, loanTerm, loanTerm, emiDateString.toString(), "");
		loan = loanRepository.save(loan);
		LoanDto loanDto = new LoanDto(username, loan.getDisbersedAmount(), loan.getRepayedAmount(), 
				loan.getTotalTerm(), loan.getRemainingTerm(), loan.getIsActive());
		loanDto.setEmis(loan.getEmiDates(), loan.getRepayments());
		LoanApplicationResponse loanApplicationResp = new LoanApplicationResponse(true, loan.getId(), loanDto, 
				LoanConstants.LOAN_APPLICATION_SUCCESS);
		return loanApplicationResp;
	}

	@Override
	public LoanApprovalResponse approveLoans(List<Integer> loanIds) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder("");
		LoanApprovalResponse approvalResponse = new LoanApprovalResponse();
		for (Integer loanId : loanIds) {
			try {
				Optional<Loan> loanOptional = loanRepository.findById(loanId);
				if (loanOptional.isPresent()) {
					Loan loan = loanOptional.get();
					if (loan.getIsActive()) sb.append(loanId + ": " + LoanConstants.LOAN_ALREADY_APPROVED + ",");
					else {
						loan.setIsActive(true);
						loanRepository.save(loan);
						sb.append(loanId + ": " + LoanConstants.LOAN_APPROVAL_SUCCESS + ",");
					}
				} else sb.append(loanId + ": " + LoanConstants.INVALID_LOAN_ID + ",");
			} catch (Exception e) {
				sb.append(loanId + ": " + LoanConstants.LOAN_APPROVAL_FAILED + ",");
			}
		}
		approvalResponse.setApprovalMessages(sb.toString());
		return approvalResponse;
	}

	@Override
	public LoanRepaymentResponse repayLoan(LoanRepaymentRequest loanRepaymentRequest) {
		// TODO Auto-generated method stub
		LoanRepaymentResponse repaymentResponse = null;
		Integer loanId = loanRepaymentRequest.getLoanId();
		Optional<Loan> loanOptional = loanRepository.findById(loanId);
		if (loanOptional.isPresent()) {
			Loan loan = loanOptional.get();
			User userfromLoan = loan.getUser();
			User userFromRequest = userRepository.findUser(loanRepaymentRequest.getUsername()).get();
			String roleOfUserFromRequest = userFromRequest.getRole();
			if (userFromRequest.getUsername().equals(userfromLoan.getUsername()) 
					|| roleOfUserFromRequest.equals(AuthConstants.ADMIN_ROLE)) {
				Double remainingAmount = loan.getDisbersedAmount() - loan.getRepayedAmount();
				Double emiAmount = remainingAmount / loan.getRemainingTerm();
				Double repaymentAmount = loanRepaymentRequest.getRepaymentAmount();
				if (repaymentAmount < emiAmount) repaymentResponse = new LoanRepaymentResponse(false, null, 
						LoanConstants.REPAYMENT_AMOUNT_LESS_THAN_EMI_AMOUNT);
				else if (repaymentAmount > remainingAmount) repaymentResponse = new LoanRepaymentResponse(false, null, 
						LoanConstants.REPAYMENT_AMOUNT_MORE_THAN_REMAINING_AMOUNT);
				else {
					loan.setRepayedAmount(loan.getRepayedAmount() + repaymentAmount);
					loan.setRemainingTerm(loan.getRemainingTerm() - 1);
					loan.setRepayments(loan.getRepayments() + repaymentAmount);
					if (loan.getRemainingTerm().intValue() > 0) loan.setRepayments(loan.getRepayments() + "/");
					loan = loanRepository.save(loan);
					LoanDto loanDto = new LoanDto(userFromRequest.getUsername(), loan.getDisbersedAmount(), 
					loan.getRepayedAmount(), loan.getTotalTerm(), loan.getRemainingTerm(), loan.getIsActive());
					loanDto.setEmis(loan.getEmiDates(), loan.getRepayments());
					repaymentResponse = new LoanRepaymentResponse(true, loanDto, LoanConstants.LOAN_REPAYMENT_SUCCESS);
				}
			} else repaymentResponse = new LoanRepaymentResponse(false, null, 
					LoanConstants.USER_NOT_PERMITTED_TO_ACCESS_THIS_LOAN);
		} else repaymentResponse = new LoanRepaymentResponse(false, null, LoanConstants.INVALID_LOAN_ID);
		return repaymentResponse;
	}

}

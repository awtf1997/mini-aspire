package com.aspire.miniaspireuserloansapp.service;

import java.util.List;

import com.aspire.miniaspireuserloansapp.model.dto.loans.LoanApplicationRequest;
import com.aspire.miniaspireuserloansapp.model.dto.loans.LoanApplicationResponse;
import com.aspire.miniaspireuserloansapp.model.dto.loans.LoanApprovalResponse;
import com.aspire.miniaspireuserloansapp.model.dto.loans.LoanRepaymentRequest;
import com.aspire.miniaspireuserloansapp.model.dto.loans.LoanRepaymentResponse;
import com.aspire.miniaspireuserloansapp.model.dto.loans.UserLoans;

public interface LoanService {

	UserLoans getLoansOfUser(String username);

	LoanApplicationResponse applyForNewLoan(LoanApplicationRequest loanApplicationRequest);

	LoanApprovalResponse approveLoans(List<Integer> loanIds);

	LoanRepaymentResponse repayLoan(LoanRepaymentRequest loanRepaymentRequest);

}

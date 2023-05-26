package com.aspire.miniaspireuserloansapp.model.dto.loans;

import java.util.List;

public class LoanApprovalRequest {
	
	private String username;
	
	private List<Integer> loanIds;

	public LoanApprovalRequest() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LoanApprovalRequest(String username, List<Integer> loanIds) {
		super();
		this.username = username;
		this.loanIds = loanIds;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<Integer> getLoanIds() {
		return loanIds;
	}

	public void setLoanIds(List<Integer> loanIds) {
		this.loanIds = loanIds;
	}
	
}

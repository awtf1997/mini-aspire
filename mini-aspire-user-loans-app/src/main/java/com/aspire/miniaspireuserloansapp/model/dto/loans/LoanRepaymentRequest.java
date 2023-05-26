package com.aspire.miniaspireuserloansapp.model.dto.loans;

public class LoanRepaymentRequest {
	
	private String username;
	
	private Integer loanId;
	
	private Double repaymentAmount;

	public LoanRepaymentRequest() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LoanRepaymentRequest(String username, Integer loanId, Double repaymentAmount) {
		super();
		this.username = username;
		this.loanId = loanId;
		this.repaymentAmount = repaymentAmount;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getLoanId() {
		return loanId;
	}

	public void setLoanId(Integer loanId) {
		this.loanId = loanId;
	}

	public Double getRepaymentAmount() {
		return repaymentAmount;
	}

	public void setRepaymentAmount(Double repaymentAmount) {
		this.repaymentAmount = repaymentAmount;
	}
	
}

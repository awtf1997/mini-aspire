package com.aspire.miniaspireuserloansapp.model.dto.loans;

public class LoanApplicationRequest {
	
	private String username;
	
	private Double loanAmount;
	
	private Integer term;

	public LoanApplicationRequest() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LoanApplicationRequest(String username, Double loanAmount, Integer term) {
		super();
		this.username = username;
		this.loanAmount = loanAmount;
		this.term = term;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Double getLoanAmount() {
		return loanAmount;
	}

	public void setLoanAmount(Double loanAmount) {
		this.loanAmount = loanAmount;
	}

	public Integer getTerm() {
		return term;
	}

	public void setTerm(Integer term) {
		this.term = term;
	}
	
}

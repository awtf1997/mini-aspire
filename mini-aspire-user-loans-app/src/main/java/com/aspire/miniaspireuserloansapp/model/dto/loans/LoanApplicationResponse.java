package com.aspire.miniaspireuserloansapp.model.dto.loans;

public class LoanApplicationResponse {
	
	private Boolean isApplied;
	
	private Integer loanId;
	
	private LoanDto loanDto;
	
	private String message;

	public LoanApplicationResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LoanApplicationResponse(Boolean isApplied, Integer loanId, LoanDto loanDto, String message) {
		super();
		this.isApplied = isApplied;
		this.loanId = loanId;
		this.loanDto = loanDto;
		this.message = message;
	}

	public Boolean getIsApplied() {
		return isApplied;
	}

	public void setIsApplied(Boolean isApplied) {
		this.isApplied = isApplied;
	}

	public Integer getLoanId() {
		return loanId;
	}

	public void setLoanId(Integer loanId) {
		this.loanId = loanId;
	}

	public LoanDto getLoanDto() {
		return loanDto;
	}

	public void setLoanDto(LoanDto loanDto) {
		this.loanDto = loanDto;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}

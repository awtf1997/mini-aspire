package com.aspire.miniaspireuserloansapp.model.dto.loans;

public class LoanRepaymentResponse {
	
	private Boolean isRepaymentSuccessful;
	
	private LoanDto loanDto;
	
	private String message;

	public LoanRepaymentResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LoanRepaymentResponse(Boolean isRepaymentSuccessful, LoanDto loanDto, String message) {
		super();
		this.isRepaymentSuccessful = isRepaymentSuccessful;
		this.loanDto = loanDto;
		this.message = message;
	}

	public Boolean getIsRepaymentSuccessful() {
		return isRepaymentSuccessful;
	}

	public void setIsRepaymentSuccessful(Boolean isRepaymentSuccessful) {
		this.isRepaymentSuccessful = isRepaymentSuccessful;
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

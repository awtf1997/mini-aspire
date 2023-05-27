package com.aspire.miniaspireuserloansapp.model.dto.loans;

import java.util.List;

public class UserLoans {
	
	private String username;
	
	private List<LoanDto> loans;
	
	private String message;

	public UserLoans() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserLoans(String username, List<LoanDto> loans, String message) {
		super();
		this.username = username;
		this.loans = loans;
		this.message = message;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<LoanDto> getLoans() {
		return loans;
	}

	public void setLoans(List<LoanDto> loans) {
		this.loans = loans;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}

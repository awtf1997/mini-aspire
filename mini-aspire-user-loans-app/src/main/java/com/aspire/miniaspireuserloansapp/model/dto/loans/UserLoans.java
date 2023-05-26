package com.aspire.miniaspireuserloansapp.model.dto.loans;

import java.util.List;

public class UserLoans {
	
	private String usename;
	
	private List<LoanDto> loans;
	
	private String message;

	public UserLoans() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserLoans(String usename, List<LoanDto> loans, String message) {
		super();
		this.usename = usename;
		this.loans = loans;
		this.message = message;
	}

	public String getUsename() {
		return usename;
	}

	public void setUsename(String usename) {
		this.usename = usename;
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

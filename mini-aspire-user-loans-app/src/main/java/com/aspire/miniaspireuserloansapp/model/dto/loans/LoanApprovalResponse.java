package com.aspire.miniaspireuserloansapp.model.dto.loans;

public class LoanApprovalResponse {
	
	private String approvalMessages;

	public LoanApprovalResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LoanApprovalResponse(String approvalMessages) {
		super();
		this.approvalMessages = approvalMessages;
	}

	public String getApprovalMessages() {
		return approvalMessages;
	}

	public void setApprovalMessages(String approvalMessages) {
		this.approvalMessages = approvalMessages;
	}
	
}

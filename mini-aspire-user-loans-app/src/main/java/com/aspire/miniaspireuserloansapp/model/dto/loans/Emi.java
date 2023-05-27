package com.aspire.miniaspireuserloansapp.model.dto.loans;

public class Emi {

	private Double emiAmount;

	private String emiDate;
	
	private Boolean isPaid;

	public Emi() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Emi(Double emiAmount, String emiDate, Boolean isPaid) {
		super();
		this.emiAmount = emiAmount;
		this.emiDate = emiDate;
		this.isPaid = isPaid;
	}

	public Double getEmiAmount() {
		return emiAmount;
	}

	public void setEmiAmount(Double emiAmount) {
		this.emiAmount = emiAmount;
	}

	public String getEmiDate() {
		return emiDate;
	}

	public void setEmiDate(String emiDate) {
		this.emiDate = emiDate;
	}

	public Boolean getIsPaid() {
		return isPaid;
	}

	public void setIsPaid(Boolean isPaid) {
		this.isPaid = isPaid;
	}

}

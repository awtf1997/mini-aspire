package com.aspire.miniaspireuserloansapp.model.dto.loans;

import java.util.ArrayList;
import java.util.List;

public class LoanDto {

	private String username;

	private Double disbersedAmount;

	private Double repayedAmount;

	private List<Emi> emis;

	private Integer totalTerm;

	private Integer remainingTerm;

	private Boolean isActive;

	public LoanDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LoanDto(String username, Double disbersedAmount, Double repayedAmount, Integer totalTerm,
			Integer remainingTerm, Boolean isActive) {
		super();
		this.username = username;
		this.disbersedAmount = disbersedAmount;
		this.repayedAmount = repayedAmount;
		this.totalTerm = totalTerm;
		this.remainingTerm = remainingTerm;
		this.isActive = isActive;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Double getDisbersedAmount() {
		return disbersedAmount;
	}

	public void setDisbersedAmount(Double disbersedAmount) {
		this.disbersedAmount = disbersedAmount;
	}

	public Double getRepayedAmount() {
		return repayedAmount;
	}

	public void setRepayedAmount(Double repayedAmount) {
		this.repayedAmount = repayedAmount;
	}

	public List<Emi> getEmis() {
		return emis;
	}

	public void setEmis(String emiDateString, String repaymentsString) {
		this.emis = createEmis(emiDateString, repaymentsString);
	}

	private List<Emi> createEmis(String emiDateString, String repaymentsString) {
		List<Emi> emis = new ArrayList<>();
		String[] emiDates = emiDateString.split("/");
		Integer completedTerm = this.totalTerm - this.remainingTerm;
		String[] repayments = repaymentsString.split("/");
		for (int i = 1; i <= completedTerm; i++) {
			Emi emi = new Emi(Double.parseDouble(repayments[i - 1]), emiDates[i - 1], true);
			emis.add(emi);
		}
		Double remainingAmount = this.disbersedAmount - this.repayedAmount;
		Double emiAmount = remainingAmount / this.remainingTerm;
		for (int i = 1; i <= this.remainingTerm; i++) {
			Emi emi = null;
			if (i != this.remainingTerm) emi = new Emi(emiAmount, emiDates[completedTerm + i - 1], false);
			else emi = new Emi(remainingAmount - (emiAmount * (this.remainingTerm - 1)),
					emiDates[completedTerm + i - 1], false);
			emis.add(emi);
		}
		return emis;
	}

	public Integer getTotalTerm() {
		return totalTerm;
	}

	public void setTotalTerm(Integer totalTerm) {
		this.totalTerm = totalTerm;
	}

	public Integer getRemainingTerm() {
		return remainingTerm;
	}

	public void setRemainingTerm(Integer remainingTerm) {
		this.remainingTerm = remainingTerm;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

}

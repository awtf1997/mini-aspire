package com.aspire.miniaspireuserloansapp.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "LOANS")
public class Loan {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID")
	private User user;

	@Column(name = "IS_ACTIVE")
	private Boolean isActive;

	@Column(name = "DISBERSED_AMOUNT")
	private Double disbersedAmount;

	@Column(name = "REPAYED_AMOUNT")
	private Double repayedAmount;

	@Column(name = "TERM_TOTAL")
	private Integer totalTerm;

	@Column(name = "TERM_REMAINING")
	private Integer remainingTerm;

	@Column(name = "EMI_DATES")
	private String emiDates;

	@Column(name = "REPAYMENTS")
	private String repayments;

	public Loan() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Loan(User user, Boolean isActive, Double disbersedAmount, Double repayedAmount, Integer totalTerm,
			Integer remainingTerm, String emiDates, String repayments) {
		super();
		this.user = user;
		this.isActive = isActive;
		this.disbersedAmount = disbersedAmount;
		this.repayedAmount = repayedAmount;
		this.totalTerm = totalTerm;
		this.remainingTerm = remainingTerm;
		this.emiDates = emiDates;
		this.repayments = repayments;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
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

	public String getEmiDates() {
		return emiDates;
	}

	public void setEmiDates(String emiDates) {
		this.emiDates = emiDates;
	}

	public String getRepayments() {
		return repayments;
	}

	public void setRepayments(String repayments) {
		this.repayments = repayments;
	}

}

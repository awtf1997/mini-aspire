package com.aspire.miniaspireuserloansapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aspire.miniaspireuserloansapp.model.entity.Loan;
import com.aspire.miniaspireuserloansapp.model.entity.User;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Integer> {

	List<Loan> findAllByUserAndIsActive(User user, boolean b);

}

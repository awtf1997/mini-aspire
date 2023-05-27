package com.aspire.miniaspireuserloansapp.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aspire.miniaspireuserloansapp.constants.AuthConstants;
import com.aspire.miniaspireuserloansapp.constants.LoanConstants;
import com.aspire.miniaspireuserloansapp.model.dto.loans.LoanDto;
import com.aspire.miniaspireuserloansapp.model.dto.loans.UserLoans;
import com.aspire.miniaspireuserloansapp.model.entity.Loan;
import com.aspire.miniaspireuserloansapp.model.entity.User;
import com.aspire.miniaspireuserloansapp.repository.LoanRepository;
import com.aspire.miniaspireuserloansapp.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class LoanServiceImplTest {
	
	@Mock
	private UserRepository userRepository;

	@Mock
	private LoanRepository loanRepository;
	
	@InjectMocks
	private LoanServiceImpl loanService;
	
	private User user;
	private Loan loan1;
	private Loan loan1AfterARepayment;
	private Loan loan2;
	private LoanDto loan1Dto;
	private LoanDto loan1DtoAfterARepayment;
	private LoanDto loan2DtoBeforeApproval;
	private LoanDto loan2DtoAfterApproval;
	
	@BeforeEach
	void setUp() throws Exception {
		user = new User("abc", "1234567890", AuthConstants.ADMIN_ROLE);
		
		loan1 = new Loan(user, true, 1000D, 500D, 3, 2, "02-06-2023/09-06-2023/16-06-2023", "500/");
		loan1Dto = new LoanDto("abc", 1000D, 500D, 3, 2, true);
		loan1Dto.setEmis("02-06-2023/09-06-2023/16-06-2023", "500/");
		
		loan1AfterARepayment = new Loan(user, true, 1000D, 250D, 3, 1, "02-06-2023/09-06-2023/16-06-2023", "500/250/");
		loan1DtoAfterARepayment = new LoanDto("abc", 1000D, 250D, 3, 1, true);
		loan1DtoAfterARepayment.setEmis("02-06-2023/09-06-2023/16-06-2023", "500/250/");
		
		loan2 = new Loan(user, false, 1000D, 0D, 3, 3, "12-06-2023/19-06-2023/26-06-2023", "");
		loan2DtoBeforeApproval= new LoanDto("abc", 1000D, 0D, 3, 3, false);
		loan2DtoBeforeApproval.setEmis("12-06-2023/19-06-2023/26-06-2023", "");
		loan2DtoAfterApproval = new LoanDto("abc", 1000D, 0D, 3, 3, true);
		loan2DtoAfterApproval.setEmis("12-06-2023/19-06-2023/26-06-2023", "");
	}

	@Test
	void testGetLoansOfUser() {
		Optional<User> userOptional = Optional.of(user);
		when(userRepository.findUser("abc")).thenReturn(userOptional);
		when(loanRepository.findAllByUserAndIsActive(user, true)).thenReturn(List.of(loan1));
		UserLoans userLoans = loanService.getLoansOfUser("abc");
		assertEquals("abc", userLoans.getUsename());
		assertEquals(LoanConstants.LOANS_FETCH_SUCCESS, userLoans.getMessage());
		List<LoanDto> loanDtos = userLoans.getLoans();
		assertEquals(1, loanDtos.size());
		assertEquals(3, loanDtos.get(0).getEmis().size());
		for (int i = 0; i < 3; i++) {
			assertEquals(loan1Dto.getEmis().get(i).getEmiAmount(), loanDtos.get(0).getEmis().get(i).getEmiAmount());
		}
	}
	
	@Test
	void testApplyForNewLoan() {
		fail("Not yet implemented");
	}

	@Test
	void testApproveLoans() {
		fail("Not yet implemented");
	}

	@Test
	void testRepayLoan() {
		fail("Not yet implemented");
	}

}

package com.aspire.miniaspireuserloansapp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;

import com.aspire.miniaspireuserloansapp.controller.AuthController;
import com.aspire.miniaspireuserloansapp.controller.LoanController;

@SpringBootTest
@WebMvcTest
class MiniAspireUserLoansAppApplicationTests {

	@Autowired
	private AuthController authController;
	
	@Autowired
	private LoanController loanController;
	
	
	@Test
	void contextLoads() {
	}

}

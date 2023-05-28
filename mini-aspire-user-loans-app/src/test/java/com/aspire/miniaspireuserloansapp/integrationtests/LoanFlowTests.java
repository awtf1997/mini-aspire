package com.aspire.miniaspireuserloansapp.integrationtests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
public class LoanFlowTests {
	
	@Autowired
	private MockMvc mockMvc;
	
}

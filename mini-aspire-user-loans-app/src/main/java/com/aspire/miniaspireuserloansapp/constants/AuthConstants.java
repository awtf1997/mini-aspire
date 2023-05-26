package com.aspire.miniaspireuserloansapp.constants;

import java.util.List;

public class AuthConstants {
	
	public static final String AUTHENTICATION_SUCCESS = "AUTHENTICATION SUCCESS";
	
	public static final String AUTHENTICATION_FAILED = "AUTHENTICATION FAILED";
	
	public static final String INCORRECT_CREDENTIALS = "INCORRECT CREDENTIALS";
	
	public static final String REGISTRATION_SUCCESS = "REGISTRATION SUCCESS";
	
	public static final String REGISTRATION_FAILED = "REGISTRATION FAILED";
	
	public static final String SERVER_FAILURE = "SERVER FAILURE";
	
	public static final String USERNAME_ALREADY_EXISTS = "USERNAME ALREADY EXISTS";
	
	public static final Integer PASSWORD_MINIMUM_LENGTH = 8;
	
	public static final Integer PASSWORD_MAXIMUM_LENGTH = 20;
	
	public static final String INVALID_PASSWORD = "INVALID PASSWORD";
	
	public static final String ADMIN_ROLE = "ADMIN";
	
	public static final String CUSTOMER_ROLE = "CUSTOMER";
	
	public static final List<String> ROLES = List.of(ADMIN_ROLE, CUSTOMER_ROLE);
	
	public static final String INVALID_ROLE = "INVALID ROLE";
	
	
}

package com.aspire.miniaspireuserloansapp.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aspire.miniaspireuserloansapp.constants.AuthConstants;
import com.aspire.miniaspireuserloansapp.model.dto.authentication.AuthenticationRequest;
import com.aspire.miniaspireuserloansapp.model.dto.authentication.AuthenticationResponse;
import com.aspire.miniaspireuserloansapp.model.dto.authentication.RegistrationRequest;
import com.aspire.miniaspireuserloansapp.model.dto.authentication.RegistrationResponse;
import com.aspire.miniaspireuserloansapp.model.entity.User;
import com.aspire.miniaspireuserloansapp.repository.UserRepository;
import com.aspire.miniaspireuserloansapp.service.AuthService;
import com.aspire.miniaspireuserloansapp.utils.JwtTokenUtil;

@Service
public class AuthServiceImpl implements AuthService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Override
	public AuthenticationResponse authenticateUser(AuthenticationRequest authReq) {
		// TODO Auto-generated method stub
		AuthenticationResponse authResp = new AuthenticationResponse();
		try {
			Optional<User> userOptional = userRepository.findUser(authReq.getUsername(), authReq.getPassword());
			if (!userOptional.isEmpty()) {
				authResp.setAuthenticated(true);
				String jwtToken = jwtTokenUtil.generateToken(userOptional.get());
				authResp.setMessage(jwtToken);
			} else {
				authResp.setAuthenticated(false);
				authResp.setMessage(AuthConstants.INCORRECT_CREDENTIALS);
			}
		} catch (Exception e) {
			e.printStackTrace();
			authResp.setAuthenticated(false);
			authResp.setMessage(AuthConstants.SERVER_FAILURE);
		}
		return authResp;
	}

	@Override
	public RegistrationResponse registerUser(RegistrationRequest regnReq) {
		// TODO Auto-generated method stub
		RegistrationResponse regnResp = new RegistrationResponse();
		try {
			Optional<User> userOptional = userRepository.findUser(regnReq.getUsername());
			if (!userOptional.isEmpty()) {
				regnResp.setRegistered(false);
				regnResp.setMessage(AuthConstants.USERNAME_ALREADY_EXISTS);
			} else {
				if (regnReq.getPassword().length() < AuthConstants.PASSWORD_MINIMUM_LENGTH) {
					regnResp.setRegistered(false);
					regnResp.setMessage(AuthConstants.INVALID_PASSWORD + ": MINIMUM PASSWORD LENGTH SHOULD BE "
							+ AuthConstants.PASSWORD_MINIMUM_LENGTH);
				} else if (regnReq.getPassword().length() > AuthConstants.PASSWORD_MAXIMUM_LENGTH) {
					regnResp.setRegistered(false);
					regnResp.setMessage(AuthConstants.INVALID_PASSWORD + ": MAXIMUM PASSWORD LENGTH SHOULD BE "
							+ AuthConstants.PASSWORD_MAXIMUM_LENGTH);
				} else if (!AuthConstants.ROLES.contains(regnReq.getRole())) {
					regnResp.setRegistered(false);
					regnResp.setMessage(AuthConstants.INVALID_ROLE);
				} else {
					User user = new User(regnReq.getUsername(), regnReq.getPassword(), regnReq.getRole());
					userRepository.save(user);
					regnResp.setRegistered(true);
					String jwtToken = jwtTokenUtil.generateToken(user);
					regnResp.setMessage(jwtToken);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			regnResp.setRegistered(false);
			regnResp.setMessage(AuthConstants.SERVER_FAILURE);
		}
		return regnResp;
	}

	@Override
	public Boolean checkIfUserExists(String username) {
		return checkIfUserExists(username, null);
	}

	@Override
	public Boolean checkIfUserExists(String username, String password) {
		try {
			Optional<User> userOptional = null;
			if (password == null)
				userOptional = userRepository.findUser(username);
			else
				userOptional = userRepository.findUser(username, password);
			return userOptional.isPresent();
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public String getUserRole(String username) {
		// TODO Auto-generated method stub
		User user = userRepository.findUser(username).get();
		return user.getRole();
	}

}

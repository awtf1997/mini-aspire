package com.aspire.miniaspireuserloansapp.utils;

import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.aspire.miniaspireuserloansapp.constants.AuthConstants;
import com.aspire.miniaspireuserloansapp.model.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil {

	@Value(value = "${jwt.secret}")
	private String jwtSecret;
	
	@Value(value = "${jwt.validity}")
	private Integer jwtValidity;
	
	private Encoder encoder = Base64.getEncoder();
	
	public String generateToken(User user) {
		String encodedJwtSecret = encoder.encodeToString(jwtSecret.getBytes());
		Map<String, Object> claims = new HashMap<>();
		return Jwts.builder().setClaims(claims)
				.setSubject(user.getUsername())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + jwtValidity * 1000))
				.signWith(SignatureAlgorithm.HS512, encodedJwtSecret).compact();
	}
	
	public Boolean validateToken(String token, String username, String role) {
		String encodedJwtSecret = encoder.encodeToString(jwtSecret.getBytes());
		Claims claims = Jwts.parser().setSigningKey(encodedJwtSecret).parseClaimsJws(token).getBody();
		return ((claims.getSubject().equals(username) || role.equals(AuthConstants.ADMIN_ROLE)) 
				&& claims.getExpiration().after(new Date()));
	}
	
}

package com.rkc.zds.service;

import com.rkc.zds.config.security.SecurityProperties;
import com.rkc.zds.config.security.SecurityUser;
//import com.rkc.zds.config.security.hmac.*;
import com.rkc.zds.dto.LoginDto;
import com.rkc.zds.dto.UserDto;

//import com.rkc.zds.config.security.SecurityUtils;
//import com.rkc.zds.config.security.hmac.HmacUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.*;

@Service
public class AuthenticationService {

	public static final String CSRF_CLAIM_HEADER = "X-HMAC-CSRF";
    public static final String ACCESS_TOKEN_COOKIE = "access_token";
	public static final String JWT_CLAIM_LOGIN = "login";

	@Autowired
	private AuthenticationManager authenticationManager;

    private SecurityProperties securityProperties = SecurityProperties.getInstance();

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private UserService userService;

	/**
	 * Authenticate a user in Spring Security The following headers are set in
	 * the response: - X-TokenAccess: JWT - X-Secret: Generated secret in base64
	 * using SHA-256 algorithm - WWW-Authenticate: Used algorithm to encode
	 * secret The authenticated user is set in the Spring Security context The
	 * generated secret is stored in a static list for every user
	 * 
	 * @param loginDTO
	 *            credentials
	 * @param response
	 *            http response
	 * @return UserDTO instance
	 * @throws HmacException
	 */
	public UserDto authenticate(LoginDto loginDTO, HttpServletRequest request, HttpServletResponse response) {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				loginDTO.getLogin(), loginDTO.getPassword());
		Authentication authentication = null;

		try {
			authentication = authenticationManager.authenticate(authenticationToken);
		} catch (BadCredentialsException e) {
			try {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
				return null;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (AuthenticationCredentialsNotFoundException x) {
			try {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
				return null;
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}			
		}
				
		SecurityContextHolder.getContext().setAuthentication(authentication);
 
		// Retrieve security user after authentication
		UserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.getLogin());

		UserDto userDTO = userService.findByUserName(userDetails.getUsername());

		// SecurityUser securityUser = (SecurityUser)
		// userDetailsService.loadUserByUsername(loginDTO.getLogin());
		SecurityUser securityUser = new SecurityUser(userDTO.getId(), userDetails.getUsername(),
				userDetails.getPassword(), null, userDetails.getAuthorities());

		// Parse Granted authorities to a list of string authorities
		List<String> authorities = new ArrayList<>();
		for (GrantedAuthority authority : securityUser.getAuthorities()) {
			authorities.add(authority.getAuthority());
		}

		// UserDto userDTO = new UserDto();
		userDTO.setId(securityUser.getId());
		userDTO.setLogin(securityUser.getUsername());
//		userDTO.setAuthorities(securityUser.getAuthorities());
		userDTO.setProfile(securityUser.getProfile());
		return userDTO;
	}

	/**
	 * Logout a user - Clear the Spring Security context - Remove the stored
	 * UserDTO secret
	 */
	public void logout() {
		if (SecurityContextHolder.getContext().getAuthentication() != null
				&& SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
			// SecurityUser securityUser = (SecurityUser)
			// SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			User securityUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			// UserDto userDTO = userService.findById(securityUser.getId());
			UserDto userDTO = userService.findByUserName(securityUser.getUsername());
			if (userDTO != null) {
				userDTO.setPublicSecret(null);
			}

		}
	}

	/**
	 * Authentication for every request - Triggered by every http request except
	 * the authentication
	 * 
	 * @see com.rkc.zds.config.security.XAuthTokenFilter Set the authenticated
	 *      user in the Spring Security context
	 * @param userName
	 *            userName
	 */
	public void tokenAuthentication(String userName) {
		UserDetails details = userDetailsService.loadUserByUsername(userName);
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(details,
				details.getPassword(), details.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authToken);
	}

	public UserDto findByUserName(String login) {
		UserDto userDTO = userService.findByUserName(login);
		return userDTO;
	}

}

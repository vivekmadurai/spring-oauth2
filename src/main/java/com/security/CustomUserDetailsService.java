package com.security;

import java.util.Iterator;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.openid.OpenIDAttribute;
import org.springframework.security.openid.OpenIDAuthenticationToken;

import com.user.User;
import com.user.UserList;

public class CustomUserDetailsService implements UserDetailsService, AuthenticationUserDetailsService<OpenIDAuthenticationToken> {

	private static final List<GrantedAuthority> DEFAULT_AUTHORITIES = AuthorityUtils.createAuthorityList("ROLE_USER");
	
	
	/**
	 * Implementation of {@code UserDetailsService}. We only need this to satisfy the {@code RememberMeServices}
	 * requirements.
	 */

	public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {

		for (Iterator iterator = UserList.userList.iterator(); iterator.hasNext();) {
			User emp = (User) iterator.next();
			if(emp.getUsername().equals(id)) {
				return emp;
			}
		}
		throw new UsernameNotFoundException(id);
	}

	/**
	 * Implementation of {@code AuthenticationUserDetailsService} which allows full access to the submitted
	 * {@code Authentication} object. Used by the OpenIDAuthenticationProvider.
	 */

	public UserDetails loadUserDetails(OpenIDAuthenticationToken token) {
		String id = token.getIdentityUrl();
		for (Iterator iterator = UserList.userList.iterator(); iterator.hasNext();) {
			User emp = (User) iterator.next();
			if(emp.getUsername().equals(id)) {
				return emp;
			}
		}
		
		User user = null;
		String email = null;
		String firstName = null;
		String lastName = null;
		String fullName = null;

		List<OpenIDAttribute> attributes = token.getAttributes();

		for (OpenIDAttribute attribute : attributes) {
			if (attribute.getName().equals("email")) {
				email = attribute.getValues().get(0);
			}
			if (attribute.getName().equals("firstname")) {
				firstName = attribute.getValues().get(0);
			}
			if (attribute.getName().equals("lastname")) {
				lastName = attribute.getValues().get(0);
			}
			if (attribute.getName().equals("fullname")) {
				fullName = attribute.getValues().get(0);
			}
		}
		if (fullName == null) {
			StringBuilder fullNameBldr = new StringBuilder();
			if (firstName != null) {
				fullNameBldr.append(firstName);
			}
			if (lastName != null) {
				fullNameBldr.append(" ").append(lastName);
			}
			fullName = fullNameBldr.toString();
		}
		user = new User(id, DEFAULT_AUTHORITIES);
		user.setName(fullName);
		user.setEmail(email);

		UserList.userList.add(user);

		return user;
	}
}
package com.flo.config;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsServiceImpl implements org.springframework.security.core.userdetails.UserDetailsService
{

	@Autowired User user;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
	{
		if(username==null || username.equals(user.getUsername())==false)
		{
			throw new UsernameNotFoundException(username+" not found");
		}
		
	    List<SimpleGrantedAuthority> grantedAuthorities = user
	    		.getAuthorities().stream()
	    		.map(auth -> new SimpleGrantedAuthority(auth))
	    		.collect(Collectors.toList());
	    
	    return new org.springframework.security.core.userdetails.User(
	    		user.getUsername(), 
	    		user.getPassword(), 
	    		grantedAuthorities
	    		);
	}

}

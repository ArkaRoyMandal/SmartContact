package com.smartcontact.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smartcontact.dao.UserRepositry;
import com.smartcontact.entites.User;

public class UserDetailsServiceImpl implements UserDetailsService {
	
	@Autowired
	private UserRepositry userRepositry;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		//featching user from database
		
	  User user=userRepositry.getUserByUserName(username);
	  
	  if(user==null) {
		  throw new UsernameNotFoundException("could not found user!!");
	  }
		
		CustomUserDetails customUserDetails=new CustomUserDetails(user);
		return customUserDetails;
	}

}

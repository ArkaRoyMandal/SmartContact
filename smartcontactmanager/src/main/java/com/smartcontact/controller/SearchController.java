package com.smartcontact.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smartcontact.dao.ContactRepositry;
import com.smartcontact.dao.UserRepositry;
import com.smartcontact.entites.Contact;
import com.smartcontact.entites.User;

@RestController
public class SearchController {
	
	@Autowired
	private UserRepositry userRepositry;
	@Autowired
	private ContactRepositry contactRepositry;

	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query,Principal principal){
//		System.out.println(query);
		User user=this.userRepositry.getUserByUserName(principal.getName());
		List<Contact> contacts = this.contactRepositry.findByNameContainingAndUser(query, user);
		
		return ResponseEntity.ok(contacts);
	}
}

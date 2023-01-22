package com.smartcontact.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smartcontact.entites.Contact;
import com.smartcontact.entites.User;

public interface ContactRepositry extends JpaRepository<Contact, Integer> {
  //pagination......
	
	@Query("from Contact as c where c.user.id=:userId")
	//current page
	//contact per page -5
	public Page<Contact> findContactsByUser(@Param("userId")int userId,Pageable pageable);
	
	//search
	public List<Contact> findByNameContainingAndUser(String name,User user);
}

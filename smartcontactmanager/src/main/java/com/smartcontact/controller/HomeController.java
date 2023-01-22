package com.smartcontact.controller;


import javax.servlet.http.HttpSession;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smartcontact.dao.UserRepositry;
import com.smartcontact.entites.User;
import com.smartcontact.helper.Message;

@Controller
public class HomeController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepositry userRepositry;
     
	/* Home Controller */
	@RequestMapping("/")
     public String homeController(Model model) {
		model.addAttribute("title","Home-Smart Contact Manager");
    	 return "home";
     }
	
	/* About Controller */
	@RequestMapping("/about")
	public String aboutController(Model model) {
		model.addAttribute("title","About-Smart Contact Manager");
		return "about";
	}
	
	/* Sign UP */
	@RequestMapping("/signUp")
	public String signupController(Model model) {
		model.addAttribute("title","SignUp-Smart Contact Manager");
		model.addAttribute("user",new User());
		return "signUp";
	}
	
	@PostMapping("/do_register")
	public String registerController( @ModelAttribute("user")User user,
@RequestParam(value = "agreement",defaultValue = "false") 
	boolean agreement,Model model,HttpSession session) {
		
		try {
			
	if(!agreement) {
	   System.out.println("You have not agreed the term and condition");
	   throw new Exception("You have not agreed the terms and condition");
	}
			
			
			
			user.setRole("ROLE_USER");
			user.setEnable(true);
			user.setImageUrl("defult.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			System.out.println("Aggrement"+agreement);
			System.out.println("User"+user);
			
			User result=this.userRepositry.save(user);
			model.addAttribute("user",new User());
			
			session.setAttribute("message", new Message("Sucessfully Register!!", "alert-success"));
			return "signUp";
			
			
		}catch(Exception e) {
			e.printStackTrace();
			model.addAttribute("user",user);
			session.setAttribute("message", new Message("Something Went Wrong!!"+e.getMessage(), "alert-danger"));
			return "signUp";
		}
		
	}
     
	/* LogIn */
	
	@RequestMapping("/signIn")
	public String loginController(Model model) {
		
		model.addAttribute("title","Login page");
		return "login.html";
	}
	
	@RequestMapping("/login_fail")
	public String loginfailController(Model model) {
		model.addAttribute("title","LogIn_Fail page");
		return "login_fail.html";
	}

}

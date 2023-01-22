package com.smartcontact.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smartcontact.Service.EmailService;
import com.smartcontact.dao.UserRepositry;
import com.smartcontact.entites.User;


@Controller
public class forgotPasswordController {
	
	Random random=new Random(1000);
	
	@Autowired
	private UserRepositry userRepositry;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@RequestMapping("/forgot")
	public String forgot() {
		return "forgot_email_form";
	}
	@PostMapping("/send-otp")
	public String verify(@RequestParam("email")String email,HttpSession session) {
		
		int otp = random.nextInt(9999);
		System.out.println(otp);
		
		

		String subject="OTP FROM SCM";
		String message="<h1> OTP ="+otp+"</h1>";
		String to=email;
		
		boolean flag = this.emailService.sendEmail(subject, message, to);
		
		if(flag) {
			
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
			
			return "verify-otp";
		}else {
			
			session.setAttribute("message", "check your Email id !!");
			return "forgot_email_form";
		}
		
		
		
	}
	@PostMapping("/verify_otp")
	public String verifyotp(@RequestParam("otp")int otp,HttpSession session) {
	 	int myOtp=(int)session.getAttribute("myotp");
	 	String email=(String)session.getAttribute("email");
	 	if(myOtp==otp) {
	 		
	 		User user = this.userRepositry.getUserByUserName(email);
	 		if(user==null) {
	 			session.setAttribute("message", "User doesnot exist with this email !!");
				return "forgot_email_form";
	 		}else {
	 			
	 		}
	 		return "password_change_form";
	 	}else {
	 		session.setAttribute("message", "You have entered wrong otp !!");
	 		return "verify-otp";
	 	}
	}
	
	@PostMapping("/change_password")
	public String changepassword(@RequestParam("newpassword")String newpassword,HttpSession session) {
		
		String email=(String)session.getAttribute("email");
		User user = this.userRepositry.getUserByUserName(email);
		user.setPassword(bCryptPasswordEncoder.encode(newpassword));
		this.userRepositry.save(user);
		
		return "redirect:/signIn?change=password changed successfully...";
	}

}

package com.smartcontact.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smartcontact.dao.ContactRepositry;
import com.smartcontact.dao.UserRepositry;
import com.smartcontact.entites.Contact;
import com.smartcontact.entites.User;
import com.smartcontact.helper.Message;

@Controller
@RequestMapping("/User")
public class UserController {
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
   
	@Autowired
	private UserRepositry userRepositry;
	
	@Autowired
	private ContactRepositry contactRepositry;
	
	//Common data on every handeler
	
	@ModelAttribute
	private void addCommonData(Model model,Principal principal) {
		//get user by email	
		 String userName=principal.getName();
		 System.out.println("USERNAME"+userName);
		 
		 User user=userRepositry.getUserByUserName(userName);
		 System.out.println("USER"+user);
		 
		 //set the value in dashboard
		 
		 model.addAttribute("user",user);
	}
	
	
	//User home page
	
	@RequestMapping("/index")
	public String dashbord(Model model ,Principal principal) {
		
		model.addAttribute("title","User Dashbord");
		return "normal/User_dashbord";
	}
	
	
	
	/* Add controller handler */
	@RequestMapping("/addContact")
	public String addContactController(Model model) {
		
		model.addAttribute("title","Add Contact");
		model.addAttribute("contact",new Contact());
		
		return "normal/Add_contact";
	}
	
	/*View the contact*/
	
    @RequestMapping(value = "/process-contact",method = RequestMethod.POST)
	public String processcontactController(@ModelAttribute Contact contact,
			@RequestParam("profileimage")MultipartFile file,
			Principal principal,HttpSession session) {
    	
    	try {
    	String name=principal.getName();
    	User user=this.userRepositry.getUserByUserName(name);
    	
    	if(file.isEmpty()) {
    		//if the file is empty then try our message
    		
    	System.out.println("file is empty");
    	contact.setImage("contact.png");
    		
    	}else {
    		contact.setImage(file.getOriginalFilename());
    		
    		File savefile=new ClassPathResource("static/img").getFile();
    		Path path = Paths.get(savefile.getAbsolutePath()+File.separator+file.getOriginalFilename());
 
    		Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
    		System.out.println("Image is Uploaded");
    		
    		
    		
    		
    	}
    	
    	contact.setUser(user);
    	
    	user.getContacts().add(contact);
    	
    	this.userRepositry.save(user);
    	
    	
		System.out.println("DATA"+contact);
		System.out.println("Adding to the database");
		
		//message success..........
		session.setAttribute("message", new Message("Your Contact is added!!", "success"));
		
    	}catch (Exception e) {
			System.out.println("ERROR!!"+e.getMessage());
			
			//message error.....
			session.setAttribute("message", new Message("something went wrong Try anain!!", "danger"));
		}
		return "normal/Add_contact";
	}
    
    @GetMapping("/showContact/{page}")
    public String showContactController(@PathVariable("page")Integer page ,Model model,Principal principal) {
    	
    	model.addAttribute("title","Show Contact");
    	
    	String userName=principal.getName();
    	User user=this.userRepositry.getUserByUserName(userName);
    	Pageable pageable = PageRequest.of(page, 3);
    	
    	Page<Contact> contacts = this.contactRepositry.findContactsByUser(user.getId(),pageable);
    	
    	model.addAttribute("contacts",contacts);
    	model.addAttribute("CurrentPage",page);
    	model.addAttribute("totalPages",contacts.getTotalPages());
    	
    	return "normal/showcontact";
    }
    
    /* Showing Contact detail*/
    
    @GetMapping("/{cID}/contact")
    public String showContactDetailsController(@PathVariable("cID") Integer cID,Model model,Principal principal) {
    	System.out.println("cID: "+cID);
    	
          Optional<Contact> contactoptional=this.contactRepositry.findById(cID);
          Contact contact= contactoptional.get();
          
          String Username=principal.getName();
          User user = this.userRepositry.getUserByUserName(Username);
          
          if(user.getId()==contact.getUser().getId()) {
        	  
        	  model.addAttribute("contact",contact);
        	  model.addAttribute("title",contact.getName());
           }

    	 return "normal/contact_detail";   
    }
    
    /* Delete Contact*/
    
    @GetMapping("/delete/{cID}")
    public String deleteContactController(@PathVariable("cID") Integer cID,Principal principal,HttpSession session) {
    	Contact contact=this.contactRepositry.findById(cID).get();
    	
       String UserName=principal.getName(); 
//       User user =this.userRepositry.getUserByUserName(UserName);\
       
       User user=this.userRepositry.getUserByUserName(principal.getName());
       

    	   
//    	   contact.setUser(null);
           
//           this.contactRepositry.delete(contact);
    	   
    	   user.getContacts().remove(contact);
    	   
    	   this.userRepositry.save(user);
           
        	
           session.setAttribute("message",new Message("Contact deleted successfully......", "success"));
       
    	
    
    	return "redirect:/User/showContact/0";
    }
    
    /*Update User*/
   
    @PostMapping("/Update_Contact/{cID}")
   public String updateController(@PathVariable("cID") Integer cID,Model model) {
	   
      Contact contact=this.contactRepositry.findById(cID).get();
      
      model.addAttribute("title","Update_Contact");
      model.addAttribute("contact",contact);
    	
    	return "normal/show_Update";
   }
    
    
    /* process Update Form*/
    
    @PostMapping("/process-Update")
    public String processUpdate(@ModelAttribute Contact contact,Principal principal,
    		@RequestParam("profileimage")MultipartFile file,
    		Model model,HttpSession session) {
    	
    	
    	try {
    		
    	 Contact oldcontactDetail=this.contactRepositry.findById(contact.getcID()).get();
    		
    		if(!file.isEmpty()) {
    			
    			//delete old photo
    			File deletefile=new ClassPathResource("static/img").getFile();
    			
    			File file1=new File(deletefile, oldcontactDetail.getImage());
    			
    			file1.delete();
    			
    			
    			
    			//update photo
    			
    			File savefile=new ClassPathResource("static/img").getFile();
        		Path path = Paths.get(savefile.getAbsolutePath()+File.separator+file.getOriginalFilename());
     
        		Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        		
        		contact.setImage(file.getOriginalFilename());
    			
    		}else {
    			contact.setImage(oldcontactDetail.getImage());
    		}
    		
    		String name=principal.getName(); 
    		User user=	this.userRepositry.getUserByUserName(name);
    				  
    		 contact.setUser(user); 
    		
    		this.contactRepositry.save(contact);
    		
    		session.setAttribute("message",new Message("Update successfully......", "success"));
    		
    		
    	}catch(Exception e) {
    		e.printStackTrace();
    		
    	}
		
    	
    	return "redirect:/User/"+contact.getcID()+"/contact";
    }
    
    
    @GetMapping("/profile")
    public String profile(Model model) {
    
    	model.addAttribute("title","profile");
    	
    	return "normal/profile";
    }
    
    
    //open settings handler
    @GetMapping("/settings")
    public String openSettings() {
    	return "normal/settings";
    }
    
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("oldpassword")String oldpassword,@RequestParam("newpassword")String newpassword,Principal principal,HttpSession session) {
    	
    	System.out.println("OLD PASSWORD"+oldpassword);
    	System.out.println("NEW PASSWORD"+newpassword);
    	
    	String username = principal.getName();
    	User currentUser = this.userRepositry.getUserByUserName(username);
    	
    	if(this.bCryptPasswordEncoder.matches(oldpassword, currentUser.getPassword())) {
    		
    		currentUser.setPassword(this.bCryptPasswordEncoder.encode(newpassword));
    		this.userRepositry.save(currentUser);
    		session.setAttribute("message", new Message("Your password is successfully changed", "success"));
    	}else {
    		session.setAttribute("message", new Message("Wrong Password!!", "danger"));
    		return "redirect:/User/settings";
    	}
    	
    	
    	return "redirect:/User/index";
    }
    
    @PostMapping("/change-profile")
    public String changeProfile(@RequestParam("name")String name,@RequestParam("about")String about,Principal principal,HttpSession session) {
    	
    	String username = principal.getName();
    	User currentname = this.userRepositry.getUserByUserName(username);
    	currentname.setName(name);
    	currentname.setAbout(about);
    	this.userRepositry.save(currentname);
    	session.setAttribute("message", new Message("Your profile is successfully changed", "success"));
    	return "redirect:/User/profile";
    }
    
}

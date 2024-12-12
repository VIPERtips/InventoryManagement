package com.tips.InventoryManagement.controller;

import java.security.NoSuchAlgorithmException;

import org.hibernate.sql.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tips.InventoryManagement.config.SecurityConfig;
import com.tips.InventoryManagement.models.User;
import com.tips.InventoryManagement.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class NavController {
	@Autowired
	private UserService userService;
	
	
	@GetMapping("/forgot-password")
	public String forgotPassword() {
		return "forgot-password";
	}
	
	@PostMapping("/forgot-password")
	public String resetPassword(@RequestParam String email) {
		
		return "forgot-password";
	}
	@GetMapping("/home")
	public String home(Model model, HttpSession session) {
		User user = (User) session.getAttribute("user");
	    if(user == null) {
	        return "redirect:/login";
	    }
	    model.addAttribute("user", user);
		return "fragments";
	}
	
	
	
	@GetMapping("/profile")
	public String profile(Model model, HttpSession session) {
	    User user = (User) session.getAttribute("user");
	    model.addAttribute("pageTitle", "Profile");
	    if(user != null && ("Admin").equals(user.getUserType())) {
	    	 
	    model.addAttribute("user", user);
	    return "profile";
	    
	    } else if(user != null && user.getUserType().equals("User")) {
	    	 model.addAttribute("user", user);
	 	    return "user-profile";
	    }
	    return "redirect:/login";
	}
	
	
	
	@GetMapping("/create-profile")
	public String createProfile(Model model, HttpSession session) {
		 User user = (User) session.getAttribute("user");
		 model.addAttribute("pageTitle", "Update Profile");
		 model.addAttribute("successMsg",model.asMap().get("successMsg"));
		 model.addAttribute("errorMsg",model.asMap().get("errorMsg"));
		    if(user != null && ("Admin").equals(user.getUserType())) {
		        
		    model.addAttribute("user", user);
		    return "create-profile";
		    
		    } else if(user != null && user.getUserType().equals("User")) {
		    	 model.addAttribute("user", user);
		 	    return "create-profile";
		    }
		    return "redirect:/login";
	}
	
	@PostMapping("/create-profile")
	public String updateProfile(@ModelAttribute("user")User user, Model model, HttpSession session, RedirectAttributes redirectAttributes) throws NoSuchAlgorithmException {
		User currentUser = (User) session.getAttribute("user");
		model.addAttribute("pageTitle", "Update Profile");
		if(currentUser != null) {
			if(user.getPassword() != null && !user.getPassword().isEmpty()) {
				if(user.getPassword().equals(user.getConfirmPassword())) {
					String hashedPassword = SecurityConfig.hashPassword(user.getPassword());
					user.setPassword(hashedPassword);
				} else {
					redirectAttributes.addFlashAttribute("errorMsg","Passwords do not match");
					return "redirect:/create-profile";
				}
			}
			user.setId(currentUser.getId());
			user.setUserType(currentUser.getUserType());
			user.setCreatedBy(currentUser.getCreatedBy());
			user.setEmail(currentUser.getEmail());
			userService.saveUser(user);
			redirectAttributes.addFlashAttribute("successMsg","Profile updated successfully");
		}
		//this shud update
		return "redirect:/create-profile";
	}
	
	
}

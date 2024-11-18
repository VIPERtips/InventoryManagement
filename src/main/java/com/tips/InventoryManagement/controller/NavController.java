package com.tips.InventoryManagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.tips.InventoryManagement.models.User;

import jakarta.servlet.http.HttpSession;

@Controller
public class NavController {
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
	    if(user == null) {
	        return "redirect:/login";
	    }
	    model.addAttribute("user", user);
	    return "profile";
	}
	
	
	
}

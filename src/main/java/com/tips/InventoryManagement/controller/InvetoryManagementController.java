package com.tips.InventoryManagement.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.tips.InventoryManagement.models.User;
import com.tips.InventoryManagement.service.ProductService;
import com.tips.InventoryManagement.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class InvetoryManagementController {
	@Autowired
	private UserService userService;
	@Autowired
	private ProductService productService;
	
	@GetMapping("/dashboard/products")
	public String showProducts(HttpSession session, Model model) {
	    User user = (User) session.getAttribute("user");
	    if (user != null) {
	        
	    	model.addAttribute("products", productService.findAllProducts());
	        model.addAttribute("pageTitle", "Products");
	        return "Products";
	    } else {
	        return "redirect:/login";
	    }
	}

	
	@GetMapping("/dashboard")
	public String dashboard(HttpSession session, Model model) {
		User user = (User) session.getAttribute("user");
		if(user != null) {
			model.addAttribute("username", user.getEmail());
			model.addAttribute("pageTitle", "Dashboard");
			return "index";
		} else {
			return "redirect:/login";
		}
	}
	
	@GetMapping("")
	public String home() {
		return "login";
	}
	
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	@PostMapping("/login")
	public String handleLogin(@RequestParam String email,@RequestParam String password,Model model, HttpSession http) {
		
		Optional<User> user = userService.findByEmail(email);
		if(user.isPresent() && user.get().getPassword().equals(password)) {
			http.setAttribute("user", user.get());
			return "redirect:/dashboard";
		} else {
			model.addAttribute("error", "Invalid username or password");
			System.out.println("Login failed for user: " + email);
			return "login";
		}	
	}
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/login";
	}
	
	@GetMapping("/register")
	public String register(@ModelAttribute("user") User  user){
		return "register";
	}
	
	@PostMapping("/register")
	public String handleRegistration(@ModelAttribute("user") User  user,Model model) {
		if(user.getPassword().equals(user.getConfirmPassword())) {
			userService.saveUser(user);
		} else {
			model.addAttribute("passwordError", "Passwords do not match");
			return "register";
		}
		
		return "redirect:/login";
	}
}

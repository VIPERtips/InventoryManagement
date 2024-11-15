package com.tips.InventoryManagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class NavController {
	@GetMapping("/home")
	public String home() {
		return "fragments";
	}
	
	
	
}

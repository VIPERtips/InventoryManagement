package com.tips.InventoryManagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import com.tips.InventoryManagement.models.Product;
import com.tips.InventoryManagement.models.Transaction;
import com.tips.InventoryManagement.models.User;
import com.tips.InventoryManagement.service.ProductService;
import com.tips.InventoryManagement.service.TransactionService;

import jakarta.servlet.http.HttpSession;

@Controller
public class TransactionController {
	@Autowired
	private TransactionService transactionService;
	
	@GetMapping("/show-transaction")
	public String showTransaction(@RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "10") int size, HttpSession session, Model model) {
		User user = (User) session.getAttribute("user");
		if(user != null && user.getUserType().equals("Admin")) {
				Page<Transaction> transactionPage = transactionService.getAllTransactions(page,size);
				model.addAttribute("transactions", transactionPage.getContent()); 
		        model.addAttribute("currentPage", transactionPage.getNumber());
		        model.addAttribute("totalPages", transactionPage.getTotalPages());
				model.addAttribute("pageTitle", "Admin Transaction History" );
				return "transaction";
			} else if (user != null && user.getUserType().equals("User")) {
				Page<Transaction> transactionPage = transactionService.getTransactionsByUserId(user.getId(), page,size);
				model.addAttribute("transactions", transactionPage.getContent()); 
		        model.addAttribute("currentPage", transactionPage.getNumber());
		        model.addAttribute("totalPages", transactionPage.getTotalPages());
				model.addAttribute("pageTitle", "User Transaction History" );
				return "user-transaction";
			}
		
		else {
			return "redirect:/login";
	}
		
	}
}

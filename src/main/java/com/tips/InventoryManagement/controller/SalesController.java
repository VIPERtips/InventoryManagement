package com.tips.InventoryManagement.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tips.InventoryManagement.models.Product;
import com.tips.InventoryManagement.models.SaleDto;
import com.tips.InventoryManagement.models.Transaction;
import com.tips.InventoryManagement.models.Sale;
import com.tips.InventoryManagement.models.User;
import com.tips.InventoryManagement.service.ProductService;
import com.tips.InventoryManagement.service.SaleService;
import com.tips.InventoryManagement.service.TransactionService;

import jakarta.servlet.http.HttpSession;

@Controller
public class SalesController {
	@Autowired
	private ProductService productService;
	
	@Autowired
	private SaleService saleService;
	
	@Autowired TransactionService transactionService;
	
	@GetMapping("/new-sale")
	public String showSalesPage(@RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "10") int size, HttpSession session,Model model) {
		User user = (User) session.getAttribute("user");
		if(user != null) {
			Page<Sale> salesPage = saleService.getSalesByUserId(user.getId(), page,size);

	        model.addAttribute("sales", salesPage.getContent()); 
	        System.out.println("Sales Page total elements : "+salesPage.getContent());
	        model.addAttribute("currentPage", salesPage.getNumber());
	        System.out.println("Sales page number"+salesPage.getNumber());
	        model.addAttribute("totalPages", salesPage.getTotalPages());
	        System.out.println("Sales page total elements "+salesPage.getTotalElements()+" sales page total pages = "+salesPage.getTotalPages());
			model.addAttribute("pageTitle", "Sales");
			return "salespage";
		} else {
			model.addAttribute("errorMessage", "No sales found");
			return "redirect:/login";
	}
		
	}
	@PostMapping("/new-sale")
	public String createSales(@ModelAttribute("saleDto") @Validated SaleDto sale,BindingResult result
			,Model model,HttpSession session) {
		
		if (result.hasErrors()) {
	        return "salespage";  
	    }
		
		Product product = productService.findProductByProductNameAndBarcode(sale.getProductName(),sale.getBarcode());
		
		if(product == null) {
			model.addAttribute("error","Product not Found");
			return "salespage";
		}
		
		if(product.getProductCode() < sale.getQuantity()) {
			model.addAttribute("error","Insufficient quantity");
			return "salespage";
		}
		
		Integer newProductCode = (int) (product.getProductCode() - sale.getQuantity());
		product.setProductCode(newProductCode);
		double totalAmount = sale.getQuantity() * sale.getUnitPrice();
		
		sale.setBrand(product.getProductDescription());
		Sale sale1 = new Sale();
		sale1.setProductName(sale.getProductName());
		sale1.setBrand(sale.getBrand());
		sale1.setQuantity(sale.getQuantity());
		sale1.setUnitPrice(sale.getUnitPrice());
		sale1.setTotalAmount(totalAmount);
		sale1.setPaymentMethod(sale.getPaymentMethod());
		sale1.setSaleDate(LocalDateTime.now());
		User user = (User) session.getAttribute("user");
		sale1.setUser(user);
		
		sale1.setReceiverNumber(sale.getReceiverNumber());
		saleService.saveSale(sale1);
		model.addAttribute("successMsg","Sale processed successfully");
		return "redirect:/new-sale";
	}
	
	@GetMapping("/sale/approve")
	public String approveSale(@RequestParam int id,HttpSession session,Model model) {

		 Sale sale= (Sale) saleService.getSaleById(id);
		if(sale != null) {
			Transaction transaction = new Transaction();
			transaction.setProductName(sale.getProductName());
			transaction.setBrand(sale.getBrand());
			transaction.setTotalAmount(sale.getTotalAmount());
			transaction.setPaymentMethod(sale.getPaymentMethod());
			transaction.setQuantity(sale.getQuantity());
			transaction.setReceiverNumber(sale.getReceiverNumber());
			transaction.setTransactionDate(sale.getSaleDate());
			User user = (User) session.getAttribute("user");
			transaction.setUser(user);
		
			transactionService.saveTransaction(transaction);
		//save transaction
		sale.setStatus("Approved");
		saleService.saveSale(sale);
		
		model.addAttribute("successMessage", "Sale approved successfully");
		} else {
			model.addAttribute("errorMessage", "Sale not found");
			return "salespage";
		}
		return "redirect:/new-sale";
	}
	
	@GetMapping("/sale/cancel")
	public String deleteSale(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
	    try {
	       
	        Optional<Sale> saleOpt = saleService.findSaleById(id);
	        if (saleOpt.isPresent()) {
	            Sale sale = saleOpt.get();
	            //remember to restore product quantity and price and amount 
	            //sort of updating to the database
	            //fixthis part
	            if(sale.getStatus() == "Approved") {
	            	saleService.deleteSaleById(id);
	            	transactionService.deleteProductById(id);
	            }
	            
	                
	                
	                

	            
	            saleService.deleteSaleById(id);

	            redirectAttributes.addFlashAttribute("successMsg", "Sale cancelled successfully.");
	        } else {
	            redirectAttributes.addFlashAttribute("errorMsg", "Sale not found.");
	        }
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("errorMsg", "Failed to delete sale: " + e.getMessage());
	    }
	    return "redirect:/new-sale";  
	}
	
	
	
}
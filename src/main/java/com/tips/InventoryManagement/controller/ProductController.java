package com.tips.InventoryManagement.controller;

import java.io.InputStream;
import java.nio.file.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import com.tips.InventoryManagement.models.Product;
import com.tips.InventoryManagement.models.ProductDto;
import com.tips.InventoryManagement.models.User;
import com.tips.InventoryManagement.service.ProductService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ProductController {
	@Autowired
	private ProductService productService;
	@GetMapping("/new-product")
	public String showAddProductForm(@ModelAttribute("product") Product product,HttpSession session, Model model) {
		User user = (User) session.getAttribute("user");
		if(user != null) {
			model.addAttribute("username", user.getEmail());
			model.addAttribute("pageTitle", "Add Products");
			return "additem";
		} else {
			return "redirect:/login";
	}
	}
	
	@PostMapping("/new-product")
	public String createProduct(@ModelAttribute("productDto") @Validated ProductDto productDto, 
	                            BindingResult result, Model model, HttpSession session) {
	    
	    if (result.hasErrors()) {
	        return "additem";  
	    }
	    
	    if (productDto.getImageFile().isEmpty()) {
	        model.addAttribute("error", "Image file is required");
	        return "additem";  
	    }
	    
	   
	    MultipartFile file = productDto.getImageFile();
	    String storageFileName = file.getOriginalFilename();
	    
	    try {
	        String uploadDir = "public/images/";
	        Path uploadPath = Paths.get(uploadDir);
	        
	        if (!Files.exists(uploadPath)) {
	            Files.createDirectories(uploadPath);
	        }
	        
	        try (InputStream inputStream = file.getInputStream()) {
	            Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
	        }
	    } catch (Exception e) {
	        model.addAttribute("error", "Error uploading image: " + e.getMessage());
	        return "additem";  
	    }
	    
	    Product product = new Product();
	    product.setProductName(productDto.getProductName());
	    product.setProductDescription(productDto.getProductDescription());
	    product.setProductCode(productDto.getProductCode());
	    product.setImageInput(storageFileName);
	    product.setBarcode(productDto.getBarcode());
	    product.setCategory(productDto.getCategory());
	    product.setUnitCost(productDto.getUnitCost());
	    product.setCostPrice(productDto.getCostPrice());
	    
	    productService.saveProduct(product);
	    
	    model.addAttribute("successMsg", "Product added successfully");
	    return "redirect:/new-product";  
	}

}

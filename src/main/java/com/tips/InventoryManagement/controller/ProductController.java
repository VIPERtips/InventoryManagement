package com.tips.InventoryManagement.controller;

import java.io.InputStream;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tips.InventoryManagement.logger.ReportLogger;
import com.tips.InventoryManagement.models.Product;
import com.tips.InventoryManagement.models.ProductDto;
import com.tips.InventoryManagement.models.User;
import com.tips.InventoryManagement.service.ProductService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ProductController {
	@Autowired
	private ProductService productService;
	
	@GetMapping("/products")
	public String showProducts(@RequestParam(defaultValue = "0") int page, 
	            @RequestParam(defaultValue = "10") int size, HttpSession session, Model model) {
	    User user = (User) session.getAttribute("user");
	    if (user != null) {
	        Page<Product> productPage;
	        // Admin can see all products created by themselves or any other user.
	        if (user.getUserType().equals("Admin")) {
	            productPage = productService.getProductsByUserId(user.getId(), page, size);
	        } else {
	            return "redirect:/login";
	        }
	        model.addAttribute("products", productPage.getContent()); 
	        model.addAttribute("currentPage", productPage.getNumber());
	        model.addAttribute("totalPages", productPage.getTotalPages());
	        model.addAttribute("pageTitle", user.getUserType() + " Products");
	        return "Products";
	    }
	    return "redirect:/login";
	}
	
	@GetMapping("/products-for-user")
	public String showProductsByCreator(@RequestParam("creatorId") Integer creatorId, 
	                                    @RequestParam(defaultValue = "0") int page,
	                                    @RequestParam(defaultValue = "10") int size,
	                                    Model model, HttpSession session) {
	    User user = (User) session.getAttribute("user");
	    if (user != null) {
	        Page<Product> productPage = productService.getProductsByCreatorId(creatorId, page, size);
	        model.addAttribute("products", productPage.getContent());
	        model.addAttribute("currentPage", productPage.getNumber());
	        model.addAttribute("totalPages", productPage.getTotalPages());
	 
            model.addAttribute("creatorId", creatorId);
	        Integer totalProductQuantity = productService.getTotalProductQuantityByUserId(creatorId); 
	        model.addAttribute("totalProductQuantity", totalProductQuantity);
	        model.addAttribute("totalCost", productService.getTotalCost(creatorId));
	        model.addAttribute("totalRevenue", productService.getTotalRevenue(creatorId));
	        model.addAttribute("expectedProfit", productService.getExpectedRevenue(creatorId));

	        
	        model.addAttribute("pageTitle", "User Products");
	        return "user-products";
	    }
	    return "redirect:/login";
	}


	
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
	    } else {

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
	    User user = (User) session.getAttribute("user");
	    Product product = new Product();
	    
	    
	    product.setProductName(productDto.getProductName());
	    product.setProductDescription(productDto.getProductDescription());
	    product.setProductCode(productDto.getProductCode());
	    product.setImageInput(storageFileName);
	    product.setBarcode(productDto.getBarcode());
	    product.setCategory(productDto.getCategory());
	    product.setUnitCost(productDto.getUnitCost());
	    product.setCostPrice(productDto.getCostPrice());
	    if (product.getCreatedBy() == null) {
            product.setCreatedBy(user); 
        }

        if (product.getUser() == null) {
            product.setUser(user); 
        }
	    if(productService.isBarcodeDuplicate(product)) {
	    	model.addAttribute("error","Duplicate barcode used please check your barcode field");
	    	return "additem";
	    }
	    productService.saveProduct(product);
	    String username = user.getFirstName()+ " " + user.getLastName();
	    ReportLogger.logReport("Added ",product.getProductName(), product.getProductDescription(), product.getProductCode(), product.getUnitCost(), product.getCostPrice(), product.getCategory(), username);
	    model.addAttribute("successMsg", "Product added successfully");
	    return "additem";
	}
	}
	@GetMapping("/products/delete")
	public String deleteProduct(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes,HttpSession session) {
	    try {
	       
	        Optional<Product> productOpt = productService.findProductById(id);
	        if (productOpt.isPresent()) {
	            Product product = productOpt.get();

	            
	            String imageFileName = product.getImageInput();
	            if (imageFileName != null && !imageFileName.isEmpty()) {
	                String uploadDir = "public/images/";
	                Path imagePath = Paths.get(uploadDir + imageFileName);
	                
	                
	                try {
	                    Files.deleteIfExists(imagePath);
	                } catch (Exception e) {
	                    redirectAttributes.addFlashAttribute("errorMsg", "Error deleting image file: " + e.getMessage());
	                    return "redirect:/products";
	                }
	            }

	            User user = (User) session.getAttribute("user");
	            productService.deleteProductById(id);
	            String username = user.getFirstName()+ " " + user.getLastName();
	    	    ReportLogger.logReport("Deleted ",product.getProductName(), product.getProductDescription(), product.getProductCode(), product.getUnitCost(), product.getCostPrice(), product.getCategory(), username);
	            redirectAttributes.addFlashAttribute("successMsg", "Product and associated image deleted successfully.");
	        } else {
	            redirectAttributes.addFlashAttribute("errorMsg", "Product not found.");
	        }
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("errorMsg", "Failed to delete product: " + e.getMessage());
	    }
	    return "redirect:/products";  
	}
	
	
	@GetMapping("/products/edit")
	public String showEditProductForm(@RequestParam("id") Integer id, Model model, HttpSession session) {
	    User user = (User) session.getAttribute("user");
	    if (user != null) {
	        Optional<Product> productOpt = productService.findProductById(id);
	        if (productOpt.isPresent()) {
	            Product product = productOpt.get();
	            
	            
	            model.addAttribute("product", product);
	            model.addAttribute("categories", List.of("Other", "Laptops", "Mobile Phones", "Tablets", "Accessories")); 
	            model.addAttribute("username", user.getEmail());
	            model.addAttribute("pageTitle", "Edit Product");
	            
	            return "edititem";  
	        } else {
	            model.addAttribute("errorMsg", "Product not found.");
	            return "redirect:/products";
	        }
	    } else {
	        return "redirect:/login";
	    }
	}


	    
	 @PostMapping("/products/edit")
	 public String updateProduct(@ModelAttribute("product") @Validated Product product, 
	                             BindingResult result, HttpSession session,
	              /*added the true*/               @RequestParam(value = "imageFile", required = true) MultipartFile imageFile, 
	                             Model model) {
	     if (result.hasErrors()) {
	         return "edititem";  
	     }

	     if (imageFile != null && !imageFile.isEmpty()) {
	         
	         String storageFileName = imageFile.getOriginalFilename();

	         try {
	             String uploadDir = "public/images/";
	             Path uploadPath = Paths.get(uploadDir);

	            
	             if (!Files.exists(uploadPath)) {
	                 Files.createDirectories(uploadPath);
	             }

	             
	             try (InputStream inputStream = imageFile.getInputStream()) {
	                 Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
	             }
	             User user = (User) session.getAttribute("user");
	             product.setUser(user);
	             product.setImageInput(storageFileName);
	             String username = user.getFirstName()+ " " + user.getLastName();
	     	    ReportLogger.logReport("Update on ",product.getProductName(), product.getProductDescription(), product.getProductCode(), product.getUnitCost(), product.getCostPrice(), product.getCategory(), username);
	         } catch (Exception e) {
	             model.addAttribute("error", "Error uploading image: " + e.getMessage());
	             return "edititem";  
	         }
	     }
	     
	     
	     productService.saveProduct(product);
	     
	     
	     model.addAttribute("successMsg", "Product updated successfully.");
	     return "redirect:/products";  
	 }




}

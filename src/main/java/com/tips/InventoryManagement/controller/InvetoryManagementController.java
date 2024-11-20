package com.tips.InventoryManagement.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tips.InventoryManagement.models.Product;
import com.tips.InventoryManagement.models.User;
import com.tips.InventoryManagement.repository.ProductRepository;
import com.tips.InventoryManagement.service.ProductService;
import com.tips.InventoryManagement.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class InvetoryManagementController {
	@Autowired
	private UserService userService;
	@Autowired
	private ProductService productService;
	
	@Autowired
	private ProductRepository productRepository;
	
	private boolean isAdmin(User user) {
	    return user != null && "Admin".equals(user.getUserType());
	}

	private boolean isUser(User user) {
	    return user != null && "User".equals(user.getUserType());
	}
	
	private void populateDashboard(Model model, User user) {
	    // Fetch the products for the logged-in user
	    List<Product> products = productRepository.findByUserId(user.getId());
	    
	    if (products.isEmpty()) {
	        model.addAttribute("message", "No products found for the user.");
	    }
	    
	    // Get distinct categories
	    List<String> categories = products.stream()
	        .map(Product::getCategory)
	        .distinct()
	        .collect(Collectors.toList());

	    // Sum up product quantities for each category
	    List<Integer> quantities = categories.stream()
	        .map(category -> products.stream()
	            .filter(product -> product.getCategory().equals(category))
	            .map(Product::getProductCode)  
	            .reduce(0, Integer::sum))   // Summing quantities
	        .collect(Collectors.toList());

	    // Add data to the model
	    model.addAttribute("categories", categories);
	    model.addAttribute("quantities", quantities);

	    // Get total product quantity, cost, revenue, and expected profit
	    
	    Integer totalProductQuantity;

        
	    if (isAdmin(user)) {
	        totalProductQuantity = productService.getTotalProductQuantityByUserId(user.getId());
	    } else if (isUser(user)) {
	        totalProductQuantity = productService.getTotalProductQuantityByUserIdForUser(1); // Adjusted for createdBy
	    } else {
	        totalProductQuantity = 0;
	    }
	    model.addAttribute("totalProductQuantity", totalProductQuantity);

	    if (totalProductQuantity == 0) {
	        model.addAttribute("message", "No products available.");
	    }
	    
	    Double totalCost = productService.getTotalCost(user.getId());
	    model.addAttribute("totalCost", totalCost != null ? totalCost : 0.0);

	    Double totalRevenue = productService.getTotalRevenue(user.getId());
	    model.addAttribute("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);

	    Double expectedProfit = productService.getExpectedRevenue(user.getId());
	    model.addAttribute("expectedProfit", expectedProfit != null ? expectedProfit : 0.0);
	}

	@GetMapping("/dashboard")
	public String dashboard(HttpSession session, Model model) {
	    User user = (User) session.getAttribute("user");
	    if (isAdmin(user)) {
	        populateDashboard(model, user);
	        return "index";
	    } else if (isUser(user)) {
	        return "redirect:/user-dashboard";
	    }
	    return "redirect:/login";
	}


	/*
	@GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("username", user.getFirstName()+ " "+user.getLastName());
            model.addAttribute("pageTitle", "Admin Dashboard");
            
            //start
            List<Product> products = productRepository.findByUserId(user.getId());
            List<String> categories = products.stream()
            		.map(Product :: getCategory)
            		.distinct()
            		.collect(Collectors.toList());
            List<Integer> quantities = categories.stream()
            		.map(category -> products.stream()
            				.filter(product -> 
            				product.getCategory().equals(category))
            				.map(Product:: getProductCode)
            				.reduce(0, Integer::sum)
            				).collect(Collectors.toList());
            model.addAttribute("categories",categories);
            model.addAttribute("quantities",quantities);
            //end
            
           
            model.addAttribute("totalProductQuantity", productService.getTotalProductQuantityByUserId(user.getId()));
            model.addAttribute("totalCost", productService.getTotalCost(user.getId()));
            model.addAttribute("totalRevenue", productService.getTotalRevenue(user.getId()));
            model.addAttribute("expectedProfit", productService.getExpectedRevenue(user.getId()));
            
            return "index";  
        } else {
            return "redirect:/login";  
        }
    }*/
	
	@GetMapping("/user-dashboard")
    public String userDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("username", user.getFirstName()+ " "+user.getLastName());
            model.addAttribute("pageTitle", "User Dashboard");
            
            
            return "user-index";  
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
	public String handleLogin(@RequestParam String email, @RequestParam String password, Model model, HttpSession session) {
		
	    Optional<User> user = userService.findByEmail(email);
	    if (user.isPresent() && user.get().getPassword().equals(password) && user.get().getUserType().equals("Admin")) {
	        session.setAttribute("user", user.get()); 
	        User user1 = (User) session.getAttribute("user");
			System.out.println("Logged in User ID: " + user1.getId());
			System.out.println("Created By User ID: " + (user1.getCreatedBy() != null ? user1.getCreatedBy().getId() : "No Admin"));
	        return "redirect:/dashboard";
	    } else if (user.isPresent() && user.get().getPassword().equals(password) && user.get().getUserType().equals("User")) {
	        session.setAttribute("user", user.get()); 
	        User user1 = (User) session.getAttribute("user");
			System.out.println("Logged in User ID: " + user1.getId());
			System.out.println("Created By User ID: " + (user1.getCreatedBy() != null ? user1.getCreatedBy().getId() : "No Admin"));
	        return "redirect:/user-dashboard";
	    } else {
	        model.addAttribute("error", "Invalid username or password");
	        System.out.println("Login failed for user: " + email);
	        return "login";
	    }
	}

	/*
	@PostMapping("/login")
	public String handleLogin(@RequestParam String email,@RequestParam String password,Model model, HttpSession http) {
		
		Optional<User> user = userService.findByEmail(email);
		if(user.isPresent() && user.get().getPassword().equals(password) && user.get().getUserType().equals("Admin")) {
			http.setAttribute("user", user.get());
			return "redirect:/dashboard";
		} else if (user.isPresent() && user.get().getPassword().equals(password) && user.get().getUserType().equals("User")) {
			http.setAttribute("user", user.get());
			return "redirect:/user-dashboard";
			
		}
		
		else {
			model.addAttribute("error", "Invalid username or password");
			System.out.println("Login failed for user: " + email);
			return "login";
		}	
	}*/
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/login";
	}
	
	@GetMapping("/create-user")
	public String registerNewUser(@ModelAttribute("user") User  user){
		return "new-user";
	}
	
	@PostMapping("/create-user")
	public String handleRegistrationNewUser(@ModelAttribute("user") User user, 
	                                         Model model, 
	                                         HttpSession session, 
	                                         RedirectAttributes redirectAttributes) {
	    User currentUser = (User) session.getAttribute("user");

	    if (user.getPassword().equals(user.getConfirmPassword())) {
	        model.addAttribute("pageTitle", "Create User"); 
	        user.setCreatedBy(currentUser);  // Set who created the user (the current admin)
	        
	        // Ensure the user is created as Admin by default (if not specified)
	        if (user.getUserType() == null || user.getUserType().isEmpty()) {
	            user.setUserType("Admin");
	        }

	        userService.saveUser(user);
	        System.out.println("User Created By: " + currentUser.getEmail()); 
	        
	        redirectAttributes.addFlashAttribute("successMsg", "User created successfully!");
	    } else {
	        model.addAttribute("passwordError", "Passwords do not match");
	        return "new-user";
	    }

	    return "redirect:/create-user";  
	}

	@GetMapping("/show-users")
	public String viewCreatedUsers(HttpSession session, Model model) {
	    User currentUser = (User) session.getAttribute("user"); // Get the admin from session

	    if (currentUser != null && currentUser.getUserType().equals("Admin")) { 
	        List<User> createdUsers = userService.findUsersCreatedBy(currentUser); 
	        model.addAttribute("createdUsers", createdUsers);
	        model.addAttribute("pageTitle", "Created Users"); 
	        return "view-created-users"; 
	    } else {
	        return "redirect:/login"; 
	    }
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

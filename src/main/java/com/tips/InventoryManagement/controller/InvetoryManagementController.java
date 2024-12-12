package com.tips.InventoryManagement.controller;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tips.InventoryManagement.config.SecurityConfig;
import com.tips.InventoryManagement.models.Product;
import com.tips.InventoryManagement.models.User;
import com.tips.InventoryManagement.repository.ProductRepository;
import com.tips.InventoryManagement.service.EmailSender;
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
	private EmailSender emailSender;
	
	@Autowired
	private ProductRepository productRepository;
	

	//check if is user is Admin
	private boolean isAdmin(User user) {
	    return user != null && "Admin".equals(user.getUserType());
	}
	//check if is user is User
	private boolean isUser(User user) {
	    return user != null && "User".equals(user.getUserType());
	}
	
	private void populateDashboard(Model model, User user) {
	    // Fetch the products for the logged-in user
	    List<Product> products = productRepository.findByUserId(user.getId());
	    
	    if (products.isEmpty()) {
	        model.addAttribute("message", "No products found for the user.");
	    }
	    
	    //get product categories
	    List<String> categories = products.stream()
	        .map(Product::getCategory)
	        .distinct()
	        .collect(Collectors.toList());

	   //get product quantity
	    List<Integer> quantities = categories.stream()
	        .map(category -> products.stream()
	            .filter(product -> product.getCategory().equals(category))
	            .map(Product::getProductCode)  
	            .reduce(0, Integer::sum))   
	        .collect(Collectors.toList());

	    
	    model.addAttribute("categories", categories);
	    model.addAttribute("quantities", quantities);

	    
	    Integer totalProductQuantity;

        
	    if (isAdmin(user)) {
	        totalProductQuantity = productService.getTotalProductQuantityByUserId(user.getId());
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
	    if (user == null) {
	        return "redirect:/login";  
	    }
	    if (isAdmin(user)) {
	        populateDashboard(model, user);
	        model.addAttribute("pageTitle", "Admin Dashboard");
	        return "index";
	    }
	    return "redirect:/login";
	}


	
	
	@GetMapping("/user-dashboard")
	public String userDashboard(@RequestParam(value = "creatorId", required = false) Integer creatorId,
	         @RequestParam(defaultValue = "0") int page,
	         @RequestParam(defaultValue = "10") int size,
	         Model model, HttpSession session) {
	    
	    User user = (User) session.getAttribute("user");
	    if (user != null) {
	    	 model.addAttribute("pageTitle", "User Dashboard");
	        if (creatorId == null) {
	            creatorId = user.getCreatedBy().getId();
	        }

	   
	        
	        Integer totalProductQuantity = productService.getTotalProductQuantityByUserId(creatorId); 
	        model.addAttribute("totalProductQuantity", totalProductQuantity);
	        model.addAttribute("totalCost", productService.getTotalCost(creatorId));
	        model.addAttribute("totalRevenue", productService.getTotalRevenue(creatorId));
	        model.addAttribute("expectedProfit", productService.getExpectedRevenue(creatorId));

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
	public String handleLogin(@RequestParam String email, @RequestParam String password, Model model, HttpSession session, RedirectAttributes redirectAttributes) throws NoSuchAlgorithmException {
	    Optional<User> user = userService.findByEmail(email);
	    if (user.isPresent()) {
	        String storedHashedPassword = user.get().getPassword();
	        if (SecurityConfig.verifyPassword(password, storedHashedPassword)) {
	            // Login successful
	            session.setAttribute("user", user.get());
	            User user1 = (User) session.getAttribute("user");
	            // Store creatorId
	            Integer creatorId = (user1.getCreatedBy() != null) ? user1.getCreatedBy().getId() : null;
	            model.addAttribute("creatorId", creatorId);
	            redirectAttributes.addFlashAttribute("successMessage", "Log in successful");
	            if (user.get().getUserType().equals("Admin")) {
	                return "redirect:/dashboard";
	            } else {
	                return "redirect:/user-dashboard";
	            }
	        } else {
	            model.addAttribute("error", "Invalid username or password");
	            System.out.println("Login failed for user: " + email);
	            return "login";
	        }
	    } else {
	        model.addAttribute("error", "Invalid username or password");
	        System.out.println("Login failed for user: " + email);
	        return "login";
	    }
	}
	/*
	@PostMapping("/login")
	public String handleLogin(@RequestParam String email, @RequestParam String password, Model model, HttpSession session,RedirectAttributes redirectAttributes) throws NoSuchAlgorithmException {
	    Optional<User> user = userService.findByEmail(email);
	    String hashedPassword = SecurityConfig.hashPassword(password);
	    if (user.isPresent() && user.get().getPassword().equals(hashedPassword) && user.get().getUserType().equals("Admin")) {
	        session.setAttribute("user", user.get());
	        User user1 = (User) session.getAttribute("user");
	       
	        // Store creatorId 
	        Integer creatorId = (user1.getCreatedBy() != null) ? user1.getCreatedBy().getId() : null;
	        session.setAttribute("creatorId", creatorId);
	        redirectAttributes.addAttribute("successMessage","Log in successfull");
	        return "redirect:/dashboard";
	    } else if (user.isPresent() && user.get().getPassword().equals(hashedPassword) && user.get().getUserType().equals("User")) {
	        session.setAttribute("user", user.get());
	        User user1 = (User) session.getAttribute("user");
	        
	        // Store creatorId 
	        Integer creatorId = (user1.getCreatedBy() != null) ? user1.getCreatedBy().getId() : null;
	        session.setAttribute("creatorId", creatorId);
	        redirectAttributes.addAttribute("successMessage","Log in successfull");
	        return "redirect:/user-dashboard";
	    } else {
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
	public String registerNewUser(@ModelAttribute("user") User  user,Model model){
		model.addAttribute("pageTitle", "Create User"); 
		return "new-user";
	}
	
	@PostMapping("/create-user")
	public String handleRegistrationNewUser(@ModelAttribute("user") User user, 
	                                         Model model, 
	                                         HttpSession session, 
	                                         RedirectAttributes redirectAttributes) throws NoSuchAlgorithmException {
	    User currentUser = (User) session.getAttribute("user");

	    if (user.getPassword().equals(user.getConfirmPassword())) {
	    	//String encodedPassword = 
	        model.addAttribute("pageTitle", "Create User"); 
	        user.setCreatedBy(currentUser);  
	        
	        // Ensure the user is created as Admin by default
	        if (user.getUserType() == null || user.getUserType().isEmpty()) {
	            user.setUserType("Admin");
	        }
	        String hashedPassword = SecurityConfig.hashPassword(user.getPassword());
			user.setPassword(hashedPassword);
			String toEmail = user.getEmail();
	        String subject = "Welcome to Inventory Management System";
	        String body = String.format("Dear %s,\n\nWelcome to our system! Here are your login credentials:\n\nEmail: %s\nPassword: %s\n\nPlease keep this information safe.\n\nBest regards,\nInventory Management Team",
	                user.getFirstName(), user.getEmail(), user.getConfirmPassword()); 

	        emailSender.sendEmail(toEmail, subject, body);

	        
	        
	        userService.saveUser(user);
	       
	        
	        redirectAttributes.addFlashAttribute("successMsg", "User created successfully!");
	    } else {
	        model.addAttribute("passwordError", "Passwords do not match");
	        return "new-user";
	    }

	    return "redirect:/create-user";  
	}

	@GetMapping("/show-users")
	public String viewCreatedUsers(HttpSession session, Model model,RedirectAttributes redirectAttributes) {
	    User currentUser = (User) session.getAttribute("user"); // Get the admin from session

	    if (currentUser != null && currentUser.getUserType().equals("Admin")) { 
	        List<User> createdUsers = userService.findUsersCreatedBy(currentUser); 
	        model.addAttribute("createdUsers", createdUsers);
	        model.addAttribute("pageTitle", "Created Users"); 
	        if (createdUsers.isEmpty()) {
	        	redirectAttributes.addFlashAttribute("errorMsg","You have not yet created users");
	        }
	        return "view-created-users"; 
	    } else {
	        return "redirect:/login"; 
	    }
	}
	@GetMapping("/users/edit")
	public String showEditProductForm(@RequestParam("id") Integer id, Model model, HttpSession session) {
	    User user = (User) session.getAttribute("user");
	    if (user != null) {
	        Optional<User> userOpt = userService.findUserById(id);
	        if (userOpt.isPresent()) {
	            User user1 = userOpt.get();
	            
	            
	            model.addAttribute("user", user1);
	            model.addAttribute("usertype", List.of("Admin", "User", "Cashier", "Stock Manager")); 
	            model.addAttribute("username", user1.getEmail());
	            model.addAttribute("pageTitle", "Edit Users");
	            
	            return "edit-users";  
	        } else {
	            model.addAttribute("errorMsg", "User not found.");
	            return "redirect:/show-users";
	        }
	    } else {
	        return "redirect:/login";
	    }
	}
	@PostMapping("/user/edit")
	public String updateUser(Model model,HttpSession session,RedirectAttributes redirectAttributes,@ModelAttribute("user") User user) throws NoSuchAlgorithmException {


	    if (user.getPassword().equals(user.getConfirmPassword())) {
	    	
	        
	        // Ensure the user is created as Admin by default
	        if (user.getUserType() == null || user.getUserType().isEmpty()) {
	            user.setUserType("Admin");
	        }
	        String hashedPassword = SecurityConfig.hashPassword(user.getPassword());
			user.setPassword(hashedPassword);
			String toEmail = user.getEmail();
	        String subject = "Welcome to Inventory Management System";
	        String body = String.format("Dear %s,\n\nHere are your new login credentials:\n\nEmail: %s\nPassword: %s\n\nPlease keep this information safe.\n\nBest regards,\nInventory Management Team",
	                user.getFirstName(), user.getEmail(), user.getConfirmPassword()); 

	        emailSender.sendEmail(toEmail, subject, body);

	        
	        
	        userService.saveUser(user);
	       
	        
	        redirectAttributes.addFlashAttribute("successMsg", "User updated successfully!");
	    } else {
	        model.addAttribute("passwordError", "Passwords do not match");
	        return "edit-users";
	    }

	   
		return "redirect:/show-users";
	}

	

	
	@GetMapping("/register")
	public String register(@ModelAttribute("user") User  user){
		return "register";
	}
	
	
	
	/*
	@PostMapping("/register")
	public String handleRegistration(@ModelAttribute("user") User  user,Model model,RedirectAttributes redirectAttributes) throws NoSuchAlgorithmException {
		if(user.getPassword().equals(user.getConfirmPassword())) {
			String hashedPassword = SecurityConfig.hashPassword(user.getPassword());
			user.setPassword(hashedPassword);
			
			userService.saveUser(user);
		} else {
			model.addAttribute("passwordError", "Passwords do not match");
			return "register";
		}
		redirectAttributes.addFlashAttribute("successMessage","Registration successfull. Redirecting to login");
		return "redirect:/login";
	}*/
	
	@PostMapping("/register")
	public String handleRegistration(
	        @ModelAttribute("user") User user,
	        Model model,
	        RedirectAttributes redirectAttributes) throws NoSuchAlgorithmException {

	    if (user.getPassword().equals(user.getConfirmPassword())) {
	        // Hash the password and set it
	        String hashedPassword = SecurityConfig.hashPassword(user.getPassword());
	        user.setPassword(hashedPassword);

	        // Save the user
	        userService.saveUser(user);

	        // Send a welcome email
	        String toEmail = user.getEmail();
	        String subject = "Welcome to Inventory Management System";
	        String body = String.format("Dear %s,\n\nWelcome to our system! Here are your login credentials:\n\nEmail: %s\nPassword: %s\n\nPlease keep this information safe.\n\nBest regards,\nInventory Management Team",
	                user.getFirstName(), user.getEmail(), user.getConfirmPassword()); 

	        emailSender.sendEmail(toEmail, subject, body);

	        redirectAttributes.addFlashAttribute("successMessage", "Registration successful. A welcome email has been sent.");
	        return "redirect:/login";
	    } else {
	        model.addAttribute("passwordError", "Passwords do not match");
	        return "register";
	    }
	}

}

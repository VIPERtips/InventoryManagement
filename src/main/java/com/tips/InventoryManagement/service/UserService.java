package com.tips.InventoryManagement.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tips.InventoryManagement.models.User;
import com.tips.InventoryManagement.repository.UserRepository;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;
	//saving the user in the db
	public void saveUser (User user) {
		userRepository.save(user);
	}
	//querying users by their unique email
	public Optional<User> findByEmail(String email) {
		// TODO Auto-generated method stub
		return userRepository.findByEmail(email);
	}
	//queryUsers created by an admin
	public List<User> findUsersCreatedBy(User createdBy) {
        return userRepository.findByCreatedBy(createdBy.getId()); 
    }
	//get the quantity for the user
	public int getTotalProductQuantityForUser(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return user.getTotalQuantity();
    }
}

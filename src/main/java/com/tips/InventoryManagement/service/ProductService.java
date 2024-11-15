package com.tips.InventoryManagement.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tips.InventoryManagement.models.Product;
import com.tips.InventoryManagement.repository.ProductRepository;

@Service
public class ProductService {
	@Autowired
	private ProductRepository productRepository;
	
	public void saveProduct (Product product) {
		productRepository.save(product);
	}
	public List<Product> findAllProducts() {
		return productRepository.findAllByOrderByIdDesc();
    }
	public void deleteProductById(int id) {
	    
	    Optional<Product> product = productRepository.findById(id);
	    if (product.isPresent()) {
	        productRepository.deleteById(id);  
	    } else {
	        throw new RuntimeException("Product not found with id: " + id);
	    }
	}
	public Optional<Product> findProductById(int id) {
	    return productRepository.findById(id);  
	}

}

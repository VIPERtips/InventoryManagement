package com.tips.InventoryManagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tips.InventoryManagement.models.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
	 List<Product> findAllByOrderByIdDesc();
}

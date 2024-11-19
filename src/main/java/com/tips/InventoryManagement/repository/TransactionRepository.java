package com.tips.InventoryManagement.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tips.InventoryManagement.models.Sale;
import com.tips.InventoryManagement.models.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

	Page<Transaction> findByUserId(int id, Pageable pageable);
	
	

}

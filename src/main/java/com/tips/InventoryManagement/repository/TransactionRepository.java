package com.tips.InventoryManagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tips.InventoryManagement.models.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

	Page<Transaction> findByUserId(int id, Pageable pageable);

}

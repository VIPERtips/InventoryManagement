package com.tips.InventoryManagement.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.tips.InventoryManagement.models.Product;
import com.tips.InventoryManagement.models.Sale;
import com.tips.InventoryManagement.models.Transaction;
import com.tips.InventoryManagement.repository.TransactionRepository;

@Service
public class TransactionService {
		@Autowired
		private TransactionRepository transactionRepository;
		
		public void saveTransaction(Transaction transaction) {
			transactionRepository.save(transaction);
		}

		public Page<Transaction> getTransactionsByUserId(int id, int page, int size) {
			 Pageable pageable = PageRequest.of(page, size,Sort.by(Sort.Order.desc("id")));
		        return transactionRepository.findByUserId(id,pageable);
		}	
		
		public Page<Transaction> getAllTransactions(int page, int size) {
			 Pageable pageable = PageRequest.of(page, size,Sort.by(Sort.Order.desc("id")));
		        return transactionRepository.findAll(pageable);
		} 
		public void deleteProductById(int id) {
		    
		    Optional<Transaction> transaction = transactionRepository.findById(id);
		    if (transaction.isPresent()) {
		        transactionRepository.deleteById(id);  
		    } else {
		        throw new RuntimeException("Transaction not found with id: " + id);
		    }
		}
}

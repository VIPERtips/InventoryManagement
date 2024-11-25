
package com.tips.InventoryManagement.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tips.InventoryManagement.models.Sale;


public interface SaleRepository extends JpaRepository<Sale, Integer> {
	@Query("select s from Sale s where s.user.id = :id")
	Page<Sale> findByUserId(int id, Pageable pageable);
	@Query("select s from Sale s where s.id = :id")
	Sale findById(@Param("id") int id);
	Page<Sale> findAll(Pageable pageable);
	
	 
}
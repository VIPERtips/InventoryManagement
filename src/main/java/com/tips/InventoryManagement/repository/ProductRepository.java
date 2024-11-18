
package com.tips.InventoryManagement.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.tips.InventoryManagement.models.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
	 Page<Product> findAll(Pageable pageable);
	 List<Product> findAllByOrderByIdDesc();
	 boolean existsByBarcodeAndUserId(String barcode,int userid);
	 
	 @Query("select sum(p.productCode) from Product p") 
	    Long getProductQuantity();
	Page<Product> findByUserId(int userid, Pageable pageable);
	List<Product> findByUserId(int userid);
	
	@Query("select sum(p.productCode) from Product p where p.user.id = ?1") 
	Long countByUserId(int userid);
	Product findByProductNameAndBarcode(String productName, String barcode);
	
	 
}
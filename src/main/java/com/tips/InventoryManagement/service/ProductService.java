package com.tips.InventoryManagement.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.tips.InventoryManagement.models.Product;
import com.tips.InventoryManagement.models.ProductDto;
import com.tips.InventoryManagement.repository.ProductRepository;

@Service
public class ProductService {
	@Autowired
	private ProductRepository productRepository;
	//checkDuplicate
	public boolean isBarcodeDuplicate(Product product)
	{
		return productRepository.existsByBarcodeAndUserId(product.getBarcode(), product.getUser().getId());
	}
	
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
	//start
	public Long getTotalProductQuantityByUserId(int userid) {
		return productRepository.countByUserId(userid);
	}

    
	public long getTotalProductQuantity() {
        Long totalQuantity = productRepository.getProductQuantity();
        return totalQuantity != null ? totalQuantity : 0;
    }
    
    public double getTotalCost(int userid) {
        List<Product> products = productRepository.findByUserId(userid);
        double totalCost = 0;
        for (Product product : products) {
        	totalCost += product.getUnitCost() *product.getProductCode();  
        }
        return BigDecimal.valueOf(totalCost).setScale(2,RoundingMode.HALF_UP).doubleValue();
    }
    public double getTotalRevenue(int userid) {
        List<Product> products = productRepository.findByUserId(userid);
        double totalRevenue = 0;
        for (Product product : products) {
        	totalRevenue += product.getCostPrice() *product.getProductCode();  
        }
        return  BigDecimal.valueOf(totalRevenue).setScale(2,RoundingMode.HALF_UP).doubleValue();
    }
    public double getExpectedRevenue(int userid) {
        
        double expectedRevenue =  getTotalRevenue(userid) - getTotalCost(userid);
        
        return  BigDecimal.valueOf(expectedRevenue).setScale(2,RoundingMode.HALF_UP).doubleValue();
    }
    public Page<Product> getPaginatedProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size,Sort.by(Sort.Order.desc("id")));
        return productRepository.findAll(pageable);
    }
    public Page<Product> getProductsByUserId(int userid,int page, int size) {
        Pageable pageable = PageRequest.of(page, size,Sort.by(Sort.Order.desc("id")));
        return productRepository.findByUserId(userid,pageable);
    }

	public Product findProductByProductNameAndBarcode(String productName, String barcode) {
		
		return productRepository.findByProductNameAndBarcode(productName,barcode);
		
	}
	
	public List<Product> getProductsByUserId(int id){
		return productRepository.findByUserId(id);
	}
	

}
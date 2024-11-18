
package com.tips.InventoryManagement.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.tips.InventoryManagement.models.Sale;
import com.tips.InventoryManagement.repository.SaleRepository;

@Service
public class SaleService {
	@Autowired
	private SaleRepository saleRepository;
	
	public void saveSale(Sale sale) {
		saleRepository.save(sale);
	}

	public Page<Sale> getSalesByUserId(int id, int page, int size) {
		Pageable pageable = PageRequest.of(page, size,Sort.by(Sort.Order.desc("id")));
        return saleRepository.findByUserId(id,pageable);
	}

	public Sale getSaleById(int id) {
		
		return saleRepository.findById(id) ;
	}

	public Optional<Sale> findSaleById(Integer id) {
		// TODO Auto-generated method stub
		return saleRepository.findById(id);
	}

	public void deleteSaleById(Integer id) {
		Optional<Sale> sale = saleRepository.findById(id);
		if(sale.isPresent()) {
			saleRepository.deleteById(id);
		}else {
			throw new RuntimeException("Sale not found with id "+id);
		}
		
	}
}
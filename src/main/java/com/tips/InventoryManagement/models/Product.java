package com.tips.InventoryManagement.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;


@Entity
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String productName;
	@Column(columnDefinition = "TEXT",name = "brand")
	private String productDescription;
	@Column(name = "quantity")
	private Integer productCode;
	private String imageInput;
	@Column(nullable = false, unique = true)
	private String barcode;
	private String category;
	private double unitCost;
	private double costPrice;
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy; 
	
    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Product() {
		// TODO Auto-generated constructor stub
	}
	
	public Product( String productName,
			String productDescription,
			 Integer productCode, String imageInput,
			String barcode, String category, double unitCost,
			double costPrice) {
		this.productName = productName;
		this.productDescription = productDescription;
		this.productCode = productCode;
		this.imageInput = imageInput;
		this.barcode = barcode;
		this.category = category;
		this.unitCost = unitCost;
		this.costPrice = costPrice;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductDescription() {
		return productDescription;
	}
	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}
	public Integer getProductCode() {
		return productCode;
	}
	public void setProductCode(Integer productCode) {
		this.productCode = productCode;
	}
	public String getImageInput() {
		return imageInput;
	}
	public void setImageInput(String imageInput) {
		this.imageInput = imageInput;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public double getUnitCost() {
		return unitCost;
	}
	public void setUnitCost(double unitCost) {
		this.unitCost = unitCost;
	}
	public double getCostPrice() {
		return costPrice;
	}
	public void setCostPrice(double costPrice) {
		this.costPrice = costPrice;
	}

	
}

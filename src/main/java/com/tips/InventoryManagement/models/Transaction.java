package com.tips.InventoryManagement.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Transaction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String productName;
	private String brand;
	private double totalAmount;
	private String paymentMethod;
	private String receiverNumber;
	private String transactionDate;
	private Integer quantity;
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Transaction() {
		// TODO Auto-generated constructor stub
	}

	public Transaction(String productName, String brand, double totalAmount, String paymentMethod,
			String receiverNumber, String transactionDate, Integer quantity) {
		this.productName = productName;
		this.brand = brand;
		this.totalAmount = totalAmount;
		this.paymentMethod = paymentMethod;
		this.receiverNumber = receiverNumber;
		this.transactionDate = transactionDate;
		this.quantity = quantity;
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

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getReceiverNumber() {
		return receiverNumber;
	}

	public void setReceiverNumber(String receiverNumber) {
		this.receiverNumber = receiverNumber;
	}

	public String getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	
}

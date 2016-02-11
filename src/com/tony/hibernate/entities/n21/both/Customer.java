package com.tony.hibernate.entities.n21.both;

import java.util.HashSet;
import java.util.Set;

import com.tony.hibernate.entities.n21.both.Order;

public class Customer {
	private Integer customerId;
	private String customerName;
	/*
	 * 1. When declare a collection type, it needs to use interface type for Hibernate return its built-in
	 * 	  collection type instead of JaveSE's stander collection.
	 * 2. Must be initialing the collection field for prevent NullPoint Exception
	 * 
	 * 
	 */
	private Set<Order> orders = new HashSet<>();

	public Set<Order> getOrders() {
		return orders;
	}

	public void setOrders(Set<Order> orders) {
		this.orders = orders;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

}

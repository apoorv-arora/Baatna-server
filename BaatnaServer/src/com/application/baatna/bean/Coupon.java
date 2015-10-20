package com.application.baatna.bean;

import java.io.Serializable;

public class Coupon implements Serializable{

	private int id;
	private String validity;
	private String name;
	private String terms;
	private int count;
	private String image;
	
//	
//	//Limit for the number of coupons
//	private int limit;
	
	public Coupon() {
		
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getValidity() {
		return validity;
	}

	public void setValidity(String validity) {
		this.validity = validity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTerms() {
		return terms;
	}

	public void setTerms(String terms) {
		this.terms = terms;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

//	public int getLimit() {
//		return limit;
//	}
//
//	public void setLimit(int limit) {
//		this.limit = limit;
//	}
	
	
	
}

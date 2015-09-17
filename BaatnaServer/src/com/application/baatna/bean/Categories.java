package com.application.baatna.bean;

import java.io.Serializable;
import java.util.List;

public class Categories implements Serializable {
	
	private int categoryId;
	private String category;
	private String categoryIcon;
//	private List categoryItems;
	
	public Categories() {
		
	}
	
	public Categories(int categoryId, String category, String categoryIcon){
		this.categoryId = categoryId;
		this.category = category;
		this.categoryIcon = categoryIcon;
//		this.categoryItems = categoryItems;
	}
	
	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCategoryIcon() {
		return categoryIcon;
	}

	public void setCategoryIcon(String categoryIcon) {
		this.categoryIcon = categoryIcon;
	}

//	public List<String> getCategoryItems() {
//		return categoryItems;
//	}
//
//	public void setCategoryItems(List categoryItems) {
//		this.categoryItems = categoryItems;
//	}

}

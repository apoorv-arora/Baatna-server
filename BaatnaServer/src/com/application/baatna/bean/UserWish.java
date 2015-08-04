package com.application.baatna.bean;

import java.io.Serializable;

public class UserWish implements Serializable {
//	private long id;
	private int wishId;
	private int userId;

	public UserWish() {
	}
	

//	public long getId() {
//		return id;
//	}
//
//
//	public void setId(long id) {
//		this.id = id;
//	}


	public int getWishId() {
		return wishId;
	}

	public void setWishId(int wishId) {
		this.wishId = wishId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
	
}

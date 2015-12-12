package com.application.baatna.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserWish implements Serializable {
	private int serial;
	private int wishId;
	private int userId; // wish posting user
	private int wishStatus;
	private int userTwoId; // accepting or declining user
//	private String couponId;

	public int getSerial() {
		return serial;
	}

	public void setSerial(int serial) {
		this.serial = serial;
	}

	public int getWishStatus() {
		return wishStatus;
	}

	public void setWishStatus(int wishStatus) {
		this.wishStatus = wishStatus;
	}

	public int getUserTwoId() {
		return userTwoId;
	}

	public void setUserTwoId(int userTwoId) {
		this.userTwoId = userTwoId;
	}

	public UserWish() {
	}

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
	
	
//
//	public String getCouponId() {
//		return couponId;
//	}
//
//	public void setCouponId(String couponId) {
//		this.couponId = couponId;
//	}
}

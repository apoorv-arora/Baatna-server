package com.application.baatna.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserWish implements Serializable {
	private int serial;
	private int wishId;
	private int userId; // wish posting user
	private int wishStatus;
	private int userTwoId;
	// accepting or declining user
//	private String couponId;
	private int delivery_opt;
	private long time_post;
	private long return_deadline;
	
	//1 for self pickup and 2 for baatna delivery
	
	public int getSerial() {
		return serial;
	}

	public int getDelivery_opt() {
		return delivery_opt;
	}

	public void setDelivery_opt(int delivery_opt) {
		this.delivery_opt = delivery_opt;
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
	

	public long getTime_post() {
		return time_post;
	}

	public void setTime_post(long time_post) {
		this.time_post = time_post;
	}

	public long getReturn_deadline() {
		return return_deadline;
	}

	public void setReturn_deadline(long return_deadline) {
		this.return_deadline = return_deadline;
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

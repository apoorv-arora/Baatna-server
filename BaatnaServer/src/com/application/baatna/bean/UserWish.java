package com.application.baatna.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserWish implements Serializable {
	// private long id;
	private int wishId;
	private int userId;
//	private int couponId;
//	private List<Coupon> coupons = new ArrayList<Coupon>();

	public UserWish() {
	}

	public int getWishId() {
		return wishId;
	}

	public void setWishId(int wishId) {
		this.wishId = wishId;
	}

//	public int getCouponId() {
//		return couponId;
//	}
//
//	public void setCouponId(int couponId) {
//		this.couponId = couponId;
//	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

//	public List<Coupon> getCoupons() {
//		return coupons;
//	}
//
//	public void setCoupons(List<Coupon> coupons) {
//		this.coupons = coupons;
//	}

}

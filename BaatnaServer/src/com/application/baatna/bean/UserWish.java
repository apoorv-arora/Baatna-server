package com.application.baatna.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserWish implements Serializable {
	private int wishId;
	private int userId;
	private int couponId = 0;

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

	public int getCouponId() {
		return couponId;
	}

	public void setCouponId(int couponId) {
		this.couponId = couponId;
	}
}

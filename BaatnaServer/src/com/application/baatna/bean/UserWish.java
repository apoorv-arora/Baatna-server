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
	private boolean negotiationStatus;
	private int negotiationAmount;
	private double u1ratedu2;
	private double u2ratedu1;
//	private String couponId;

	

	public int getSerial() {
		return serial;
	}

	public double getU1ratedu2() {
		return u1ratedu2;
	}

	public void setU1ratedu2(double u1ratedu2) {
		this.u1ratedu2 = u1ratedu2;
	}

	public double getU2ratedu1() {
		return u2ratedu1;
	}

	public void setU2ratedu1(double u2ratedu1) {
		this.u2ratedu1 = u2ratedu1;
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

	public boolean isNegotiationStatus() {
		return negotiationStatus;
	}

	public void setNegotiationStatus(boolean negotiationStatus) {
		this.negotiationStatus = negotiationStatus;
	}

	public int getNegotiationAmount() {
		return negotiationAmount;
	}

	public void setNegotiationAmount(int negotiationAmount) {
		this.negotiationAmount = negotiationAmount;
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

package com.application.baatna.bean;

import java.io.Serializable;

public class UserRating implements Serializable
{
	private int serial;
	private int userIdOne;
	private int userIdTwo;
	private double rating;
	
	
	public int getUserIdOne() {
		return userIdOne;
	}
	public void setUserIdOne(int userIdOne) {
		this.userIdOne = userIdOne;
	}
	public int getUserIdTwo() {
		return userIdTwo;
	}
	public void setUserIdTwo(int userIdTwo) {
		this.userIdTwo = userIdTwo;
	}
	public double getRating() {
		return rating;
	}
	public void setRating(double rating) {
		this.rating = rating;
	}
	public int getSerial() {
		return serial;
	}
	public void setSerial(int serial) {
		this.serial = serial;
	}
}
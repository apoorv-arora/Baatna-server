package com.application.baatna.bean;

import java.io.Serializable;

/**
 * User Rating table. Stores the user rating values.
 */
public class UserRating implements Serializable {
	private int serial;
	private int reviewer; // One who has rated
	private int reviewed; // One who has been rated
	private double rating;

	public UserRating() {
	}

	public int getSerial() {
		return serial;
	}

	public void setSerial(int serial) {
		this.serial = serial;
	}

	public int getReviewer() {
		return reviewer;
	}

	public void setReviewer(int reviewer) {
		this.reviewer = reviewer;
	}

	public int getReviewed() {
		return reviewed;
	}

	public void setReviewed(int reviewed) {
		this.reviewed = reviewed;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

}
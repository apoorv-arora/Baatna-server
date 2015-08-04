package com.application.baatna.bean;

public class Location {

	private double latitude, longitude;

	public Location(double latitude, double longitude) {

		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Location() {
	}

	public double getLatitude() {

		return latitude;
	}

	public void setLatitude(double latitude) {

		this.latitude = latitude;
	}

	public double getLongitude() {

		return longitude;
	}

	public void setLongitude(double longitude) {

		this.longitude = longitude;
	}
}

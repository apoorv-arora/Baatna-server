package com.application.baatna.bean;

public class NewsFeed {

	private int feedId;
	/**
	 * Type 0: User joined near you
	 * Type 1: User X posted a new request of wish item Y
	 * Type 2: User X gave Y to user Z
	 * */
	private int type; 
	/**
	 * Time when the item is posted
	 * */
	private long timestamp;
	/**
	 * User X
	 * */
	private int userIdFirst;
	/**
	 * User Z
	 * */
	private int userIdSecond;
	/**
	 * Wish Y
	 * */
	private int wishId;
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public int getUserIdFirst() {
		return userIdFirst;
	}
	public void setUserIdFirst(int userIdFirst) {
		this.userIdFirst = userIdFirst;
	}
	public int getUserIdSecond() {
		return userIdSecond;
	}
	public void setUserIdSecond(int userIdSecond) {
		this.userIdSecond = userIdSecond;
	}
	public int getWishId() {
		return wishId;
	}
	public void setWishId(int wishId) {
		this.wishId = wishId;
	}
	public int getFeedId() {
		return feedId;
	}
	public void setFeedId(int feedId) {
		this.feedId = feedId;
	}
}

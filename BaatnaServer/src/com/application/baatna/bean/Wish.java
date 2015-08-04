package com.application.baatna.bean;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

public class Wish implements Serializable {

	private String title;
	private String description;
	private long timeOfPost;
	private int wishId;
	private int userId;
	private Set<User> acceptedUsers = new HashSet<User>();
	private Set<User> declinedUsers = new HashSet<User>();

	public Wish() {
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getTimeOfPost() {
		return timeOfPost;
	}

	public void setTimeOfPost(long timeOfPost) {
		this.timeOfPost = timeOfPost;
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

	public Set<User> getAcceptedUsers() {
		return acceptedUsers;
	}

	public void setAcceptedUsers(Set<User> acceptedUsers) {
		this.acceptedUsers = acceptedUsers;
	}

	public Set<User> getDeclinedUsers() {
		return declinedUsers;
	}

	public void setDeclinedUsers(Set<User> declinedUsers) {
		this.declinedUsers = declinedUsers;
	}

}

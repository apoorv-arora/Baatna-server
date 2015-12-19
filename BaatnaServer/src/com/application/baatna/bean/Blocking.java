package com.application.baatna.bean;

import java.io.Serializable;

public class Blocking implements Serializable{
	
	private int serial;
	private int blockingUserId;
	private int blockedUserId;

	public int getSerial() {
		return serial;
	}
	public void setSerial(int serial) {
		this.serial = serial;
	}
	public int getBlockingUserId() {
		return blockingUserId;
	}
	public void setBlockingUserId(int blockingUserId) {
		this.blockingUserId = blockingUserId;
	}
	public int getBlockedUserId() {
		return blockedUserId;
	}
	public void setBlockedUserId(int blockedUserId) {
		this.blockedUserId = blockedUserId;
	}
	
	
}
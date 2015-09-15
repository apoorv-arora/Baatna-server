package com.application.baatna.bean;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Message implements Serializable {

	private int messageId;
	private String message;
	private boolean status;
	private String timeOfMessage;
	private int fromUserId;
	private int toUserId;
	private int wishId;

	public Message() {
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getTimeOfMessage() {
		return timeOfMessage;
	}

	public void setTimeOfMessage(String timeOfMessage) {
		this.timeOfMessage = timeOfMessage;
	}

	public int getFromUserId() {
		return fromUserId;
	}

	public void setFromUserId(int fromUserId) {
		this.fromUserId = fromUserId;
	}

	public int getToUserId() {
		return toUserId;
	}

	public void setToUserId(int toUserId) {
		this.toUserId = toUserId;
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public int getWishId() {
		return wishId;
	}

	public void setWishId(int wishId) {
		this.wishId = wishId;
	}

}

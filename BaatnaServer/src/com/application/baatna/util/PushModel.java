package com.application.baatna.util;

import org.codehaus.jettison.json.JSONObject;

public class PushModel {

	private String pushId;
	private JSONObject notification;

	public String getPushId() {
		return pushId;
	}

	public void setPushId(String pushId) {
		this.pushId = pushId;
	}

	public JSONObject getNotification() {
		return notification;
	}

	public void setNotification(JSONObject notification) {
		this.notification = notification;
	}

}
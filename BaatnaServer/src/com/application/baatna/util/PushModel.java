package com.application.baatna.util;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class PushModel {
	
	private int to;
	private JSONObject notification;
	
	public int getTo() {
		return to;
	}
	public void setTo(int to) {
		this.to = to;
	}
	public JSONObject getNotification() {
		return notification;
	}
	public void setNotification(JSONObject notification) {
		this.notification = notification;
	}

	
}
package com.application.baatna.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jivesoftware.smack.XMPPException;

/**
 * Singleton class for sending push notifications.
 */

public class PushUtil {

	private static volatile PushUtil sInstance;
	private static ExecutorService executorService;

	/**
	 * Empty constructor to prevent multiple objects in memory
	 */
	private PushUtil() {
	}

	/**
	 * Implementation of double check'd locking scheme.
	 */
	public static PushUtil getInstance() {

		if (sInstance == null) {
			synchronized (PushUtil.class) {
				if (sInstance == null) {
					sInstance = new PushUtil();
					executorService = Executors.newFixedThreadPool(10);
				}
			}
		}
		return sInstance;
	}

	public void sendPush(PushModel pushModel, String type) {

		Runnable runnable = new Runnable() {
			public void run() {

				GCM ccsClient = new GCM();
				String userName = CommonLib.projectId + "@gcm.googleapis.com";
				String password = CommonLib.apiKey;
				try {
					ccsClient.connect(userName, password);
				} catch (XMPPException e) {
					e.printStackTrace();
				}
				String messageId = ccsClient.getRandomMessageId();

				Map<String, String> payload = new HashMap<String, String>();
				payload.put("command", "something");
				payload.put("Notification", String.valueOf(pushModel.getNotification()));
				payload.put("type", type);

				JSONObject object = new JSONObject();
				try {
					object.put("Notification", pushModel.getNotification());
					object.put("actionId", "id");
					object.put("additionalParam", "value");
				} catch (JSONException exp) {
					exp.printStackTrace();
				}
				payload.put("value", object.toString());
				payload.put("EmbeddedMessageId", messageId);
				Long timeToLive = 10000L;
				Boolean delayWhileIdle = false;

				// for (com.application.baatna.bean.Session user : users) {
				// send push notif to all
				ccsClient.send(GCM.createJsonMessage(pushModel.getPushId(), messageId, payload, null, timeToLive,
						delayWhileIdle));
				// }
				ccsClient.disconnect();
			}
		};
		executorService.submit(runnable);
	}

}
package com.application.baatna.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.application.baatna.bean.AllMessages;
import com.application.baatna.bean.Location;
import com.application.baatna.bean.Message;
import com.application.baatna.bean.NewsFeed;
import com.application.baatna.bean.Session;
import com.application.baatna.bean.User;
import com.application.baatna.bean.UserCompactMessage;
import com.application.baatna.bean.Wish;
import com.application.baatna.dao.FeedDAO;
import com.application.baatna.dao.MessageDAO;
import com.application.baatna.dao.UserDAO;
import com.application.baatna.dao.WishDAO;
import com.application.baatna.util.CommonLib;
import com.application.baatna.util.JsonUtil;

@Path("/messaging")
public class Messaging {

	final String GOOGLE_SERVER_KEY = "google";

	/**
	 * Create a new wish
	 */
	@Path("/post")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public JSONObject postWish(@FormParam("client_id") String clientId, @FormParam("app_type") String appType,
			@FormParam("userId") String toUserId, @FormParam("wishId") String wishId,
			@FormParam("message") String message, @FormParam("access_token") String accessToken) {

		// null checks, invalid request
		if (clientId == null || appType == null)
			return CommonLib.getResponseString("Invalid params", "", CommonLib.RESPONSE_INVALID_PARAMS);

		// check for client_id
		if (!clientId.equals(CommonLib.ANDROID_CLIENT_ID))
			return CommonLib.getResponseString("Invalid client id", "", CommonLib.RESPONSE_INVALID_CLIENT_ID);

		// check for app type
		if (!appType.equals(CommonLib.ANDROID_APP_TYPE))
			return CommonLib.getResponseString("Invalid params", "", CommonLib.RESPONSE_INVALID_APP_TYPE);

		UserDAO userDao = new UserDAO();
		WishDAO wishDao = new WishDAO();
		// access token validity
		int userId = userDao.userActive(accessToken);

		if (userId > 0) {
			MessageDAO messageDao = new MessageDAO();

			Message messageObj = messageDao.addMessage(message, false, "" + System.currentTimeMillis(), userId,
					Integer.parseInt(toUserId), Integer.parseInt(wishId));

			if (messageObj != null) {

				User fromUser = userDao.getUserDetails(messageObj.getFromUserId());
				final User toUser = userDao.getUserDetails(messageObj.getToUserId());
				Wish wish = wishDao.getWish(messageObj.getWishId());
				final JSONObject messageJson = new JSONObject();
				try {
					messageJson.put("to_user", JsonUtil.getUserJsonWithoutBio(toUser));
					messageJson.put("from_user", JsonUtil.getUserJsonWithoutBio(fromUser));
					messageJson.put("wish", JsonUtil.getWishJson(wish));
					messageJson.put("message_id", messageObj.getMessageId());
					messageJson.put("from_to", false);
					messageJson.put("message", messageObj.getMessage());
				} catch (JSONException e) {
					e.printStackTrace();
				}

				Runnable runnable = new Runnable() {
					public void run() {
						messageDao.sendPushToNearbyUsers(messageJson, toUser.getUserId());						
					}
				};
				Thread thread = new Thread(runnable);
				thread.start();

				JSONObject messageJsonCustom = new JSONObject();
				try {
					messageJsonCustom.put("to_user", JsonUtil.getUserJsonWithoutBio(toUser));
					messageJsonCustom.put("from_user", JsonUtil.getUserJsonWithoutBio(fromUser));
					messageJsonCustom.put("wish", JsonUtil.getWishJson(wish));
					messageJsonCustom.put("message_id", messageObj.getMessageId());
					messageJsonCustom.put("from_to", true);
					messageJsonCustom.put("message", messageObj.getMessage());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return CommonLib.getResponseString(messageJsonCustom, "", CommonLib.RESPONSE_SUCCESS);
			} else
				return CommonLib.getResponseString("failure", "", CommonLib.RESPONSE_FAILURE);
		} else
			return CommonLib.getResponseString("failure", "", CommonLib.RESPONSE_FAILURE);

	}

	@Path("/view_messages")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public AllMessages viewMessages(@FormParam("access_token") String accessToken,
			@FormParam("to_userId") int toUserId) {

		AllMessages allmessages = null;
		UserDAO userdao = new UserDAO();

		int fromUserId = userdao.userActive(accessToken);

		if (fromUserId > 0 && toUserId > 0) {

			MessageDAO messagedao = new MessageDAO();

			allmessages = messagedao.getAllMessages(fromUserId, toUserId);

		}

		return allmessages;
	}

	@Path("/get")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public JSONObject getNewsFeed(@FormParam("access_token") String accessToken,
			@FormParam("client_id") String clientId, @FormParam("app_type") String appType) {
		// check for client_id
		if (!clientId.equals(CommonLib.ANDROID_CLIENT_ID))
			return CommonLib.getResponseString("Invalid client id", "", CommonLib.RESPONSE_INVALID_CLIENT_ID);

		// check for app type
		if (!appType.equals(CommonLib.ANDROID_APP_TYPE))
			return CommonLib.getResponseString("Invalid params", "", CommonLib.RESPONSE_INVALID_APP_TYPE);

		UserDAO userDao = new UserDAO();

		// access token validity
		int userId = userDao.userActive(accessToken);

		if (userId > 0) {
			// Iterate over all the wishes
			MessageDAO messageDao = new MessageDAO();
			JSONArray messageArr = new JSONArray();
			JSONObject messageJson = new JSONObject();
			ArrayList<UserCompactMessage> messages = messageDao.getAcceptedUsersForMessages(userId);
			try {
				java.util.Collections.sort(messages, new Comparator<UserCompactMessage>() {
					public int compare(UserCompactMessage s1, UserCompactMessage s2) {
						return (int) (s2.getTimestamp() - s1.getTimestamp());
					}
				});
				for (UserCompactMessage message : messages) {
					messageArr.put(JsonUtil.getUserCompatJson(message));
				}
				messageJson.put("messages", messageArr);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return CommonLib.getResponseString(messageJson, "", CommonLib.RESPONSE_SUCCESS);
		}
		return CommonLib.getResponseString("failure", "", CommonLib.RESPONSE_FAILURE);
	}
	
	// negotiation deposit amount
		@Path("/negotiate")
		@POST
		@Produces("application/json")
		@Consumes("application/x-www-form-urlencoded")
		public JSONObject updateNegotiatedWishes(@FormParam("client_id") String clientId,
				@FormParam("app_type") String appType, @FormParam("userId") int toUserId,
				@FormParam("wishId") int wishId, @FormParam("access_token") String accessToken,
				@FormParam("action") int actionType,
				@FormParam("negAmount") int negAmount) {

			// null checks, invalid request
			if (clientId == null || appType == null)
				return CommonLib.getResponseString("Invalid params", "",
						CommonLib.RESPONSE_INVALID_PARAMS);

			// check for client_id
			if (!clientId.equals(CommonLib.ANDROID_CLIENT_ID))
				return CommonLib.getResponseString("Invalid client id", "",
						CommonLib.RESPONSE_INVALID_CLIENT_ID);

			// check for app type
			if (!appType.equals(CommonLib.ANDROID_APP_TYPE))
				return CommonLib.getResponseString("Invalid params", "",
						CommonLib.RESPONSE_INVALID_APP_TYPE);

			UserDAO userDao = new UserDAO();

			// access token validity
			int userId = userDao.userActive(accessToken);
			
			if (userId > 0) {

				WishDAO wishDao = new WishDAO();
				Wish wish  = wishDao.getWish(wishId);

				boolean value = wishDao.updateWishedNegotiation(userId, actionType, wishId, negAmount );

					// get all sessions of toUserId from sessions table
					// create your own JSON object
					// send to all sessions
				if( value == true ){
					
					String notificationString = "";
					User user = userDao.getUserDetails(userId) ; 
					if( user != null )  {
						if( user.getUserName() == null || user.getUserName().equals("") ) {
							try { 
								JSONObject data = new JSONObject(user.getFacebookData());
								if(data.has("name")) {
									String name = String.valueOf(data.get("name"));
									name = name.split(" ")[0];
									notificationString = name ;
								}
							} catch(JSONException e) {
								e.printStackTrace();
							}
						} else
							notificationString = user.getUserName();
					}
					
					if(actionType == CommonLib.ACTION_NEGOTIATION_ACCEPTED)
						notificationString = notificationString + " has accepted the negotiation amount of " +  wish.getTitle() 
						+ " at Rs. " + negAmount + ".";
					else
						notificationString = notificationString + " wants to set the negotiation amount of " +  wish.getTitle() 
						+ " at Rs. " + negAmount + ".";
					
					JSONObject negJson = new JSONObject();
					try {
						negJson.put("user", JsonUtil.getUserJson(user));
						negJson.put("wish", JsonUtil.getWishJson(wish));
						negJson.put("message", notificationString);
						//negJson.put("type", value);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					UserDAO userdao = new UserDAO();
					userdao.sendPushToAllSessions(negJson, toUserId);
					
					
					//MessageDAO messageDao = new MessageDAO();
					//Message messageObj = messageDao.addMessage(notificationString, false, "" + System.currentTimeMillis(), userId,
						//	toUserId, wishId);

					return CommonLib.getResponseString("success", "",
							CommonLib.RESPONSE_SUCCESS);
				}

				else{
						return CommonLib.getResponseString("failure", "",
						CommonLib.RESPONSE_FAILURE);
				}
			} 
			else
			return CommonLib.getResponseString("failure", "",
				CommonLib.RESPONSE_FAILURE);

		}


}
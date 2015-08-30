package com.application.baatna.resource;

import java.util.ArrayList;
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
			@FormParam("userId") String toUserId, @FormParam("message") String message,
			@FormParam("access_token") String accessToken) {

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

		// access token validity
		int userId = userDao.userActive(accessToken);

		if (userId > 0) {
			MessageDAO messageDao = new MessageDAO();

			Message messageObj = messageDao.addMessage(message, false, "" + System.currentTimeMillis(), userId,
					Integer.parseInt(toUserId), -1);

			if (messageObj != null) {

				User fromUser = userDao.getUserDetails(messageObj.getFromUserId());
				User toUser = userDao.getUserDetails(messageObj.getToUserId());

				JSONObject messageJson = new JSONObject();
				try {
					messageJson.put("to_user", JsonUtil.getUserJson(toUser));
					messageJson.put("from_user", JsonUtil.getUserJson(fromUser));
					messageJson.put("message_id", messageObj.getMessageId());
					messageJson.put("from_to", true);
					messageJson.put("message", messageObj.getMessage());
				} catch (JSONException e) {
					e.printStackTrace();
				}

				messageDao.sendPushToNearbyUsers(messageJson);

				return CommonLib.getResponseString("success", "", CommonLib.RESPONSE_SUCCESS);
			} else
				return CommonLib.getResponseString("failure", "", CommonLib.RESPONSE_FAILURE);
		} else
			return CommonLib.getResponseString("failure", "", CommonLib.RESPONSE_FAILURE);

	}

//	@Path("/incoming_message")
//	@POST
//	@Produces(MediaType.TEXT_PLAIN)
//	@Consumes("application/x-www-form-urlencoded")
//	public String sendMessage(@FormParam("access_token") String accessToken, @FormParam("wishId") int wishId,
//			@FormParam("to_userId") int toUserId, @FormParam("message") String message) throws XMPPException {
//
//		UserDAO userdao = new UserDAO();
//
//		int fromUserId = userdao.userActive(accessToken);
//
//		if (fromUserId > 0) {
//
//			MessageDAO messagedao = new MessageDAO();
//			String messageId = "";
//
//			// getting toPushId of toUserId
//
//			String toPushId = userdao.getSessionDetails(toUserId, accessToken).getPushId();
//
//			// sending message touserId and toPushId
//			// PushDAO pushdao = new PushDAO();
//
//			// pushdao.connectToGCM();
//			//
//			// messageId = pushdao.sendMessage(GOOGLE_SERVER_KEY, toPushId,
//			// message);
//
//			if (!messageId.equals("FAILURE")) {
//
//				Date date = new Date();
//				System.out.println(date.toString());
//
//				if (messagedao.addMessage(message, true, date.toString(), fromUserId, toUserId, wishId,
//						Integer.parseInt(messageId)))
//					return "SUCCESS";
//
//			}
//
//			return "FAILURE";
//
//		}
//
//		else
//			return "FAILURE";
//	}

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
			@FormParam("client_id") String clientId, @FormParam("app_type") String appType,
			@FormParam("latitude") double latitude, @FormParam("longitude") double longitude) {
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
			ArrayList<NewsFeed> feedItems = new ArrayList<NewsFeed>();

			// inflate type 1 - User joined near you
			if (latitude == 0 && longitude == 0) {
				Session session = userDao.getSessionDetails(userId, accessToken);
				if (session != null && session.getLocation() != null) {
					latitude = session.getLocation().getLatitude();
					longitude = session.getLocation().getLongitude();
				}
			}
			Location location = new Location(latitude, longitude);

			// get the user feed
			FeedDAO feedDao = new FeedDAO();
			feedItems.addAll(feedDao.getNearbyUsers(location));
			feedItems.addAll(feedDao.getUsersWithWishes(location));
			feedItems.addAll(feedDao.getUsersWithWishesFulfilled(location));

			// sort based on timestamp of the feed items
			java.util.Collections.sort(feedItems, new Comparator<NewsFeed>() {
				public int compare(NewsFeed s1, NewsFeed s2) {
					return (int) (s1.getTimestamp() - s2.getTimestamp());
				}
			});

			// construction of big fat json
			JSONObject newsFeedJsonObject = new JSONObject();
			JSONArray feedItemJson = new JSONArray();
			try {
				newsFeedJsonObject.put("newsFeed", feedItemJson);
				for (NewsFeed feedItem : feedItems) {
					JSONObject feedJsonObject = new JSONObject();

					int type = feedItem.getType();

					if (type == 1) {

						int userIdFirst = feedItem.getUserIdFirst();
						User userFirst = userDao.getUserDetails(userIdFirst);

						feedJsonObject.put("userFirst", JsonUtil.getUserJson(userFirst));
						feedJsonObject.put("type", 1);

					} else if (type == 2) {

						int userIdFirst = feedItem.getUserIdFirst();
						int wishId = feedItem.getWishId();

						User userFirst = userDao.getUserDetails(userIdFirst);

						WishDAO wishDao = new WishDAO();
						Wish wish = wishDao.getWish(wishId);

						feedJsonObject.put("userFirst", JsonUtil.getUserJson(userFirst));

						feedJsonObject.put("wish", JsonUtil.getWishJson(wish));
						feedJsonObject.put("type", 2);

					} else if (type == 3) {

						int userIdFirst = feedItem.getUserIdFirst();
						int wishId = feedItem.getWishId();
						int userIdSecond = feedItem.getUserIdSecond();

						User userFirst = userDao.getUserDetails(userIdFirst);
						User userSecond = userDao.getUserDetails(userIdSecond);

						WishDAO wishDao = new WishDAO();
						Wish wish = wishDao.getWish(wishId);

						feedJsonObject.put("userFirst", JsonUtil.getUserJson(userFirst));
						feedJsonObject.put("userSecond", JsonUtil.getUserJson(userSecond));

						feedJsonObject.put("wish", JsonUtil.getWishJson(wish));
						feedJsonObject.put("type", 3);
					}
					feedItemJson.put(feedJsonObject);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return CommonLib.getResponseString(newsFeedJsonObject, "", CommonLib.RESPONSE_SUCCESS);
		} else
			return CommonLib.getResponseString("failure", "", CommonLib.RESPONSE_FAILURE);
	}

}

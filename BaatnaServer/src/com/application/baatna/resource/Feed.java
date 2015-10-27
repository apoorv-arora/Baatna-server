package com.application.baatna.resource;

import java.util.ArrayList;
import java.util.Comparator;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.application.baatna.bean.Location;
import com.application.baatna.bean.NewsFeed;
import com.application.baatna.bean.Session;
import com.application.baatna.bean.User;
import com.application.baatna.bean.Wish;
import com.application.baatna.dao.FeedDAO;
import com.application.baatna.dao.UserDAO;
import com.application.baatna.dao.WishDAO;
import com.application.baatna.util.CommonLib;
import com.application.baatna.util.JsonUtil;
import com.application.baatna.util.facebook.Friends;

@Path("/newsfeed")
public class Feed {

	@Path("/get")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public JSONObject getNewsFeed(@FormParam("access_token") String accessToken,
			@FormParam("client_id") String clientId, @FormParam("app_type") String appType,
			@FormParam("latitude") double latitude, @FormParam("longitude") double longitude,
			@QueryParam("start") int start, @QueryParam("count") int count) {
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

			User currentUser = userDao.getUserDetails(userId);
			// get the user feed
			FeedDAO feedDao = new FeedDAO();
			feedItems.addAll(feedDao.getFeedItems(location, start, count, userId));
			int total = feedDao.getFeedItemsCount(location, userId);
			// sort based on timestamp of the feed items
			java.util.Collections.sort(feedItems, new Comparator<NewsFeed>() {
				public int compare(NewsFeed s1, NewsFeed s2) {
					return (int) (s1.getTimestamp() - s2.getTimestamp());
				}
			});
			java.util.Collections.reverse(feedItems);

			// construction of big fat json
			JSONObject newsFeedJsonObject = new JSONObject();
			JSONArray feedItemJson = new JSONArray();
			try {
				for (NewsFeed feedItem : feedItems) {
					JSONObject feedJsonObject = new JSONObject();

					int type = feedItem.getType();

					if (type == 1) {

						int userIdFirst = feedItem.getUserIdFirst();

						User userFirst = userDao.getUserDetails(userIdFirst);

						boolean shouldAdd = true;
						
						if( CommonLib.isFacebookCheckValid ) {
							shouldAdd = Friends.isFriendOnFacebook(currentUser.getFacebookId(),
									userFirst.getFacebookId(), currentUser.getFacebookToken());
						}
						
						if ( shouldAdd ) {
							feedJsonObject.put("userFirst", JsonUtil.getUserJson(userFirst));

							feedJsonObject.put("timestamp", userFirst.getTimestamp());
							
							feedJsonObject.put("type", 1);
							try {
								Session session = userDao.getSession(userIdFirst);
								if (session.getLocation() != null) {
									feedJsonObject.put("latitude", session.getLocation().getLatitude());
									feedJsonObject.put("longitude", session.getLocation().getLongitude());
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

							feedItemJson.put(feedJsonObject);
						} else
							total--;

					} else if (type == 2) {

						int userIdFirst = feedItem.getUserIdFirst();
						int wishId = feedItem.getWishId();

						User userFirst = userDao.getUserDetails(userIdFirst);

						boolean shouldAdd = true;
						
						if( CommonLib.isFacebookCheckValid ) {
							shouldAdd = Friends.isFriendOnFacebook(currentUser.getFacebookId(),
									userFirst.getFacebookId(), currentUser.getFacebookToken());
						}
						
						if (shouldAdd) {
							WishDAO wishDao = new WishDAO();
							Wish wish = wishDao.getWish(wishId);

							feedJsonObject.put("userFirst", JsonUtil.getUserJson(userFirst));
							feedJsonObject.put("timestamp", wish.getTimeOfPost());
							feedJsonObject.put("wish", JsonUtil.getWishJson(wish));
							feedJsonObject.put("type", 2);

							try {
								Session session = userDao.getSession(userIdFirst);
								if (session.getLocation() != null) {
									feedJsonObject.put("latitude", session.getLocation().getLatitude());
									feedJsonObject.put("longitude", session.getLocation().getLongitude());
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

							feedItemJson.put(feedJsonObject);
						} else
							total--;
					} else if (type == 3) {

						int userIdFirst = feedItem.getUserIdFirst();
						int wishId = feedItem.getWishId();
						int userIdSecond = feedItem.getUserIdSecond();

						User userFirst = userDao.getUserDetails(userIdFirst);
						User userSecond = userDao.getUserDetails(userIdSecond);

						boolean shouldAdd = true;
						
						if( CommonLib.isFacebookCheckValid ) {
							shouldAdd = Friends.isFriendOnFacebook(currentUser.getFacebookId(),
									userFirst.getFacebookId(), currentUser.getFacebookToken());
							if(!shouldAdd) {
								shouldAdd = Friends.isFriendOnFacebook(currentUser.getFacebookId(),
										userSecond.getFacebookId(), currentUser.getFacebookToken()); 
							}
						}
						
						if (shouldAdd) {
							
							WishDAO wishDao = new WishDAO();
							Wish wish = wishDao.getWish(wishId);
							feedJsonObject.put("timestamp", wish.getTimeOfPost());
							feedJsonObject.put("userFirst", JsonUtil.getUserJson(userFirst));
							feedJsonObject.put("userSecond", JsonUtil.getUserJson(userSecond));

							feedJsonObject.put("wish", JsonUtil.getWishJson(wish));
							feedJsonObject.put("type", 3);

							try {
								Session session = userDao.getSession(userFirst.getUserId());
								if (session.getLocation() != null) {
									feedJsonObject.put("latitude", session.getLocation().getLatitude());
									feedJsonObject.put("longitude", session.getLocation().getLongitude());
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

							feedItemJson.put(feedJsonObject);
						} else 
							total--;
					}
				}
				newsFeedJsonObject.put("newsFeed", feedItemJson);
				newsFeedJsonObject.put("total", total);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return CommonLib.getResponseString(newsFeedJsonObject, "", CommonLib.RESPONSE_SUCCESS);
		} else
			return CommonLib.getResponseString("failure", "", CommonLib.RESPONSE_FAILURE);
	}

}

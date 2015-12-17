package com.application.baatna.resource;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.application.baatna.bean.Categories;
import com.application.baatna.bean.User;
import com.application.baatna.bean.Wish;
import com.application.baatna.dao.CategoryDAO;
import com.application.baatna.dao.FeedDAO;
import com.application.baatna.dao.UserDAO;
import com.application.baatna.dao.WishDAO;
import com.application.baatna.util.CommonLib;
import com.application.baatna.util.JsonUtil;

@Path("/wish")
public class WishPost {

	/**
	 * Create a new wish
	 */
	@Path("/add")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public JSONObject postWish(@FormParam("client_id") String clientId,
			@FormParam("app_type") String appType,
			@FormParam("title") String title,
			@FormParam("description") String description,
			@FormParam("required_for") int requiredFor,
			@FormParam("access_token") String accessToken) {

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
		final int userId = userDao.userActive(accessToken);

		if (userId > 0) {
			final WishDAO wishdao = new WishDAO();

			Wish wish = wishdao.addWishPost(title, description,
					System.currentTimeMillis(), userId, requiredFor);

			if (wish != null) {

				FeedDAO feedDao = new FeedDAO();
				boolean returnFeedResult = feedDao.addFeedItem(
						FeedDAO.USER_REQUESTED_WISH,
						System.currentTimeMillis(), userId, -1,
						wish.getWishId());
				if (returnFeedResult) {
					System.out.println("Success type 1");
				} else {
					System.out.println("Failure type 1");
				}

				// TODO: broadcast the wish to the nearby users.
				// GET THE CURRENT LOCATION
				// GET THE NEARBY USERS
				// SEND PUSH TO NEARBY USERS

				String notificationString = "";
				User user = userDao.getUserDetails(wish.getUserId()) ; 
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
				notificationString = notificationString + " wants to borrow " +  wish.getTitle();
				
				final JSONObject wishJson = new JSONObject();
				try {
					wishJson.put("user", JsonUtil.getUserJson(user));
					wishJson.put("wish", JsonUtil.getWishJson(wish));
					wishJson.put("message", notificationString);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				Runnable runnable = new Runnable() {
					@Override
					public void run() {
						wishdao.sendPushToNearbyUsers(wishJson, userId);
					}
				};
				Thread thread = new Thread(runnable);
				thread.start();

				return CommonLib.getResponseString("success", "",
						CommonLib.RESPONSE_SUCCESS);
			} else
				return CommonLib.getResponseString("failure", "",
						CommonLib.RESPONSE_FAILURE);
		} else
			return CommonLib.getResponseString("failure", "",
					CommonLib.RESPONSE_FAILURE);

	}

	@Path("/view")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public JSONObject viewWishes(@FormParam("access_token") String accessToken,
			@QueryParam("start") int start,
			@QueryParam("count") int count) {

		UserDAO dao = new UserDAO();
		int userId = dao.userActive(accessToken);

		if (userId > 0) {

			WishDAO wishdao = new WishDAO();
			List<Wish> wishes = wishdao.getAllWishes(userId, start, count);
			int size = wishdao.getWishesCount(userId);
			JSONObject returnObject = new JSONObject();
			try {
				JSONArray jsonArr = new JSONArray();
				for (Wish wish : wishes) {
					JSONObject wishJson = JsonUtil.getWishJson(wish);

					JSONArray userArr = new JSONArray();
					List<User> acceptedUsers = wishdao.getWishedUsers(1, wish.getWishId());
					for (User acceptedUser : acceptedUsers) {
						userArr.put(JsonUtil.getUserJson(acceptedUser));
					}
					wishJson.getJSONObject("wish").put("accepted_users", userArr);
					jsonArr.put(wishJson);
				}
				returnObject.put("wishes", jsonArr);
				returnObject.put("total", size);
			} catch (JSONException e) {

			}
			return CommonLib.getResponseString(returnObject, "success",
					CommonLib.RESPONSE_SUCCESS);
		}
		return CommonLib.getResponseString("failure", "failure",
				CommonLib.RESPONSE_FAILURE);

	}
	
	@Path("/get")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public JSONObject getWishes(@FormParam("access_token") String accessToken,
			@QueryParam("type") int type,
			@QueryParam("another_user") int anotherUser,
			@QueryParam("start") int start,
			@QueryParam("count") int count) {

		UserDAO dao = new UserDAO();
		int userId = dao.userActive(accessToken);

		if (userId > 0) {

			WishDAO wishdao = new WishDAO();
			if(anotherUser != 0)
				userId = anotherUser;
			List<Wish> wishes = wishdao.getAllWishesBasedOnType(userId, start, count, type);
			int size = wishdao.getAllWishesCountBasedOnType(userId, type);
			JSONObject returnObject = new JSONObject();
			try {
				JSONArray jsonArr = new JSONArray();
				for (Wish wish : wishes) {

					JSONObject wishJson = JsonUtil.getWishJson(wish);

					JSONArray userArr = new JSONArray();
					List<User> acceptedUsers = wishdao.getWishedUsers(1, wish.getWishId());
					for (User acceptedUser : acceptedUsers) {
						userArr.put(JsonUtil.getUserJson(acceptedUser));
					}
					wishJson.getJSONObject("wish").put("accepted_users", userArr);
					jsonArr.put(wishJson);
				}
				returnObject.put("wishes", jsonArr);
				returnObject.put("total", size);
			} catch (JSONException e) {

			}
			return CommonLib.getResponseString(returnObject, "success",
					CommonLib.RESPONSE_SUCCESS);
		}
		return CommonLib.getResponseString("failure", "failure",
				CommonLib.RESPONSE_FAILURE);

	}

	@Path("/delete")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public JSONObject deleteWishes(
			@FormParam("access_token") String accessToken,
			@FormParam("wishId") int wishId) {

		UserDAO dao = new UserDAO();
		int userId = dao.userActive(accessToken);
		boolean value = false;

		if (userId > 0) {

			WishDAO wishdao = new WishDAO();
			value = wishdao.deleteWish(wishId);

			if (value)
				return CommonLib.getResponseString(String.valueOf(wishId),
						"success", CommonLib.RESPONSE_SUCCESS);

		}

		return CommonLib.getResponseString("failure", "failure",
				CommonLib.RESPONSE_FAILURE);

	}

	@Path("/categories")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public JSONObject getCategories(
			@FormParam("access_token") String accessToken) {

		UserDAO dao = new UserDAO();
		int userId = dao.userActive(accessToken);

		if (userId > 0) {

			CategoryDAO categoryDao = new CategoryDAO();
			List<Categories> categories = categoryDao.getCategories();
			JSONObject returnObject = new JSONObject();
			try {
				JSONArray categoriesArr = new JSONArray();
				for (Categories category : categories) {
					categoriesArr.put(JsonUtil.getCategoryJson(category));
				}
				returnObject.put("categories", categoriesArr);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return CommonLib.getResponseString(returnObject, "",
					CommonLib.RESPONSE_SUCCESS);
		}

		return CommonLib.getResponseString("failure", "",
				CommonLib.RESPONSE_FAILURE);

	}

	@Path("/update")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public JSONObject updateWishes(@FormParam("client_id") String clientId,
			@FormParam("app_type") String appType,
			@FormParam("access_token") String accessToken,
			@FormParam("wishId") int wishId, @FormParam("action") int actionType) {

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

		UserDAO dao = new UserDAO();
		int userId = dao.userActive(accessToken);

		if (userId > 0) {

			WishDAO wishdao = new WishDAO();
			boolean value = false;
			value = wishdao.updateWishedUsers(userId, actionType, wishId);

			if (value) {

				FeedDAO feedDao = new FeedDAO();
				Wish currentWish = wishdao.getWish(wishId);
				boolean returnFeedResult = feedDao.addFeedItem(
						FeedDAO.USER_WISH_FULFILLED,
						System.currentTimeMillis(), currentWish.getUserId(),
						userId, wishId);
				if (returnFeedResult) {
					System.out.println("Success type 1");
				} else {
					System.out.println("Failure type 1");
				}
				JSONObject messageJson = new JSONObject();
				try {
					UserDAO userDao = new UserDAO();
					messageJson.put("from_user", JsonUtil.getUserJson(userDao.getUserDetails(userId)));
					messageJson.put("action_type", actionType);
					messageJson.put("wish", JsonUtil.getWishJson(currentWish));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if(actionType == 1)
					wishdao.sendPushToUsers(messageJson, currentWish.getUserId());
				return CommonLib.getResponseString(String.valueOf(wishId),
						"success", CommonLib.RESPONSE_SUCCESS);
			}

		} else
			return CommonLib.getResponseString("failure", "Invalid user",
					CommonLib.RESPONSE_INVALID_USER);

		return CommonLib.getResponseString("failure", "failure",
				CommonLib.RESPONSE_FAILURE);

	}

	@Path("/acceptedUsers")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public JSONObject getUpdateList(

	@FormParam("access_token") String accessToken,
			@FormParam("wishId") int wishId, @FormParam("type") int updateType) {

		UserDAO dao = new UserDAO();
		int userId = dao.userActive(accessToken);

		if (userId > 0) {
			List<User> users ;
			WishDAO wishdao = new WishDAO();
			users = wishdao.getWishedUsers(updateType, wishId);
			JSONObject returnJsonObject = new JSONObject();
			JSONArray usersArray = new JSONArray();
			try {
				for (Iterator iterator = users.iterator(); iterator.hasNext();) {
					User userwish = (User) iterator.next();
					usersArray.put(JsonUtil.getUserJson(userwish));
				}
				returnJsonObject.put("users", usersArray);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return CommonLib.getResponseString(returnJsonObject, "success",
					CommonLib.RESPONSE_SUCCESS);
		}

		return CommonLib.getResponseString("failure", "failure",
				CommonLib.RESPONSE_FAILURE);

	}
	
	@Path("/updateStatus")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public JSONObject updateWishStatus(@FormParam("client_id") String clientId,
			@FormParam("app_type") String appType,
			@FormParam("access_token") String accessToken,
			@FormParam("wishId") int wishId, @FormParam("action") int actionType) {

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

		UserDAO dao = new UserDAO();
		int userId = dao.userActive(accessToken);

		if (userId > 0) {

			WishDAO wishdao = new WishDAO();
			boolean value = false;
			value = wishdao.updateWishStatus(userId, actionType, wishId, actionType);

			return CommonLib.getResponseString(String.valueOf(wishId),
					"success", CommonLib.RESPONSE_SUCCESS);
		}
		return CommonLib.getResponseString("failure", "failure",
				CommonLib.RESPONSE_FAILURE);

	}

}

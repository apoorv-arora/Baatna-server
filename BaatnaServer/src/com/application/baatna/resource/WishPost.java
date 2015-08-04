package com.application.baatna.resource;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

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
		int userId = userDao.userActive(accessToken);

		if (userId > 0) {
			WishDAO wishdao = new WishDAO();

			Wish wish = wishdao.addWishPost(title, description,
					System.currentTimeMillis(), userId);

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
				wishdao.sendPushToNearbyUsers();

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
	public JSONObject viewWishes(@FormParam("access_token") String accessToken) {

		UserDAO dao = new UserDAO();
		int userId = dao.userActive(accessToken);

		if (userId > 0) {

			WishDAO wishdao = new WishDAO();
			List<Wish> wishes = wishdao.getAllWishes(userId);
			JSONObject returnObject = new JSONObject();
			try {
				JSONArray jsonArr = new JSONArray();
				for (Wish wish : wishes) {
					jsonArr.put(JsonUtil.getWishJson(wish));
				}
				returnObject.put("wishes", jsonArr);
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

			WishDAO wishdao = new WishDAO();
			Set users = wishdao.getWishedUsers(updateType, wishId);
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

}

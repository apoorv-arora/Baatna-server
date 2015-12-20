package com.application.baatna.util;

import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.application.baatna.bean.Categories;
import com.application.baatna.bean.Coupon;
import com.application.baatna.bean.Institution;
import com.application.baatna.bean.Logistics;
import com.application.baatna.bean.ReturnTable;
import com.application.baatna.bean.Session;
import com.application.baatna.bean.User;
import com.application.baatna.bean.UserCompactMessage;
import com.application.baatna.bean.Wish;
import com.application.baatna.dao.WishDAO;

public class JsonUtil {

	public static JSONObject getLogisticJson(Logistics logistic) throws JSONException{
		JSONObject logisticJsonObj=new JSONObject();
		JSONObject logisticJson=new JSONObject();
		
		logisticJsonObj.put("logistic_id",logistic.getLogistic_id());
		logisticJsonObj.put("borrow_id",logistic.getBorrower_id());
		logisticJsonObj.put("lender_id",logistic.getLender_id());
		logisticJsonObj.put("type",logistic.getType());
	
		
		logisticJson.put("logistic",logisticJsonObj);
		return logisticJson;
		
	}
	public static JSONObject getReturnJson(ReturnTable return_obj) throws JSONException{
		JSONObject returnJsonObj=new JSONObject();
		JSONObject returnJson=new JSONObject();
		
		returnJsonObj.put("logistic_id",return_obj.getReturn_id());
		returnJsonObj.put("borrow_id",return_obj.getBorrow_id());
		returnJsonObj.put("lender_id",return_obj.getLender_id());
		returnJsonObj.put("type",return_obj.getReturn_type());
	
		
		returnJson.put("logistic",returnJsonObj);
		return returnJson;
		
	}
	public static JSONObject getUserJson(User user) throws JSONException {
		JSONObject userJsonObject = new JSONObject();
		JSONObject userJson = new JSONObject();

		userJsonObject.put("user_id", user.getUserId());
		userJsonObject.put("is_verified", user.getIsVerified());
		userJsonObject.put("address", user.getAddress());
		userJsonObject.put("email", user.getEmail());
		userJsonObject.put("phone", user.getPhone());
		userJsonObject.put("profile_pic", user.getProfilePic());
		userJsonObject.put("fbId", user.getFacebookId());
		userJsonObject.put("bio", user.getBio());
		userJsonObject.put("rating", user.getRating());
		userJsonObject.put("contact", user.getPhone());
		if (user.getUserName() == null || user.getUserName().equals("")) {

			JSONObject data = null;
			try {
				data = new JSONObject(user.getFacebookData());
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (data != null && data.has("name")) {
				String name = String.valueOf(data.get("name"));
				name = name.split(" ")[0];
				userJsonObject.put("user_name", name);
			}
		} else {
			String name = user.getUserName();
			name = name.split(" ")[0];
			userJsonObject.put("user_name", name);
		}
		userJson.put("user", userJsonObject);
		return userJson;
	}
	
	public static JSONObject getUserJsonWithoutBio(User user) throws JSONException {
		JSONObject userJsonObject = new JSONObject();
		JSONObject userJson = new JSONObject();

		userJsonObject.put("user_id", user.getUserId());
		userJsonObject.put("is_verified", user.getIsVerified());
		userJsonObject.put("address", user.getAddress());
		userJsonObject.put("email", user.getEmail());
		userJsonObject.put("phone", user.getPhone());
		userJsonObject.put("profile_pic", user.getProfilePic());
		userJsonObject.put("fbId", user.getFacebookId());
		userJsonObject.put("contact", user.getPhone());
		userJsonObject.put("rating",user.getRating());
		if (user.getUserName() == null || user.getUserName().equals("")) {

			JSONObject data = null;
			try {
				data = new JSONObject(user.getFacebookData());
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (data != null && data.has("name")) {
				String name = String.valueOf(data.get("name"));
				name = name.split(" ")[0];
				userJsonObject.put("user_name", name);
			}
		} else {
			String name = user.getUserName();
			name = name.split(" ")[0];
			userJsonObject.put("user_name", name);
		}
		userJson.put("user", userJsonObject);
		return userJson;
	}

	public static JSONObject getWishJson(Wish wish) throws JSONException {
		JSONObject categoryJson = new JSONObject();

		JSONObject wishJsonObject = new JSONObject();

		wishJsonObject.put("title", wish.getTitle());
		wishJsonObject.put("description", wish.getDescription());
		wishJsonObject.put("user_id", wish.getUserId());
		wishJsonObject.put("time_post", wish.getTimeOfPost());
		wishJsonObject.put("wish_id", wish.getWishId());
		wishJsonObject.put("status", wish.getStatus());
		wishJsonObject.put("required_for", wish.getRequiredFor());

		categoryJson.put("wish", wishJsonObject);
		return categoryJson;

	}

	public static JSONObject getCategoryJson(Categories category) throws JSONException {
		JSONObject categoryJson = new JSONObject();

		JSONObject wishJsonObject = new JSONObject();

		wishJsonObject.put("category_id", category.getCategoryId());
		wishJsonObject.put("category_name", category.getCategory());
		wishJsonObject.put("category_icon", category.getCategoryIcon());

		categoryJson.put("category", wishJsonObject);

		return categoryJson;
	}

	public static JSONObject getInstitutionJson(Institution name) throws JSONException {
		JSONObject categoryJson = new JSONObject();

		JSONObject wishJsonObject = new JSONObject();

		wishJsonObject.put("name", name.getInstitutionName());
		JSONArray branchArr = new JSONArray();
		for (String branch : name.getBranches()) {
			branchArr.put(branch);
		}
		wishJsonObject.put("branches", branchArr);

		categoryJson.put("institution", wishJsonObject);

		return categoryJson;
	}

	public static JSONObject getSessionJson(Session session) throws JSONException {
		JSONObject sessionJsonObject = new JSONObject();
		JSONObject sessionJson = new JSONObject();

		sessionJsonObject.put("latitude", session.getLocation() != null ? session.getLocation().getLatitude() : 0);
		sessionJsonObject.put("longitude", session.getLocation() != null ? session.getLocation().getLongitude() : 0);
		sessionJsonObject.put("user_id", session.getUserId());
		sessionJsonObject.put("session_id", session.getSessionId());
		sessionJson.put("session", sessionJsonObject);
		return sessionJson;
	}

	public static JSONObject getUserCompatJson(UserCompactMessage object) throws JSONException {
		JSONObject userCompatJsonObject = new JSONObject();
		JSONObject userCompatJson = new JSONObject();
		
		WishDAO wishDao = new WishDAO();
		
		JSONObject wishJson = JsonUtil.getWishJson(object.getWish());
		
		JSONArray userArr = new JSONArray();
		List<User> acceptedUsers = wishDao.getWishedUsers(1, object.getWish().getWishId());
		for (User acceptedUser : acceptedUsers) {
			userArr.put(JsonUtil.getUserJson(acceptedUser));
		}
		wishJson.getJSONObject("wish").put("accepted_users", userArr);
		userCompatJsonObject.put("wish", wishJson);
		userCompatJsonObject.put("user", getUserJson(object.getUser()));
		userCompatJsonObject.put("type", object.getType());

		userCompatJson.put("message", userCompatJsonObject);
		return userCompatJson;
	}

	public static JSONObject getCouponDetails(Coupon object) throws JSONException {
		JSONObject couponJsonObject = new JSONObject();
		JSONObject couponJson = new JSONObject();

		couponJsonObject.put("id", object.getId());
		couponJsonObject.put("count", object.getCount());
		couponJsonObject.put("image", object.getImage());
		couponJsonObject.put("name", object.getName());
		couponJsonObject.put("terms", object.getTerms());
		couponJsonObject.put("validity", object.getValidity());

		couponJson.put("coupon", couponJsonObject);
		return couponJson;
	}

}

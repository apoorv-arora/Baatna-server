package com.application.baatna.util;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.application.baatna.bean.Categories;
import com.application.baatna.bean.Institution;
import com.application.baatna.bean.Session;
import com.application.baatna.bean.User;
import com.application.baatna.bean.UserCompactMessage;
import com.application.baatna.bean.Wish;

public class JsonUtil {

	public static JSONObject getUserJson(User user) throws JSONException{
		JSONObject userJsonObject = new JSONObject();
		JSONObject userJson = new JSONObject();
		
		userJsonObject.put("user_id", user.getUserId());
		userJsonObject.put("is_verified", user.getIsVerified());
		userJsonObject.put("address", user.getAddress());
		userJsonObject.put("description", user.getBio());
		userJsonObject.put("email", user.getEmail());
		userJsonObject.put("phone", user.getPhone());
		userJsonObject.put("profile_pic", user.getProfilePic());
		userJsonObject.put("fbId", user.getFacebookId());
		userJsonObject.put("contact", user.getPhone());
		if(user.getUserName() == null || user.getUserName().equals("")) {
			JSONObject data = new JSONObject(user.getFacebookData());
			if(data.has("name"))
				userJsonObject.put("user_name", String.valueOf(data.get("name")));
		} else
			userJsonObject.put("user_name", user.getUserName());
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
		
		JSONArray userArr = new JSONArray();
		for( User acceptedUser: wish.getAcceptedUsers() ) {
			userArr.put(JsonUtil.getUserJson(acceptedUser));
		}
		wishJsonObject.put("accepted_users", userArr);
		
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
		for(String branch: name.getBranches()) {
			branchArr.put(branch);
		}
		wishJsonObject.put("branches", branchArr);
		
		categoryJson.put("institution", wishJsonObject);
		
		return categoryJson;
	}
	
	public static JSONObject getSessionJson(Session session) throws JSONException{
		JSONObject sessionJsonObject = new JSONObject();
		JSONObject sessionJson = new JSONObject();
		
		sessionJsonObject.put("latitude", session.getLocation() != null ? session.getLocation().getLatitude():0);
		sessionJsonObject.put("longitude", session.getLocation() != null ? session.getLocation().getLongitude():0);
		sessionJsonObject.put("user_id", session.getUserId());
		sessionJsonObject.put("session_id", session.getSessionId());
		sessionJson.put("session", sessionJsonObject);
		return sessionJson;
	}
	
	public static JSONObject getUserCompatJson(UserCompactMessage object) throws JSONException{
		JSONObject userCompatJsonObject = new JSONObject();
		JSONObject userCompatJson = new JSONObject();

		userCompatJsonObject.put("wish", getWishJson(object.getWish()));
		userCompatJsonObject.put("user", getUserJson(object.getUser()));
		userCompatJsonObject.put("type", object.getType());
		
		userCompatJson.put("message", userCompatJsonObject);
		return userCompatJson;
	}
	
}

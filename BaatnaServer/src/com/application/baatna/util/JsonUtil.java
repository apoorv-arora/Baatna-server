package com.application.baatna.util;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.application.baatna.bean.Categories;
import com.application.baatna.bean.NewsFeed;
import com.application.baatna.bean.Session;
import com.application.baatna.bean.User;
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
		
		categoryJson.put("wish", wishJsonObject);
		return categoryJson;
		
		
	}
	
	public static JSONObject getCategoryJson(Categories category) throws JSONException {
		JSONObject categoryJson = new JSONObject();
		
		JSONObject wishJsonObject = new JSONObject();
		
		wishJsonObject.put("category_id", category.getCategoryId());
		wishJsonObject.put("category_name", category.getCategory());
		
		categoryJson.put("category", wishJsonObject);
		
		return categoryJson;
	}
	
	public static JSONObject getInstitutionJson(String name) throws JSONException {
		JSONObject categoryJson = new JSONObject();
		
		JSONObject wishJsonObject = new JSONObject();
		
		wishJsonObject.put("name", name);
		
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
	
}

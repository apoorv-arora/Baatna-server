package com.application.baatna.resource;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.mail.EmailException;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.application.baatna.bean.Institution;
import com.application.baatna.bean.User;
import com.application.baatna.bean.Wish;
import com.application.baatna.dao.FeedDAO;
import com.application.baatna.dao.UserDAO;
import com.application.baatna.dao.WishDAO;
import com.application.baatna.util.CommonLib;
import com.application.baatna.util.CryptoHelper;
import com.application.baatna.util.JsonUtil;
import com.application.baatna.util.mailer.EmailModel;
import com.application.baatna.util.mailer.EmailUtil;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("/user")
public class BaatnaUser {

	@Path("/signup")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public JSONObject userSignup(@DefaultValue("images/default.jpg") @FormParam("profile_pic") String profilePic,
			@FormParam("client_id") String clientId, @FormParam("app_type") String appType,
			@FormParam("user_name") String userName, @FormParam("password") String passWord,
			@FormParam("email") String email, @FormParam("address") String address, @FormParam("phone") String phone,
			@FormParam("bio") String bio, @FormParam("fbid") String fbId, @FormParam("fbdata") String fbData,
			@FormParam("fb_token") String fbToken, @FormParam("fb_permission") String fb_permissions,
			@QueryParam("isFacebookLogin") boolean isFacebookLogin) {

		// check for client_id
		if (!clientId.equals(CommonLib.ANDROID_CLIENT_ID))
			return CommonLib.getResponseString("Invalid client id", "", CommonLib.RESPONSE_INVALID_CLIENT_ID);

		// check for app type
		if (!appType.equals(CommonLib.ANDROID_APP_TYPE))
			return CommonLib.getResponseString("Invalid params", "", CommonLib.RESPONSE_INVALID_APP_TYPE);

		// check invalid params
		if (userName == null || userName.isEmpty())
			CommonLib.getResponseString("", "Invalid userName", CommonLib.RESPONSE_INVALID_PARAMS);

		if (passWord == null || passWord.isEmpty())
			CommonLib.getResponseString("", "Invalid passWord", CommonLib.RESPONSE_INVALID_PARAMS);

		if (email == null || email.isEmpty())
			CommonLib.getResponseString("", "Invalid email", CommonLib.RESPONSE_INVALID_PARAMS);

		UserDAO dao = new UserDAO();
		// check if user exists
		boolean userExists = dao.UserExists(email);
		User userCreated;
		// create the user if the user does not exist.
		if (!userExists) {
			String facebookPic = null;
			if (fbData != null) {
				JSONObject object = null;
				try {
					object = new JSONObject(fbData);
					if (object.has("picture")) {
						object = object.getJSONObject("picture");
						if (object.has("data")) {
							object = object.getJSONObject("data");
							if (object.has("url"))
								facebookPic = String.valueOf(object.get("url"));
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			if (facebookPic != null)
				profilePic = facebookPic;
			userCreated = dao.addUserDetails(profilePic, userName, passWord, email, address, phone, bio, fbId, fbData,
					fbToken, fb_permissions);

			if (userCreated != null) {
				EmailModel emailModel = new EmailModel();
				emailModel.setTo(email);
				emailModel.setFrom(CommonLib.BAPP_ID);
				emailModel.setSubject("Welcome to your local Baatna Community!!");
				emailModel.setContent(
						"Hey," + "\n\nWelcome to Baatna! Thank you for becoming a member of the local Baatna community!"
								+ "\n\nBaatna enables you to borrow the things you need from people in your neighborhood. Right here, right now, for free."
								+ "\n\nHow does it work?"
								+ "\n\nYou know those moments where you need to use something that you do not need to own? Tell Baatna what you are looking for and we'll find friendly neighbors willing to lend it to you. Looking for something right now? Just go to the app and post your need."
								+ "\n\nIn return you can share your stuff when it's convenient. If one of your neighbors is looking for something, we will let you know. It's up to you if you want to lend out your stuff. Be an awesome neighbor and share the love!"
								+ "\n\nWe're doing our best to make Baatna more efficient and useful for you everyday. Incase you have any feedback, please get back to us at -hello@baatna.com."
								+ "\nWe would love to hear from you." + "\n\nCheers" + "\nBaatna Team");
				EmailUtil.getInstance().sendEmail(emailModel);
				// TODO: send a push to nearby users.

				FeedDAO feedDao = new FeedDAO();
				boolean returnFeedResult = feedDao.addFeedItem(FeedDAO.USER_JOINED, System.currentTimeMillis(),
						userCreated.getUserId(), -1, -1);
				if (returnFeedResult) {
					System.out.println("Success type 1");
				} else {
					System.out.println("Failure type 1");
				}

				return CommonLib.getResponseString("success", "", CommonLib.RESPONSE_SUCCESS);

			} else {
				return CommonLib.getResponseString("", "Something went wrong", CommonLib.RESPONSE_FAILURE);
			}
		} else {
			return CommonLib.getResponseString("", "User already exists with the email", CommonLib.RESPONSE_FAILURE);
		}
	}

	/**
	 * Verification process triggered by an email.
	 * 
	 * @param blob
	 *            Encrypted blob which contains user_id
	 */
	@Path("/verifyEmail")
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public String verifyUser(@QueryParam("blob") String blobToDecrypt) {
		if (blobToDecrypt == null)
			return "";

		CryptoHelper helper = new CryptoHelper();
		try {
			String userJsonStr = helper.decrypt(blobToDecrypt, null, null);
			if (userJsonStr != null) {
				JSONObject userJson = new JSONObject(userJsonStr);
				if (userJson.has("user_id") && userJson.get("user_id") instanceof Integer) {
					UserDAO userDao = new UserDAO();
					User userObject = userDao.getUserDetails(userJson.getInt("user_id"));
					if (userObject != null) {
						userDao.verifyUserEmail(userObject, true);
						return CommonLib.getResponseString("success", "", CommonLib.RESPONSE_SUCCESS).toString();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	@Path("/editProfile")
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes("application/x-www-form-urlencoded")
	public String changeProfile(@FormParam("access_token") String accessToken, @FormParam("userName") String userName,
			@FormParam("passWord") String passWord, @FormParam("email") String email,
			@FormParam("address") String address, @FormParam("phone") String phone, @FormParam("bio") String bio) {

		UserDAO userdao = new UserDAO();

		int userId = userdao.userActive(accessToken);
		if (userId > 0) {

			if (userdao.editUserDetails(userId, userName, passWord, email, address, phone, bio))
				return "SUCCESS";

		}

		return "FAILURE";
	}

	@Path("/editPicture")
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String uploadFile(@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail,
			@FormDataParam("access_token") String accessToken) throws IOException {

		UserDAO userdao = new UserDAO();

		int userId = userdao.userActive(accessToken);

		if (userId > 0) {

			try {

				Random random = new Random();
				String uri = random.nextInt() + fileDetail.getFileName();
				File fileToUpload = new File(uri);
				BufferedImage img = ImageIO.read(uploadedInputStream);
				ImageIO.write(img, "png", fileToUpload);

				if (userdao.editProfilePic(userId, uri))
					return "SUCCESS";

			} catch (IOException ex) {
				ex.printStackTrace();
				return "FAILURE";
			}
		}

		return "FAILURE";

	}

	@Path("/registrationId")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public JSONObject registerPushId(@FormParam("access_token") String accessToken,
			@FormParam("pushId") String pushId) {

		UserDAO dao = new UserDAO();

		int userId = dao.userActive(accessToken);

		if (userId > 0 && dao.updatePushId(pushId, accessToken))
			return CommonLib.getResponseString("success", "success", CommonLib.RESPONSE_SUCCESS);

		return CommonLib.getResponseString("failure", "failure", CommonLib.RESPONSE_FAILURE);
	}

	@Path("/location")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public JSONObject registerPushId(@FormParam("access_token") String accessToken, @FormParam("latitude") double lat,
			@FormParam("longitude") double lon) {

		UserDAO dao = new UserDAO();

		int userId = dao.userActive(accessToken);

		if (userId > 0 && dao.updateLocation(lat, lon, accessToken))
			return CommonLib.getResponseString("success", "success", CommonLib.RESPONSE_SUCCESS);

		return CommonLib.getResponseString("failure", "failure", CommonLib.RESPONSE_FAILURE);
	}

	@Path("/nearbyusers")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public JSONObject getNearbyUsers(@FormParam("client_id") String clientId, @FormParam("app_type") String appType,
			@FormParam("access_token") String accessToken) {

		// check for client_id
		if (!clientId.equals(CommonLib.ANDROID_CLIENT_ID))
			return CommonLib.getResponseString("Invalid client id", "", CommonLib.RESPONSE_INVALID_CLIENT_ID);

		// check for app type
		if (!appType.equals(CommonLib.ANDROID_APP_TYPE))
			return CommonLib.getResponseString("Invalid params", "", CommonLib.RESPONSE_INVALID_APP_TYPE);

		UserDAO dao = new UserDAO();
		// check if user exists
		int userId = dao.userActive(accessToken);
		// create the user if the user does not exist.
		if (userId > 0) {
			ArrayList<com.application.baatna.bean.Session> nearbyUsers = dao.getNearbyUsers();
			JSONObject nearbyUsersJson = new JSONObject();
			JSONArray userArr = new JSONArray();
			try {
				for (com.application.baatna.bean.Session nearbyUser : nearbyUsers) {
					userArr.put(JsonUtil.getSessionJson(nearbyUser));
				}
				nearbyUsersJson.put("users", userArr);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return CommonLib.getResponseString(nearbyUsersJson, "", CommonLib.RESPONSE_SUCCESS);

		} else {
			return CommonLib.getResponseString("", "Something went wrong", CommonLib.RESPONSE_FAILURE);
		}
	}
	
	@Path("/institution")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public JSONObject updateInstitution(@FormParam("client_id") String clientId, @FormParam("app_type") String appType,
			@FormParam("access_token") String accessToken, @FormParam("institution_name") String institutionId,
			@FormParam("branch_name") String branchName, @FormParam("year") int year,
			@FormParam("phone_number") String phoneNumber) {

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

			// check institution validity
			if (!userDao.validateInstitution(institutionId))
				return CommonLib.getResponseString("failure", "Invalid institution name", CommonLib.RESPONSE_FAILURE);

			boolean returnValue = userDao.updateInstitution(institutionId, "", userId, 1, branchName, year,
					phoneNumber);
			if (returnValue) {
				return CommonLib.getResponseString("success", "", CommonLib.RESPONSE_SUCCESS);
			} else
				return CommonLib.getResponseString("failure", "Something went wrong", CommonLib.RESPONSE_FAILURE);
		} else
			return CommonLib.getResponseString("failure", "", CommonLib.RESPONSE_FAILURE);

	}

	@Path("/institutions")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public JSONObject getCategories(@FormParam("access_token") String accessToken) {

		UserDAO dao = new UserDAO();
		int userId = dao.userActive(accessToken);

		if (userId > 0) {

			List<Institution> categories = CommonLib.getInstitutionsList();
			JSONObject returnObject = new JSONObject();
			try {
				JSONArray categoriesArr = new JSONArray();
				for (Institution category : categories) {
					categoriesArr.put(JsonUtil.getInstitutionJson(category));
				}
				returnObject.put("institutions", categoriesArr);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return CommonLib.getResponseString(returnObject, "", CommonLib.RESPONSE_SUCCESS);
		}

		return CommonLib.getResponseString("failure", "", CommonLib.RESPONSE_FAILURE);

	}

	@Path("/test")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public JSONObject getTest() {
		JSONObject object = new JSONObject();
		try {
			object.put("test", "passed");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}

	@Path("/details")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public JSONObject userDetails(@FormParam("client_id") String clientId, @FormParam("app_type") String appType,
			@QueryParam("user_id") String userId) {

		// check for client_id
		if (!clientId.equals(CommonLib.ANDROID_CLIENT_ID))
			return CommonLib.getResponseString("Invalid client id", "", CommonLib.RESPONSE_INVALID_CLIENT_ID);

		// check for app type
		if (!appType.equals(CommonLib.ANDROID_APP_TYPE))
			return CommonLib.getResponseString("Invalid params", "", CommonLib.RESPONSE_INVALID_APP_TYPE);

		UserDAO dao = new UserDAO();
		// check if user exists
		User userExists = dao.getUserDetails(Integer.parseInt(userId));
		// create the user if the user does not exist.
		if (userExists != null) {
			try {
				return CommonLib.getResponseString(JsonUtil.getUserJson(userExists), "Success",
						CommonLib.RESPONSE_SUCCESS);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return CommonLib.getResponseString("", "User already exists with the email", CommonLib.RESPONSE_FAILURE);
	}

	@Path("/feedback")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public JSONObject userSignup(@FormParam("client_id") String clientId, @FormParam("app_type") String appType,
			@FormParam("log") String log, @FormParam("message") String title,
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
			User user = userDao.getUserDetails(userId);
			EmailModel emailModel = new EmailModel();
			emailModel.setTo(CommonLib.BAPP_ID);
			emailModel.setFrom(user.getEmail());
			emailModel.setSubject("Baatna Android Application Feedback");
			emailModel.setContent("Message:" + title + "\n\nLog:" + log);
			EmailUtil.getInstance().sendEmail(emailModel);
			return CommonLib.getResponseString("success", "success", CommonLib.RESPONSE_SUCCESS);
		} else
			return CommonLib.getResponseString("failure", "", CommonLib.RESPONSE_FAILURE);
	}
	@Path("/rating")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public JSONObject updateRating(@FormParam("client_id") String clientId,
			@FormParam("app_type") String appType,
			@FormParam("access_token") String accessToken,
			@FormParam("userId") int userId, @FormParam("rating") double rating) {

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

		UserDAO dao= new UserDAO();
		int userIdtwo=dao.userActive(accessToken);
		boolean retValue=false;
		boolean userCanRate=false;
		if(dao.usersEverInteracted(userIdtwo,userId))
		{
			userCanRate=true;
		}
		if(userId>0 )
		{

			if(userIdtwo > 0 && userCanRate){
				retValue = dao.setRatingForUser(userIdtwo,userId, rating);
			}
		}
		if(retValue)
		{
		return CommonLib.getResponseString("success", "success", CommonLib.RESPONSE_SUCCESS);
		}
	 else
		return CommonLib.getResponseString("failure", "", CommonLib.RESPONSE_RATED_FAILURE);
}
	@Path("/editrating")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public JSONObject editRating(@FormParam("client_id") String clientId,
			@FormParam("app_type") String appType,
			@FormParam("access_token") String accessToken,
			@FormParam("userId") int userId, @FormParam("rating") double rating) {

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

		
		boolean retValue=false;
		if(userId>0 )
		{
			UserDAO dao= new UserDAO();
			int userIdtwo=dao.userActive(accessToken);

			if(userIdtwo > 0){
				retValue = dao.editRatingForUser(userIdtwo,userId, rating);
			}
		}
		if(retValue)
		{
		return CommonLib.getResponseString("success", "success", CommonLib.RESPONSE_SUCCESS);
		}
	 else
		return CommonLib.getResponseString("failure", "", CommonLib.RESPONSE_RATED_FAILURE);
}
	@Path("/viewrating")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public JSONObject viewRating(@FormParam("client_id") String clientId,
			@FormParam("app_type") String appType,
			@FormParam("access_token") String accessToken,
			@QueryParam("userId") int userId) {

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

		
		UserDAO dao= new UserDAO();
		// check if user exists
		User userExists = dao.getUserDetails(userId);
		if(dao.userActive(accessToken)>0 && userExists!=null)
		{
			JSONObject returnObject= new JSONObject();
			
			try{
			//JSONObject user=JsonUtil.getUserJson(userExists);
		    JSONObject ratingJson=JsonUtil.getRatingJson(userExists);
			returnObject.put("rating",ratingJson);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return CommonLib.getResponseString(returnObject, "success", CommonLib.RESPONSE_SUCCESS);
		}
		return CommonLib.getResponseString("failure", "", CommonLib.RESPONSE_RATED_FAILURE);
		
		
		
		
		
		
		
	
}
}
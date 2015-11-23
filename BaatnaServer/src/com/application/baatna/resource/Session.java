package com.application.baatna.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.mail.EmailException;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.engine.spi.SessionDelegatorBaseImpl;

import com.application.baatna.bean.Location;
import com.application.baatna.bean.User;
import com.application.baatna.dao.FeedDAO;
import com.application.baatna.dao.UserDAO;
import com.application.baatna.types.EmailType;
import com.application.baatna.util.CommonLib;
import com.application.baatna.util.EmailUtil;
import com.application.baatna.util.JsonUtil;

@Path("/auth")
public class Session {

	/**
	 * Login Api call.
	 */
	@Path("/login")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public JSONObject authorization(@DefaultValue("images/default.jpg") @FormParam("profile_pic") String profilePic,
			@FormParam("client_id") String clientId, @FormParam("user_name") String userName,
			@FormParam("email") String email, @FormParam("app_type") String appType,
			@FormParam("password") String password, @FormParam("address") String address,
			@FormParam("phone") String phone, @FormParam("bio") String bio, @FormParam("registration_id") String regId,
			@FormParam("latitude") double latitude, @FormParam("longitude") double longitude,
			@FormParam("fbid") String fbId, @FormParam("fbdata") String fbData, @FormParam("fb_token") String fbToken,
			@FormParam("fb_permission") String fb_permissions, @QueryParam("isFacebookLogin") boolean isFacebookLogin) {

		// null checks, invalid request
		if (clientId == null || appType == null)
			return CommonLib.getResponseString("Invalid params", "", CommonLib.RESPONSE_INVALID_PARAMS);

		// check for client_id
		if (!clientId.equals(CommonLib.ANDROID_CLIENT_ID))
			return CommonLib.getResponseString("Invalid client id", "", CommonLib.RESPONSE_INVALID_CLIENT_ID);

		// check for app type
		if (!appType.equals(CommonLib.ANDROID_APP_TYPE))
			return CommonLib.getResponseString("Invalid params", "", CommonLib.RESPONSE_INVALID_APP_TYPE);

		if (!((isFacebookLogin && fbToken != null && !fbToken.isEmpty())) && (email == null || password == null))
			return CommonLib.getResponseString("Invalid params", "", CommonLib.RESPONSE_INVALID_PARAMS);

		UserDAO userDao = new UserDAO();
		User user = userDao.getUserDetails(email, password);

		// user does not exist
		if ((user == null || user.getUserId() <= 0) && !(isFacebookLogin && fbToken != null && !fbToken.isEmpty()))
			return CommonLib.getResponseString("Invalid user", "Invalid login credentials",
					CommonLib.RESPONSE_INVALID_PARAMS);

		// create user if it does not exist, else generate the access token
		if (user == null || user.getUserId() <= 0) {

			user = userDao.getUserDetails(fbId);

			if (user == null || user.getUserId() <= 0) {
				user = userDao.addUserDetails(profilePic, userName, password, email, address, phone, bio, fbId, fbData,
						fbToken, fb_permissions);

				if (user != null) {
					try {
						EmailUtil.sendEmail(email, EmailType.VERIFY_MAIL);
						// TODO: send a push to nearby users.
					} catch (EmailException e) {
						e.printStackTrace();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
					FeedDAO feedDao = new FeedDAO();
					boolean returnFeedResult = feedDao.addFeedItem(FeedDAO.USER_JOINED, System.currentTimeMillis(),
							user.getUserId(), -1, -1);
					if (returnFeedResult) {
						System.out.println("Success type 1");
					} else {
						System.out.println("Failure type 1");
					}
				}
			}
		}

		if (user == null || user.getUserId() <= 0)
			return CommonLib.getResponseString("Error", "Some error occured", CommonLib.RESPONSE_INVALID_PARAMS);

		// email verification check
		int status = CommonLib.RESPONSE_SUCCESS;
		// : CommonLib.RESPONSE_INVALID_USER;

		// Generate Access Token
		String accessToken = userDao.generateAccessToken(user.getUserId());

		// TODO: send the complete user object in json
		Location location = new Location(latitude, longitude);
		boolean sessionAdded = userDao.addSession(user.getUserId(), accessToken, regId, location);
		if (sessionAdded) {
			JSONObject responseObject = new JSONObject();
			try {
				responseObject.put("access_token", accessToken);
				responseObject.put("user_id", user.getUserId());
				responseObject.put("email", user.getEmail());
				responseObject.put("profile_pic", user.getProfilePic());
				responseObject.put("username", user.getUserName());
				if (user.getIsInstitutionVerified() == 1) {
					responseObject.put("HSLogin", false);// flag which checks if
															// the user is
															// verified or not
					responseObject.put("instutionLogin", true);// flag to
																// disable the
																// institution
																// login
					responseObject.put("INSTITUTION_NAME", user.getInstitutionName());
					responseObject.put("STUDENT_ID", user.getStudentId());
				}
				responseObject.put("user", JsonUtil.getUserJson(user));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return CommonLib.getResponseString(responseObject.toString(), "", status);
		} else
			return CommonLib.getResponseString("failed", "", status);
	}

	@Path("/logout")
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes("application/x-www-form-urlencoded")
	public String userLogout(@FormParam("access_token") String accessToken) {

		UserDAO dao = new UserDAO();
		int userId = dao.userActive(accessToken);

		boolean returnValue = dao.nullifyAccessToken(userId, accessToken);

		if (accessToken != null && !returnValue)
			return "FAILURE";

		return "SUCCESS";
	}

	@Path("/appConfig")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public JSONObject userBlock(@FormParam("access_token") String accessToken) {

		UserDAO dao = new UserDAO();
		int userId = dao.userActive(accessToken);

		if (userId > 0) {
			return CommonLib.getResponseString("", "You cannot use this app anymot, fuck off!",
					CommonLib.RESPONSE_SUCCESS);
		}
		return CommonLib.getResponseString("", "You cannot use this app anymot, fuck off!", CommonLib.RESPONSE_FAILURE);
	}

}

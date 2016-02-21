package com.application.baatna.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.application.baatna.bean.Location;
import com.application.baatna.bean.User;
import com.application.baatna.dao.FeedDAO;
import com.application.baatna.dao.UserDAO;
import com.application.baatna.util.CommonLib;
import com.application.baatna.util.JsonUtil;
import com.application.baatna.util.mailer.EmailModel;
import com.application.baatna.util.mailer.EmailUtil;

@Path("/auth")
public class Session extends BaseResource {

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
			@FormParam("fb_permission") String fb_permissions, @QueryParam("isFacebookLogin") boolean isFacebookLogin,
			@FormParam("deviceId") String deviceId) {

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
						HtmlEmail newemail = new HtmlEmail();
						newemail.setHostName("smtp.gmail.com");
						newemail.setSmtpPort(465);
						newemail.setAuthenticator(new DefaultAuthenticator(CommonLib.BAPP_ID, CommonLib.BAPP_PWD));
						newemail.setSSLOnConnect(true);
						newemail.setFrom("hello@baatna.com");
						newemail.setSubject("Welcome to your local Baatna Community!!");
						newemail.setHtmlMsg(
								"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">  <html xmlns=\"http://www.w3.org/1999/xhtml\">      <head>          <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />                  </head>      <body yahoo bgcolor=\"#f1f2f2\" style=\"margin: 0;           padding: 0;            min-width: 100%\">          <table width=\"100%\" align=\"center\" bgcolor=\"#f1f2f2\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">              <tr>                  <td>                       <table style=\" width: 100%; max-width: 600px;\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">                          <tr bgcolor=\"#f1f2f2\">                              <td style=\"padding: 50px 50px 0px 50px;\"></td>                          </tr>                                                   <tr bgcolor=\"white\" >                              <td  style=\"padding: 0 50px 0px 50px;\" align=\"center\">                                  <a href=\"http://www.baatna.com\">                                       <img src=\"https://s3-ap-southeast-1.amazonaws.com/www.baatna.com/baatna.jpg\" align=\"middle\" height=\"100px\" width=\"100px\" border=\"0\" alt=\"www.baatna.com\" >                                   </a>                                  <p style=\"font-weight:800; text-align: center;color: black; font-family:Helvetica;font-size: 20px;font-weight: normal;line-height: 1.5\">Welcome To Baatna</p>                                  <p style=\"font-weight:800; text-align: center;color: grey; font-family:Helvetica;font-size: 14px;font-weight: normal;line-height: 1.5; margin-bottom:50px\"> We thank you for becoming a member of our<br>                                      local Baatna community!</p>                                  <img src=\"https://s3-ap-southeast-1.amazonaws.com/www.baatna.com/baatna_email2.jpg\" alt=\"http://baatna.com\"  width=60%>                                  <p style=\"font-weight:800; text-align: center;color: grey; font-family:Helvetica;font-size: 14px;font-weight: normal;line-height: 1.5; margin-bottom:50px\">Baatna helps you to borrow things you need from people around you.<br>                                   Right here,right now,for free</p>                              </td>                                 </tr>                      </table>                      <table style=\" width: 100%; max-width: 600px;\" align=\"center\" bgcolor=\"white\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">                          <col width=\"37.5%\">                          <col width=\"25%\">                          <col width=\"37.5%\">                          <tr bgcolor=\"white\" height=\"60px\">                               <td ></td>                                          <td bgcolor=\"#0ED7A3\" align=\"center\" style=\"border-radius:8px; font-family:Helvetica;font-size: 14px;\" ><a href=\"http://www.baatna.com\" style=\"text-decoration:none; color:white;\" ><b>Learn More</b></a></td>                                  <td ></td>                          </tr>                          <tr  bgcolor=\"white\">                          <td colspan=\"3\" style=\"padding: 0px 50px 50px 50px;\"></td>                          </tr>                      </table>                      <table style=\" width: 100%; max-width: 600px;\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">                          <tr bgcolor=\"#f1f2f2\">                              <td style=\"padding: 50px 50px 0px 50px;\"></td>                          </tr>                          <tr bgcolor=\"white\">                              <td style=\"padding: 50px 50px 0px 50px;\" align=\"center\">                                  <p style=\"font-weight:800; text-align: center;color: black; font-family:Helvetica;font-size: 20px;font-weight: normal;line-height: 1.5\">How does it works?</p>                                  <p style=\"font-weight:800; text-align: center;color: grey; font-family:Helvetica;font-size: 14px;font-weight: normal;line-height: 1.5; margin-bottom:50px\"> You know those moments when you need something that you do not own?<br>Tell Baatna what you are looking for and we'll find<br>friendly forlks willing to lend it to you.</p>                                  <p style=\"font-weight:800; text-align: center;color: grey; font-family:Helvetica;font-size: 14px;font-weight: normal;line-height: 1.5; margin-bottom:50px\"> Looking for something right now?</p></td>                          </tr>                      </table>                      <table style=\" width: 100%; max-width: 600px;\" align=\"center\" bgcolor=\"white\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">                      <col width=\"37.5%\">                      <col width=\"25%\">                       <col width=\"37.5%\">                              <tr bgcolor=\"white\" border-radius:\"8px\" height=\"60px\">                                  <td style=\"padding: 0px 50px 0px 50px;\"></td>                                             <td bgcolor=\"#0ED7A3\" align=\"center\" style=\"border-radius:8px; font-family:Helvetica;font-size: 14px;\" ><a href=\"http://www.baatna.com\" style=\"text-decoration:none; color:white;\"><a href=\"https://play.google.com/store/apps/details?id=com.application.baatna\" style=\"text-decoration:none; color:white\"><b>Post your need</b></a></td>                                   <td style=\"padding: 0 50px 0px 50px;\">                             </tr>                               <tr  bgcolor=\"white\">                                    <td colspan=\"3\" style=\"padding: 0px 50px 50px 50px;\"></td>                               </tr>                      </table>                      <table style=\" width: 100%; max-width: 600px;\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">                              <tr bgcolor=\"#f1f2f2\">                                   <td style=\"padding: 50px 50px 0px 50px;\"></td>                              </tr>                              <tr bgcolor=\"white\">                                  <td style=\"padding: 50px 50px 0px 50px;\" align=\"center\">                                  <p style=\"font-weight:800; text-align: center;color: black; font-family:Helvetica;font-size: 20px;font-weight: normal;line-height: 1.5\">Help others out</p>                                   <p style=\"font-weight:800; text-align: center;color: grey; font-family:Helvetica;font-size: 14px;font-weight: normal;line-height: 1.5; margin-bottom:50px\">Share your stuff when it's convenient.If someone around you is looking<br>something, we will let you know. It's up to you if you want to lend out<br>your stuff. Be an awesome neighbour and share the love! <br>                                  </td>                              </tr>                      </table>                      <table style=\" width: 100%; max-width: 600px;\" align=\"center\" bgcolor=\"white\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">                              <col width=\"37.5%\">                               <col width=\"25%\">                                <col width=\"37.5%\">                                  <tr bgcolor=\"white\" border-radius:\"8px\" height=\"60px\">                                       <td style=\"padding: 0px 50px 0 50px;\"></td>                                                 <td bgcolor=\"#0ED7A3\" align=\"center\" style=\"border-radius:8px; font-family:Helvetica;font-size: 14px;\" ><a href=\"http://www.baatna.com\" style=\"text-decoration:none; color:white;\"><a href=\"https://play.google.com/store/apps/details?id=com.application.baatna\" style=\"text-decoration:none; color:white;\"><b>Fulfill your need</b></a></td>                                      <td style=\"padding: 0 50px 0 50px;\">                                </tr>                              <tr  bgcolor=\"white\">                                      <td colspan=\"3\" style=\"padding: 0px 50px 0px 50px;\"></td>                              </tr>                      </table>                        <table style=\" width: 100%; max-width: 600px;\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">                      <tr bgcolor=\"white\">                              <td style=\"padding: 50px 50px 50px 50px;\" align=\"center\">                                  <p style=\"font-weight:800; text-align: center;color: grey; font-family:Helvetica;font-size: 14px;font-weight: normal;line-height: 1.5; margin-bottom:50px\">We're doing our best to make Baatna more efficient and useful for you everyday.Incase you have any feedback or query, or would just like to buy us cofee,please write to us at<br></p>                                   <p style=\"color: springGreen;text-align:center;font-weight: bold;text-decoration: underline;font-size: 20px;line-height: 1.5\"><a href=\"hello@baatna.com\" style=\"color:#0ED7A3;\">hello@baatna.com</a></p></td>                               </tr>                              <tr bgcolor=\"#f1f2f2\">                                      <td style=\"padding: 50px 50px 0 50px;\" align=\"center\">                                          <a href=\"https://www.facebook.com/baatna/?fref=ts\">                                              <img src=\"https://s3-ap-southeast-1.amazonaws.com/www.baatna.com/facebook1.png\" alt=\"https://www.facebook.com/baatna/?fref=ts\" style=\"margin: 0px 5px\" width=5%></a>                                          <a href=\"https://twitter.com/BaatnaCommunity\"><img src=\"https://s3-ap-southeast-1.amazonaws.com/www.baatna.com/twitter1.png\" alt=\"https://twitter.com/BaatnaCommunity\"  style=\"margin: 0px 5px\" width=5%></a>                                          <p style=\"font-weight:800; text-align: center;color: grey; font-family:Helvetica;font-size: 14px;font-weight: normal;line-height: 1.5; margin-bottom:50px\"> <a href=\"http://baatna.com\" style=\"text-decoration:none; color:black;\">www.<b>baatna</b>.com</a></p>                                          <p style=\"font-weight:800; text-align: center;color: grey; font-family:Helvetica;font-size: 14px;font-weight: normal;line-height: 1.5; margin-bottom:50px\"></p>                                      </td>                               </tr>                      </table>                  </td>              </tr>          </table>      </body>  </html>");
						newemail.addTo(email);
						newemail.send();
					} catch (EmailException e) {
						e.printStackTrace();
					}
					FeedDAO feedDao = new FeedDAO();
					boolean returnFeedResult = feedDao.addFeedItem(FeedDAO.USER_JOINED, System.currentTimeMillis(),
							user.getUserId(), -1, -1);
					if (returnFeedResult) {
						System.out.println("Success type 1");
					} else {
						System.out.println("Failure type 1");
					}

					EmailModel randomShitMail = new EmailModel();
					randomShitMail.setTo("founders@baatna.com");
					randomShitMail.setFrom(CommonLib.BAPP_ID);
					randomShitMail.setSubject("Someone just joined!!");
					randomShitMail.setContent("Hey," + "\n\n " + user.getEmail() + " Just Joined Baatna."
							+ "\n\n,Good Going" + "\nBaatna Team");
					EmailUtil.getInstance().sendEmail(randomShitMail);
				}
			}
		}

		if (user == null || user.getUserId() <= 0)
			return CommonLib.getResponseString("Error", "Some error occured", CommonLib.RESPONSE_INVALID_PARAMS);

		// email verification check
		int status = CommonLib.RESPONSE_SUCCESS;
		// : CommonLib.RESPONSE_INVALID_USER;

		// Generate Access Token
		Object[] tokens = userDao.generateAccessToken(user.getUserId(), deviceId);
		String accessToken = (String) tokens[0];
		boolean exists = (Boolean) tokens[1];

		// TODO: send the complete user object in json
		Location location = new Location(latitude, longitude);
		boolean sessionAdded = false;
		if (!exists) {
			sessionAdded = userDao.addSession(user.getUserId(), accessToken, regId, location, deviceId);
		}
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

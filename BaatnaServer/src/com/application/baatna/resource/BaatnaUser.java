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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.application.baatna.bean.Institution;
import com.application.baatna.bean.User;
import com.application.baatna.dao.FeedDAO;
import com.application.baatna.dao.UserDAO;
import com.application.baatna.util.CommonLib;
import com.application.baatna.util.CryptoHelper;
import com.application.baatna.util.JsonUtil;
//import com.application.baatna.util.BaatnaExceptionHandler;
//import com.application.baatna.util.mailer.DOCTYPE;
import com.application.baatna.util.mailer.EmailModel;
import com.application.baatna.util.mailer.EmailUtil;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("/user")
public class BaatnaUser extends BaseResource {

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
				/*
				 * EmailModel emailModel = new EmailModel();
				 * emailModel.setTo(email);
				 * emailModel.setFrom(CommonLib.BAPP_ID); emailModel.setSubject(
				 * "Welcome to your local Baatna Community!!");
				 * /*emailModel.setContent( "Hey," +
				 * "\n\nWelcome to Baatna! Thank you for becoming a member of the local Baatna community!"
				 * +
				 * "\n\nBaatna enables you to borrow the things you need from people in your neighborhood. Right here, right now, for free."
				 * + "\n\nHow does it work?" +
				 * "\n\nYou know those moments where you need to use something that you do not need to own? Tell Baatna what you are looking for and we'll find friendly neighbors willing to lend it to you. Looking for something right now? Just go to the app and post your need."
				 * +
				 * "\n\nIn return you can share your stuff when it's convenient. If one of your neighbors is looking for something, we will let you know. It's up to you if you want to lend out your stuff. Be an awesome neighbor and share the love!"
				 * +
				 * "\n\nWe're doing our best to make Baatna more efficient and useful for you everyday. Incase you have any feedback, please get back to us at -hello@baatna.com."
				 * + "\nWe would love to hear from you." + "\n\nCheers" +
				 * "\nBaatna Team");
				 */
				// emailModel.setHtmlContent("<!DOCTYPE html PUBLIC
				// \"-//W3C//DTD XHTML 1.0 Transitional//EN\"
				// \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html
				// xmlns=\"http://www.w3.org/1999/xhtml\"> <head> <meta
				// http-equiv=\"Content-Type\" content=\"text/html;
				// charset=utf-8\" /> <style type=\"text/css\">body {margin: 0;
				// padding: 0; min-width: 100%!important;} .content { width:
				// 100%; max-width: 600px; } .main{ font-weight:800; text-align:
				// center; color: black; font-family: \"Helvetica Neue
				// Bold\",\"Helvetica\", \"sans-serif\"; font-size: 20px;
				// font-weight: normal; line-height: 1.5;} .text{
				// text-decoration:none; text-align: center; color: grey;
				// font-family: \"Helvetica Neue Light\",
				// \"Helvetica\",\"sans-serif\"; font-size: 14px; font-weight:
				// normal; line-height: 1.5; margin-bottom: 50px;} .link{color:
				// springGreen; text-align:center; font-weight: bold;
				// text-decoration: underline; font-size: 20px; line-height:
				// 1.5;} .button_class{ text-align: center; font-family:
				// \"Helvetica Neue Light\", \"Helvetica\",\"sans-serif\";
				// background-clip: padding-box; /* this has been added */
				// border-radius: 8px; font-size: 14px; background-color:
				// #0ED7A3; color: black;} </style> </head><body yahoo
				// bgcolor=\"#f1f2f2\"> <table width=\"100%\" align=\"center\"
				// bgcolor=\"#f1f2f2\" border=\"0\" cellpadding=\"0\"
				// cellspacing=\"0\"><tr> <td><table class=\"content\"
				// align=\"center\" cellpadding=\"0\" cellspacing=\"0\"
				// border=\"0\"><tr bgcolor=\"#f1f2f2\"><td style=\"padding:
				// 50px 50px 0px 50px;\"></td></tr> <tr bgcolor=\"white\" ><td
				// style=\"padding: 0 50px 0px 50px;\" align=\"center\"><a
				// href=\"http://www.baatna.com\"> <img
				// src=\"https://s3-ap-southeast-1.amazonaws.com/www.baatna.com/baatna.jpg\"
				// align=\"middle\" height=\"100px\" width=\"100px\"
				// border=\"0\" alt=\"www.baatna.com\" > </a>"+
				// "<p class=\"main\">Welcome To Baatna</p> <p class=\"text\">
				// We thank you for becoming a member of of our<br> local Baatna
				// community!</p> <img
				// src=\"https://s3-ap-southeast-1.amazonaws.com/www.baatna.com/baatna_email2.jpg\"
				// alt=\"http://baatna.com\" width=60%><p class=\"text\">Baatna
				// helps you to borrow things you need from people around
				// you.<br> Right here,right now,for free</p> </td></tr>
				// </table> <table class=\"content\" align=\"center\"
				// bgcolor=\"white\" cellpadding=\"0\" cellspacing=\"0\"
				// border=\"0\"><col width=\"25%\"> <col width=\"37.5%\"><tr
				// bgcolor=\"white\" height=\"60px\"><td ></td> <td
				// class=\"button_class\" ><a href=\"http://www.baatna.com\"
				// style=\"text-decoration:none; color:white;\" ><b>Learn
				// More</b></a></td><td ></td></tr> <tr bgcolor=\"white\"> <td
				// colspan=\"3\" style=\"padding: 0px 50px 50px
				// 50px;\"></td></tr> </table> <table class=\"content\"
				// align=\"center\" cellpadding=\"0\" cellspacing=\"0\"
				// border=\"0\"> <tr bgcolor=\"#f1f2f2\"> <td style=\"padding:
				// 50px 50px 0px 50px;\"></td> </tr> <tr bgcolor=\"white\"> <td
				// style=\"padding: 50px 50px 0px 50px;\" align=\"center\"> <p
				// class=\"main\">How does it works?</p> <p class=\"text\"> You
				// know those moments when you need something that you do not
				// own?<br>Tell Baatna what you are looking for and we'll
				// find<br>friendly forlks willing to lend it to you.</p> <p
				// class=\"text\"> Looking for something right now?</p></td>
				// </tr> </table> <table class=\"content\" align=\"center\"
				// bgcolor=\"white\" cellpadding=\"0\" cellspacing=\"0\"
				// border=\"0\"> <col width=\"37.5%\"> <col width=\"25%\"> <col
				// width=\"37.5%\"> <tr bgcolor=\"white\" border-radius:\"8px\"
				// height=\"60px\"> <td style=\"padding: 0px 50px 0px
				// 50px;\"></td><td class=\"button_class\"><a
				// href=\"https://play.google.com/store/apps/developer?id=Baatna\"
				// style=\"text-decoration:none; color:white\"><b>Post your
				// need</b></a></td><td style=\"padding: 0 50px 0px 50px;\">
				// </tr> <tr bgcolor=\"white\"><td colspan=\"3\"
				// style=\"padding: 0px 50px 50px 50px;\"></td> </tr> </table>
				// <table class=\"content\" align=\"center\" cellpadding=\"0\"
				// cellspacing=\"0\" border=\"0\"> <tr bgcolor=\"#f1f2f2\"> <td
				// style=\"padding: 50px 50px 0px 50px;\"></td> </tr> <tr
				// bgcolor=\"white\"><td style=\"padding: 50px 50px 0px 50px;\"
				// align=\"center\"><p class=\"main\">Help others out</p> <p
				// class=\"text\">Share your stuff when it's convenient.If
				// someone around you is looking<br>something, we will let you
				// know. It's up to you if you want to lend out<br>your stuff.
				// Be an awesome neighbour and share the love! <br> </td> </tr>
				// </table> <table class=\"content\" align=\"center\"
				// bgcolor=\"white\" cellpadding=\"0\" cellspacing=\"0\"
				// border=\"0\"> <col width=\"37.5%\"> <col width=\"25%\"> <col
				// width=\"37.5%\"> <tr bgcolor=\"white\" border-radius:\"8px\"
				// height=\"60px\"> <td style=\"padding: 0px 50px 0
				// 50px;\"></td> <td class=\"button_class\"><a
				// href=\"https://play.google.com/store/apps/developer?id=Baatna\"
				// style=\"text-decoration:none; color:white;\"><b>Fulfill your
				// need</b></a></td><td style=\"padding: 0 50px 0 50px;\"> </tr>
				// <tr bgcolor=\"white\"> <td colspan=\"3\" style=\"padding: 0px
				// 50px 0px 50px;\"></td> </tr> </table><table class=\"content\"
				// align=\"center\" cellpadding=\"0\" cellspacing=\"0\"
				// border=\"0\"> <tr bgcolor=\"white\"> <td style=\"padding:
				// 50px 50px 50px 50px;\" align=\"center\"> <p
				// class=\"text\">We're doing our best to make Baatna more
				// efficient and useful for you everyday.<br>Incase you have any
				// feedback or query, or would just like to buy us
				// cofee,<br>please write to us at<br></p> <p class=\"link\"><a
				// href=\"hello@baatna.com\"
				// style=\"color:#0ED7A3;\">hello@baatna.com</a></p></td> </tr>
				// <tr bgcolor=\"#f1f2f2\"><td style=\"padding: 50px 50px 0
				// 50px;\" align=\"center\"><a
				// href=\"https://www.facebook.com/baatna/?fref=ts\"><img
				// src=\"https://s3-ap-southeast-1.amazonaws.com/www.baatna.com/facebook1.png\"
				// alt=\"https://www.facebook.com/baatna/?fref=ts\"
				// style=\"margin: 0px 5px\" width=5%></a> <a
				// href=\"https://twitter.com/BaatnaCommunity\"><img
				// src=\"https://s3-ap-southeast-1.amazonaws.com/www.baatna.com/twitter1.png\"
				// alt=\"https://twitter.com/BaatnaCommunity\" style=\"margin:
				// 0px 5px\" width=5%></a> <p class=\"text\"> <a
				// href=\"http://baatna.com\" style=\"text-decoration:none;
				// color:black;\">www.<b>baatna</b>.com</a></p><p
				// class=\"text\">Unsubscribe from emails</p>
				// </td></tr></table></td></tr> </table></body>
				// </html>","text/html");

				// EmailUtil.getInstance().sendEmail(emailModel);
				// TODO: send a push to nearby users.

				try {
					HtmlEmail newemail = new HtmlEmail();
					newemail.setHostName("smtp.gmail.com");
					newemail.setSmtpPort(465);
					newemail.setAuthenticator(new DefaultAuthenticator(CommonLib.BAPP_ID, CommonLib.BAPP_PWD));
					newemail.setSSLOnConnect(true);
					newemail.setFrom("hello@baatna.com");
					newemail.setSubject("Welcome to your local Baatna Community!!");
					// newemail.setHtmlMsg("<!DOCTYPE html PUBLIC \"-//W3C//DTD
					// XHTML 1.0 Transitional//EN\"
					// \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html
					// xmlns=\"http://www.w3.org/1999/xhtml\"> <head> <meta
					// http-equiv=\"Content-Type\" content=\"text/html;
					// charset=utf-8\" /> <style type=\"text/css\">body {margin:
					// 0; padding: 0; min-width: 100%!important;} .content {
					// width: 100%; max-width: 600px; } .main{ font-weight:800;
					// text-align: center; color: black; font-family:
					// \"Helvetica Neue Bold\",\"Helvetica\", \"sans-serif\";
					// font-size: 20px; font-weight: normal; line-height: 1.5;}
					// .text{ text-decoration:none; text-align: center; color:
					// grey; font-family: \"Helvetica Neue Light\",
					// \"Helvetica\",\"sans-serif\"; font-size: 14px;
					// font-weight: normal; line-height: 1.5; margin-bottom:
					// 50px;} .link{color: springGreen; text-align:center;
					// font-weight: bold; text-decoration: underline; font-size:
					// 20px; line-height: 1.5;} .button_class{ text-align:
					// center; font-family: \"Helvetica Neue Light\",
					// \"Helvetica\",\"sans-serif\"; background-clip:
					// padding-box; /* this has been added */ border-radius:
					// 8px; font-size: 14px; background-color: #0ED7A3; color:
					// black;} </style> </head><body yahoo bgcolor=\"#f1f2f2\">
					// <table width=\"100%\" align=\"center\"
					// bgcolor=\"#f1f2f2\" border=\"0\" cellpadding=\"0\"
					// cellspacing=\"0\"><tr> <td><table class=\"content\"
					// align=\"center\" cellpadding=\"0\" cellspacing=\"0\"
					// border=\"0\"><tr bgcolor=\"#f1f2f2\"><td style=\"padding:
					// 50px 50px 0px 50px;\"></td></tr> <tr bgcolor=\"white\"
					// ><td style=\"padding: 0 50px 0px 50px;\"
					// align=\"center\"><a href=\"http://www.baatna.com\"> <img
					// src=\"https://s3-ap-southeast-1.amazonaws.com/www.baatna.com/baatna.jpg\"
					// align=\"middle\" height=\"100px\" width=\"100px\"
					// border=\"0\" alt=\"www.baatna.com\" > </a>"+
					// "<p class=\"main\">Welcome To Baatna</p> <p
					// class=\"text\"> We thank you for becoming a member of of
					// our<br> local Baatna community!</p> <img
					// src=\"https://s3-ap-southeast-1.amazonaws.com/www.baatna.com/baatna_email2.jpg\"
					// alt=\"http://baatna.com\" width=60%><p
					// class=\"text\">Baatna helps you to borrow things you need
					// from people around you.<br>Right here,right now,for
					// free</p> </td></tr> </table> <table class=\"content\"
					// align=\"center\" bgcolor=\"white\" cellpadding=\"0\"
					// cellspacing=\"0\" border=\"0\"><col width=\"25%\"><col
					// width=\"37.5%\"><tr bgcolor=\"white\" height=\"60px\"><td
					// ></td><td class=\"button_class\" ><a
					// href=\"http://www.baatna.com\"
					// style=\"text-decoration:none; color:white;\" ><b> Learn
					// More </b></a></td><td ></td></tr> <tr bgcolor=\"white\">
					// <td colspan=\"3\" style=\"padding: 0px 50px 50px
					// 50px;\"></td></tr> </table> <table class=\"content\"
					// align=\"center\" cellpadding=\"0\" cellspacing=\"0\"
					// border=\"0\"> <tr bgcolor=\"#f1f2f2\"> <td
					// style=\"padding: 50px 50px 0px 50px;\"></td> </tr> <tr
					// bgcolor=\"white\"> <td style=\"padding: 50px 50px 0px
					// 50px;\" align=\"center\"> <p class=\"main\">How does it
					// works?</p> <p class=\"text\"> You know those moments when
					// you need something that you do not own?<br>Tell Baatna
					// what you are looking for and we'll find<br>friendly
					// forlks willing to lend it to you.</p> <p class=\"text\">
					// Looking for something right now?</p></td> </tr> </table>
					// <table class=\"content\" align=\"center\"
					// bgcolor=\"white\" cellpadding=\"0\" cellspacing=\"0\"
					// border=\"0\"> <col width=\"37.5%\"> <col width=\"25%\">
					// <col width=\"37.5%\"> <tr bgcolor=\"white\"
					// border-radius:\"8px\" height=\"60px\"><td
					// style=\"padding: 0px 50px 0px 50px;\"></td><td
					// class=\"button_class\"><a
					// href=\"https://play.google.com/store/apps/developer?id=Baatna\"
					// style=\"text-decoration:none; color:white\"><b>Post your
					// need</b></a></td><td style=\"padding: 0 50px 0px 50px;\">
					// </tr> <tr bgcolor=\"white\"><td colspan=\"3\"
					// style=\"padding: 0px 50px 50px 50px;\"></td></tr>
					// </table> <table class=\"content\" align=\"center\"
					// cellpadding=\"0\" cellspacing=\"0\" border=\"0\"> <tr
					// bgcolor=\"#f1f2f2\"> <td style=\"padding: 50px 50px 0px
					// 50px;\"></td></tr> <tr bgcolor=\"white\"><td
					// style=\"padding: 50px 50px 0px 50px;\"
					// align=\"center\"><p class=\"main\">Help others out</p> <p
					// class=\"text\">Share your stuff when it's convenient.If
					// someone around you is looking<br>something, we will let
					// you know. It's up to you if you want to lend out<br>your
					// stuff. Be an awesome neighbour and share the love! <br>
					// </td> </tr> </table><table class=\"content\"
					// align=\"center\" bgcolor=\"white\" cellpadding=\"0\"
					// cellspacing=\"0\" border=\"0\"> <col width=\"37.5%\">
					// <col width=\"25%\"> <col width=\"37.5%\"><tr
					// bgcolor=\"white\" border-radius:\"8px\" height=\"60px\">
					// <td style=\"padding: 0px 50px 0 50px;\"></td> <td
					// class=\"button_class\"><a
					// href=\"https://play.google.com/store/apps/developer?id=Baatna\"
					// style=\"text-decoration:none; color:white;\"><b> Fulfill
					// your need </b></a></td><td style=\"padding: 0 50px 0
					// 50px;\"></tr> <tr bgcolor=\"white\"> <td colspan=\"3\"
					// style=\"padding: 0px 50px 0px 50px;\"></td></tr>
					// </table><table class=\"content\" align=\"center\"
					// cellpadding=\"0\" cellspacing=\"0\" border=\"0\"> <tr
					// bgcolor=\"white\"> <td style=\"padding: 50px 50px 50px
					// 50px;\" align=\"center\"><p class=\"text\">We're doing
					// our best to make Baatna more efficient and useful for you
					// everyday.<br>Incase you have any feedback or query, or
					// would just like to buy us cofee,<br>please write to us
					// at<br></p> <p class=\"link\"><a href=\"hello@baatna.com\"
					// style=\"color:#0ED7A3;\">hello@baatna.com</a></p></td>
					// </tr> <tr bgcolor=\"#f1f2f2\"><td style=\"padding: 50px
					// 50px 0 50px;\" align=\"center\"><a
					// href=\"https://www.facebook.com/baatna/?fref=ts\"><img
					// src=\"https://s3-ap-southeast-1.amazonaws.com/www.baatna.com/facebook1.png\"
					// alt=\"https://www.facebook.com/baatna/?fref=ts\"
					// style=\"margin: 0px 5px\" width=5%></a><a
					// href=\"https://twitter.com/BaatnaCommunity\"><img
					// src=\"https://s3-ap-southeast-1.amazonaws.com/www.baatna.com/twitter1.png\"
					// alt=\"https://twitter.com/BaatnaCommunity\"
					// style=\"margin: 0px 5px\" width=5%></a> <p
					// class=\"text\"> <a href=\"http://baatna.com\"
					// style=\"text-decoration:none;
					// color:black;\">www.<b>baatna</b>.com</a></p><p
					// class=\"text\">Unsubscribe from emails</p>
					// </td></tr></table></td></tr> </table></body> </html>");
					newemail.setHtmlMsg(
							"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">  <html xmlns=\"http://www.w3.org/1999/xhtml\">      <head>          <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />                  </head>      <body yahoo bgcolor=\"#f1f2f2\" style=\"margin: 0;           padding: 0;            min-width: 100%\">          <table width=\"100%\" align=\"center\" bgcolor=\"#f1f2f2\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">              <tr>                  <td>                       <table style=\" width: 100%; max-width: 600px;\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">                          <tr bgcolor=\"#f1f2f2\">                              <td style=\"padding: 50px 50px 0px 50px;\"></td>                          </tr>                                                   <tr bgcolor=\"white\" >                              <td  style=\"padding: 0 50px 0px 50px;\" align=\"center\">                                  <a href=\"http://www.baatna.com\">                                       <img src=\"https://s3-ap-southeast-1.amazonaws.com/www.baatna.com/baatna.jpg\" align=\"middle\" height=\"100px\" width=\"100px\" border=\"0\" alt=\"www.baatna.com\" >                                   </a>                                  <p style=\"font-weight:800; text-align: center;color: black; font-family:Helvetica;font-size: 20px;font-weight: normal;line-height: 1.5\">Welcome To Baatna</p>                                  <p style=\"font-weight:800; text-align: center;color: grey; font-family:Helvetica;font-size: 14px;font-weight: normal;line-height: 1.5; margin-bottom:50px\"> We thank you for becoming a member of our<br>                                      local Baatna community!</p>                                  <img src=\"https://s3-ap-southeast-1.amazonaws.com/www.baatna.com/baatna_email2.jpg\" alt=\"http://baatna.com\"  width=60%>                                  <p style=\"font-weight:800; text-align: center;color: grey; font-family:Helvetica;font-size: 14px;font-weight: normal;line-height: 1.5; margin-bottom:50px\">Baatna helps you to borrow things you need from people around you.<br>                                   Right here,right now,for free</p>                              </td>                                 </tr>                      </table>                      <table style=\" width: 100%; max-width: 600px;\" align=\"center\" bgcolor=\"white\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">                          <col width=\"37.5%\">                          <col width=\"25%\">                          <col width=\"37.5%\">                          <tr bgcolor=\"white\" height=\"60px\">                               <td ></td>                                          <td bgcolor=\"#0ED7A3\" align=\"center\" style=\"border-radius:8px; font-family:Helvetica;font-size: 14px;\" ><a href=\"http://www.baatna.com\" style=\"text-decoration:none; color:white;\" ><b>Learn More</b></a></td>                                  <td ></td>                          </tr>                          <tr  bgcolor=\"white\">                          <td colspan=\"3\" style=\"padding: 0px 50px 50px 50px;\"></td>                          </tr>                      </table>                      <table style=\" width: 100%; max-width: 600px;\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">                          <tr bgcolor=\"#f1f2f2\">                              <td style=\"padding: 50px 50px 0px 50px;\"></td>                          </tr>                          <tr bgcolor=\"white\">                              <td style=\"padding: 50px 50px 0px 50px;\" align=\"center\">                                  <p style=\"font-weight:800; text-align: center;color: black; font-family:Helvetica;font-size: 20px;font-weight: normal;line-height: 1.5\">How does it works?</p>                                  <p style=\"font-weight:800; text-align: center;color: grey; font-family:Helvetica;font-size: 14px;font-weight: normal;line-height: 1.5; margin-bottom:50px\"> You know those moments when you need something that you do not own?<br>Tell Baatna what you are looking for and we'll find<br>friendly forlks willing to lend it to you.</p>                                  <p style=\"font-weight:800; text-align: center;color: grey; font-family:Helvetica;font-size: 14px;font-weight: normal;line-height: 1.5; margin-bottom:50px\"> Looking for something right now?</p></td>                          </tr>                      </table>                      <table style=\" width: 100%; max-width: 600px;\" align=\"center\" bgcolor=\"white\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">                      <col width=\"37.5%\">                      <col width=\"25%\">                       <col width=\"37.5%\">                              <tr bgcolor=\"white\" border-radius:\"8px\" height=\"60px\">                                  <td style=\"padding: 0px 50px 0px 50px;\"></td>                                             <td bgcolor=\"#0ED7A3\" align=\"center\" style=\"border-radius:8px; font-family:Helvetica;font-size: 14px;\" ><a href=\"http://www.baatna.com\" style=\"text-decoration:none; color:white;\"><a href=\"https://play.google.com/store/apps/developer?id=Baatna\" style=\"text-decoration:none; color:white\"><b>Post your need</b></a></td>                                   <td style=\"padding: 0 50px 0px 50px;\">                             </tr>                               <tr  bgcolor=\"white\">                                    <td colspan=\"3\" style=\"padding: 0px 50px 50px 50px;\"></td>                               </tr>                      </table>                      <table style=\" width: 100%; max-width: 600px;\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">                              <tr bgcolor=\"#f1f2f2\">                                   <td style=\"padding: 50px 50px 0px 50px;\"></td>                              </tr>                              <tr bgcolor=\"white\">                                  <td style=\"padding: 50px 50px 0px 50px;\" align=\"center\">                                  <p style=\"font-weight:800; text-align: center;color: black; font-family:Helvetica;font-size: 20px;font-weight: normal;line-height: 1.5\">Help others out</p>                                   <p style=\"font-weight:800; text-align: center;color: grey; font-family:Helvetica;font-size: 14px;font-weight: normal;line-height: 1.5; margin-bottom:50px\">Share your stuff when it's convenient.If someone around you is looking<br>something, we will let you know. It's up to you if you want to lend out<br>your stuff. Be an awesome neighbour and share the love! <br>                                  </td>                              </tr>                      </table>                      <table style=\" width: 100%; max-width: 600px;\" align=\"center\" bgcolor=\"white\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">                              <col width=\"37.5%\">                               <col width=\"25%\">                                <col width=\"37.5%\">                                  <tr bgcolor=\"white\" border-radius:\"8px\" height=\"60px\">                                       <td style=\"padding: 0px 50px 0 50px;\"></td>                                                 <td bgcolor=\"#0ED7A3\" align=\"center\" style=\"border-radius:8px; font-family:Helvetica;font-size: 14px;\" ><a href=\"http://www.baatna.com\" style=\"text-decoration:none; color:white;\"><a href=\"https://play.google.com/store/apps/developer?id=Baatna\" style=\"text-decoration:none; color:white;\"><b>Fulfill your need</b></a></td>                                      <td style=\"padding: 0 50px 0 50px;\">                                </tr>                              <tr  bgcolor=\"white\">                                      <td colspan=\"3\" style=\"padding: 0px 50px 0px 50px;\"></td>                              </tr>                      </table>                        <table style=\" width: 100%; max-width: 600px;\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">                      <tr bgcolor=\"white\">                              <td style=\"padding: 50px 50px 50px 50px;\" align=\"center\">                                  <p style=\"font-weight:800; text-align: center;color: grey; font-family:Helvetica;font-size: 14px;font-weight: normal;line-height: 1.5; margin-bottom:50px\">We're doing our best to make Baatna more efficient and useful for you everyday.Incase you have any feedback or query, or would just like to buy us cofee,please write to us at<br></p>                                   <p style=\"color: springGreen;text-align:center;font-weight: bold;text-decoration: underline;font-size: 20px;line-height: 1.5\"><a href=\"hello@baatna.com\" style=\"color:#0ED7A3;\">hello@baatna.com</a></p></td>                               </tr>                              <tr bgcolor=\"#f1f2f2\">                                      <td style=\"padding: 50px 50px 0 50px;\" align=\"center\">                                          <a href=\"https://www.facebook.com/baatna/?fref=ts\">                                              <img src=\"https://s3-ap-southeast-1.amazonaws.com/www.baatna.com/facebook1.png\" alt=\"https://www.facebook.com/baatna/?fref=ts\" style=\"margin: 0px 5px\" width=5%></a>                                          <a href=\"https://twitter.com/BaatnaCommunity\"><img src=\"https://s3-ap-southeast-1.amazonaws.com/www.baatna.com/twitter1.png\" alt=\"https://twitter.com/BaatnaCommunity\"  style=\"margin: 0px 5px\" width=5%></a>                                          <p style=\"font-weight:800; text-align: center;color: grey; font-family:Helvetica;font-size: 14px;font-weight: normal;line-height: 1.5; margin-bottom:50px\"> <a href=\"http://baatna.com\" style=\"text-decoration:none; color:black;\">www.<b>baatna</b>.com</a></p>                                          <p style=\"font-weight:800; text-align: center;color: grey; font-family:Helvetica;font-size: 14px;font-weight: normal;line-height: 1.5; margin-bottom:50px\">Unsubscribe from emails</p>                                      </td>                               </tr>                      </table>                  </td>              </tr>          </table>      </body>  </html>");
					newemail.addTo(email);
					newemail.send();
				} catch (EmailException e) {
					e.printStackTrace();
				}

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
			@FormParam("access_token") String accessToken, @FormParam("latitude") double latitude,
			@FormParam("longitude") double longitude) {

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
			ArrayList<com.application.baatna.bean.Session> nearbyUsers = dao.getNearbyUsers(latitude, longitude);
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
	public JSONObject updateRating(@FormParam("client_id") String clientId, @FormParam("app_type") String appType,
			@FormParam("access_token") String accessToken, @FormParam("userId") int userId,
			@FormParam("rating") double rating, @FormParam("wish_id") int wishId,
			@FormParam("user_wish_id") int userWishId) {

		// null checks, invalid request
		if (clientId == null || appType == null)
			return CommonLib.getResponseString("Invalid params", "", CommonLib.RESPONSE_INVALID_PARAMS);

		// check for client_id
		if (!clientId.equals(CommonLib.ANDROID_CLIENT_ID))
			return CommonLib.getResponseString("Invalid client id", "", CommonLib.RESPONSE_INVALID_CLIENT_ID);

		// check for app type
		if (!appType.equals(CommonLib.ANDROID_APP_TYPE))
			return CommonLib.getResponseString("Invalid params", "", CommonLib.RESPONSE_INVALID_APP_TYPE);

		UserDAO dao = new UserDAO();
		int currentUserId = dao.userActive(accessToken);
		if (currentUserId > 0 && userId > 0) {
			boolean retValue = false;
			// boolean userCanRate = false;
			// userCanRate = dao.usersEverInteracted(currentUserId, userId);
			// if (userCanRate) {
			retValue = dao.setRatingForUser(currentUserId, userId, rating, wishId, userWishId);
			if (retValue)
				return CommonLib.getResponseString("success", "success", CommonLib.RESPONSE_SUCCESS);
			else
				return CommonLib.getResponseString("failure", "", CommonLib.RESPONSE_RATED_FAILURE);
			// } else {
			// return CommonLib.getResponseString("failure", "",
			// CommonLib.RESPONSE_RATED_FAILURE);
			// }
		}

		return CommonLib.getResponseString("failure", "", CommonLib.RESPONSE_RATED_FAILURE);
	}

	@Path("/viewrating")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public JSONObject viewRating(@FormParam("client_id") String clientId, @FormParam("app_type") String appType,
			@FormParam("access_token") String accessToken, @QueryParam("userId") int userToRateId) {

		// null checks, invalid request
		if (clientId == null || appType == null)
			return CommonLib.getResponseString("Invalid params", "", CommonLib.RESPONSE_INVALID_PARAMS);

		// check for client_id
		if (!clientId.equals(CommonLib.ANDROID_CLIENT_ID))
			return CommonLib.getResponseString("Invalid client id", "", CommonLib.RESPONSE_INVALID_CLIENT_ID);

		// check for app type
		if (!appType.equals(CommonLib.ANDROID_APP_TYPE))
			return CommonLib.getResponseString("Invalid params", "", CommonLib.RESPONSE_INVALID_APP_TYPE);

		UserDAO dao = new UserDAO();
		// check if user exists
		int currentUserId = dao.userActive(accessToken);
		if (currentUserId > 0) {
			Object[] ratingObjects = dao.getRating(currentUserId, userToRateId);
			try {
				JSONObject object = new JSONObject();
				try {
					object.put("rating_allowed", ratingObjects[0]);
					object.put("rating", ratingObjects[1]);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return CommonLib.getResponseString(object, "success", CommonLib.RESPONSE_SUCCESS);
			} catch (Exception e) {
				return CommonLib.getResponseString("failure", "failure", CommonLib.RESPONSE_RATED_FAILURE);
			}
		}
		return CommonLib.getResponseString("failure", "User does not exists or session is invalid",
				CommonLib.RESPONSE_RATED_FAILURE);

	}

	@Path("/block")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public JSONObject blockUser(@FormParam("client_id") String clientId, @FormParam("app_type") String appType,
			@FormParam("access_token") String accessToken, @FormParam("userId") int userId) {

		// null checks, invalid request
		if (clientId == null || appType == null)
			return CommonLib.getResponseString("Invalid params", "", CommonLib.RESPONSE_INVALID_PARAMS);

		// check for client_id
		if (!clientId.equals(CommonLib.ANDROID_CLIENT_ID))
			return CommonLib.getResponseString("Invalid client id", "", CommonLib.RESPONSE_INVALID_CLIENT_ID);

		// check for app type
		if (!appType.equals(CommonLib.ANDROID_APP_TYPE))
			return CommonLib.getResponseString("Invalid params", "", CommonLib.RESPONSE_INVALID_APP_TYPE);

		UserDAO dao = new UserDAO();
		boolean retVal = false;
		// check if user exists
		// User userExists = dao.getUserDetails(userId);
		int blockingUserId = dao.userActive(accessToken);
		if (blockingUserId > 0 && userId > 0) {
			retVal = dao.addUserToBlockedList(blockingUserId, userId);
		}
		if (retVal)
			return CommonLib.getResponseString("success", "success", CommonLib.RESPONSE_SUCCESS);
		else
			return CommonLib.getResponseString("failure", "user does not exist", CommonLib.RESPONSE_FAILURE);
	}

	// temporary test path
	@Path("/exception")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public JSONObject genException(@FormParam("abc") int abc) {

		throw new NullPointerException();
	}

}
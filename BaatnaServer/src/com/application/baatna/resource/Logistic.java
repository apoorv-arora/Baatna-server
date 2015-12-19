package com.application.baatna.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.application.baatna.bean.Logistics;
import com.application.baatna.bean.ReturnTable;
import com.application.baatna.bean.User;
import com.application.baatna.bean.UserWish;
import com.application.baatna.bean.Wish;
import com.application.baatna.dao.LogisticsDAO;
import com.application.baatna.dao.UserDAO;
import com.application.baatna.dao.WishDAO;
import com.application.baatna.util.CommonLib;
import com.application.baatna.util.DBUtil;
import com.application.baatna.util.JsonUtil;

@Path("/logistic")
public class Logistic{
	@Path("/delivery")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public JSONObject delivery(@FormParam("client_id") String clientId, 
			@FormParam("app_type") String appType,
			@FormParam("userId") int toUserId,
			@FormParam("access_token") String accessToken,
			@FormParam("wishid")int wishid,
			@FormParam("borrow_address") String borrow_address,
			@FormParam("phone_number") String phone_number,
			@FormParam("type") int type,
			@FormParam("delivery_time") long delivery_time){
		if (clientId == null || appType == null)
			return CommonLib.getResponseString("Invalid params", "", CommonLib.RESPONSE_INVALID_PARAMS);

		// check for client_id
		if (!clientId.equals(CommonLib.ANDROID_CLIENT_ID))
			return CommonLib.getResponseString("Invalid client id", "", CommonLib.RESPONSE_INVALID_CLIENT_ID);

		// check for app type
		if (!appType.equals(CommonLib.ANDROID_APP_TYPE))
			return CommonLib.getResponseString("Invalid params", "", CommonLib.RESPONSE_INVALID_APP_TYPE);


		UserDAO userDao = new UserDAO();
		int userId = userDao.userActive(accessToken);

		if(userId>0){
			int flag=0;

			LogisticsDAO logisticsdao=new LogisticsDAO();
			if(type==1){
				// printing message for self pickup
				WishDAO wishDao=new WishDAO();
				flag=logisticsdao.updateuserwish(toUserId,userId,wishid,type); 
				//String notification="Congratulatioin borrower choose for Self Pick up";
				//MessageDAO messageDao = new MessageDAO();
				//Message messageobj=messageDao.addMessage(notification,false,"" + System.currentTimeMillis(),userId,toUserId,wishid);
				JSONObject message=new JSONObject();
				if(flag>0){
					User toUser=userDao.getUserDetails(toUserId);
					User fromUser=userDao.getUserDetails(userId);
					Wish wish = wishDao.getWish(wishid);
					Logistics logistic=logisticsdao.addLogisticEntry(userId,toUserId,type,delivery_time);
					
					//Logistics logistic=logisticdao.getlogistic(toUserId,userId,wishid);
					try {
						
						message.put("to_user", JsonUtil.getUserJson(toUser));
						message.put("from_user", JsonUtil.getUserJson(fromUser));
						message.put("wish", JsonUtil.getWishJson(wish));
						message.put("logistic", JsonUtil.getLogisticJson(logistic));
						message.put("action_type",1);
						//message.put("notifiaction", notification);

					} catch (JSONException e) {
						e.printStackTrace();
					}
					logisticsdao.sendPushToAllSessions(message, toUserId);
					return CommonLib.getResponseString(message, "", CommonLib.RESPONSE_SUCCESS);
				}
				return CommonLib.getResponseString("failure", "", CommonLib.RESPONSE_FAILURE);
			}
			else if(type==2){
				flag=logisticsdao.updateuserwish(toUserId,userId,wishid,type);
				//String notification="borrower choose baatna delivery.please provide your address " +
				//		"and phone number and preffered pick up time";
				if(flag>0)
				{
					WishDAO wishDao=new WishDAO();
					int result=logisticsdao.update_userdetails(borrow_address,phone_number,userId);
					if(result>0)
					{
						Logistics logistic=logisticsdao.addLogisticEntry(userId,toUserId,type,delivery_time);
						JSONObject message=new JSONObject();

						User toUser=userDao.getUserDetails(toUserId);
						User fromUser=userDao.getUserDetails(userId);
						Wish wish = wishDao.getWish(wishid);
						//Logistics logistic=logisticdao.getlogistic(toUserId,userId,wishid);
						try {

							message.put("to_user", JsonUtil.getUserJson(toUser));
							message.put("from_user", JsonUtil.getUserJson(fromUser));
							message.put("wish", JsonUtil.getWishJson(wish));
							message.put("logistic", JsonUtil.getLogisticJson(logistic));
							message.put("action_type",2);
							//message.put("notifiaction", notification);

						} catch (JSONException e) {
							e.printStackTrace();
						}
						logisticsdao.sendPushToAllSessions(message, toUserId);
						return CommonLib.getResponseString(message, "", CommonLib.RESPONSE_SUCCESS);
					}
				}
				return CommonLib.getResponseString("failure", "", CommonLib.RESPONSE_FAILURE);
			}
			else
				return CommonLib.getResponseString("failure", "", CommonLib.RESPONSE_FAILURE);

		}
		return CommonLib.getResponseString("failure", "", CommonLib.RESPONSE_FAILURE);

	}

	@Path("/delivery_lender")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public JSONObject lender(@FormParam("client_id") String clientId, 
			@FormParam("app_type") String appType,
			@FormParam("userId") int toUserId,
			@FormParam("access_token") String accessToken,
			@FormParam("wishid")int wishid,@FormParam("lender_address") String lender_address,
			@FormParam("phone_number") String phone_number,@FormParam("pick_up_time") long pick_up_time){
		if (clientId == null || appType == null)
			return CommonLib.getResponseString("Invalid params", "", CommonLib.RESPONSE_INVALID_PARAMS);

		// check for client_id
		if (!clientId.equals(CommonLib.ANDROID_CLIENT_ID))
			return CommonLib.getResponseString("Invalid client id", "", CommonLib.RESPONSE_INVALID_CLIENT_ID);

		// check for app type
		if (!appType.equals(CommonLib.ANDROID_APP_TYPE))
			return CommonLib.getResponseString("Invalid params", "", CommonLib.RESPONSE_INVALID_APP_TYPE);

		UserDAO userDao = new UserDAO();
		int userId=userDao.userActive(accessToken);

		LogisticsDAO logisticdao=new LogisticsDAO();
		if(userId>0)
		{
			int flag=logisticdao.updatelogistic(pick_up_time,toUserId,userId,wishid);
			//String notification="Congratulations for your fulfillment of your wish " +
			//"we will deliver it to you as soon as possible. ";
			int result=logisticdao.update_userdetails(lender_address,phone_number,userId);
			if(result>0 && flag>0)
			{
				JSONObject message=new JSONObject();
				WishDAO wishDao=new WishDAO();
				User toUser=userDao.getUserDetails(toUserId);
				User fromUser=userDao.getUserDetails(userId);
				Wish wish = wishDao.getWish(wishid);
				Logistics logistic=logisticdao.getlogistic(toUserId,userId,wishid);
				try {

					message.put("to_user", JsonUtil.getUserJson(toUser));
					message.put("from_user", JsonUtil.getUserJson(fromUser));
					message.put("wish", JsonUtil.getWishJson(wish));
					message.put("logistic", JsonUtil.getLogisticJson(logistic));
					message.put("action_type",3);
					//	message.put("notifiaction", notification);

				} catch (JSONException e) {
					e.printStackTrace();
				}
				logisticdao.sendPushToAllSessions(message, toUserId);
				logisticdao.sendPushToAllSessions(message, userId);

				return CommonLib.getResponseString(message, "", CommonLib.RESPONSE_SUCCESS);
			}
			return CommonLib.getResponseString("failure", "", CommonLib.RESPONSE_FAILURE);
		}
		return CommonLib.getResponseString("failure", "", CommonLib.RESPONSE_FAILURE);

	}

	@Path("/return")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public JSONObject return_delivery(@FormParam("client_id") String clientId, 
			@FormParam("app_type") String appType,
			@FormParam("userId") int toUserId,
			@FormParam("access_token") String accessToken,
			@FormParam("wishid")int wishid,@FormParam("type") int type){
		if (clientId == null || appType == null)
			return CommonLib.getResponseString("Invalid params", "", CommonLib.RESPONSE_INVALID_PARAMS);

		// check for client_id
		if (!clientId.equals(CommonLib.ANDROID_CLIENT_ID))
			return CommonLib.getResponseString("Invalid client id", "", CommonLib.RESPONSE_INVALID_CLIENT_ID);

		// check for app type
		if (!appType.equals(CommonLib.ANDROID_APP_TYPE))
			return CommonLib.getResponseString("Invalid params", "", CommonLib.RESPONSE_INVALID_APP_TYPE);
		WishDAO wishDao=new WishDAO();
		UserDAO userDao = new UserDAO();
		int userId=userDao.userActive(accessToken);
		LogisticsDAO logisticdao=new LogisticsDAO();
		int flag=0;
		if(userId>0)
		{
			ReturnTable return_entry=logisticdao.addreturnentry(userId, toUserId,wishid, type);
			User toUser=userDao.getUserDetails(toUserId);
			User fromUser=userDao.getUserDetails(userId);
			if(type==2)
			{
				flag=logisticdao.update_return(fromUser,toUser, wishid);
			}
			Wish wish=wishDao.getWish(wishid);
			
			JSONObject message=new JSONObject();
			if(flag>0)
			{
				try {

					message.put("to_user", JsonUtil.getUserJson(toUser));
					message.put("from_user", JsonUtil.getUserJson(fromUser));
					message.put("wish", JsonUtil.getWishJson(wish));
					message.put("logistic", JsonUtil.getReturnJson(logisticdao.getreturn(toUserId,userId,wishid)));
					message.put("action_type",4);
					//message.put("notifiaction", notification);

				} catch (JSONException e) {
					e.printStackTrace();
				}
				logisticdao.sendPushToAllSessions(message, toUserId);
				logisticdao.sendPushToAllSessions(message, userId);

				return CommonLib.getResponseString(message, "", CommonLib.RESPONSE_SUCCESS);

			}
			return CommonLib.getResponseString("failure", "", CommonLib.RESPONSE_FAILURE);

		}

		return CommonLib.getResponseString("failure", "", CommonLib.RESPONSE_FAILURE);

	}
}

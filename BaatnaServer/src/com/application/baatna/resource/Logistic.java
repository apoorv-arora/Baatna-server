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
			@FormParam("userId") int lender_id,
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
		int borrow_id = userDao.userActive(accessToken);

		if(borrow_id>0){
			int flag=0;

			LogisticsDAO logisticsdao=new LogisticsDAO();
			if(type==CommonLib.SELF_DELIVERY){
				// printing message for self pickup
				WishDAO wishDao=new WishDAO();
				flag=logisticsdao.updateuserwish(lender_id,borrow_id,wishid,type); 
				//String notification="Congratulatioin borrower choose for Self Pick up";
				//MessageDAO messageDao = new MessageDAO();
				//Message messageobj=messageDao.addMessage(notification,false,"" + System.currentTimeMillis(),userId,toUserId,wishid);
				JSONObject message=new JSONObject();
				if(flag>0){
					User lenderUser=userDao.getUserDetails(lender_id);
					User borrowUser=userDao.getUserDetails(borrow_id);
					Wish wish = wishDao.getWish(wishid);
					Logistics logistic=logisticsdao.addLogisticEntry(borrow_id,lender_id,wishid,type,delivery_time);

					//Logistics logistic=logisticdao.getlogistic(toUserId,userId,wishid);

					try {

						message.put("lender_user", JsonUtil.getUserJson(lenderUser));
						message.put("borrow_user", JsonUtil.getUserJson(borrowUser));
						message.put("wish", JsonUtil.getWishJson(wish));
						message.put("logistic", JsonUtil.getLogisticJson(logistic));
						message.put("action_type",CommonLib.ACTION_SELF_PICK_UP);
						//message.put("notifiaction", notification);

					} catch (JSONException e) {
						e.printStackTrace();
					}

					logisticsdao.sendPushToAllSessions(message, lender_id);

					return CommonLib.getResponseString(message, "", CommonLib.RESPONSE_SUCCESS);
				}
				return CommonLib.getResponseString("failure", "", CommonLib.RESPONSE_FAILURE);
			}
			else if(type==CommonLib.BAATNA_DELIVERY){
				flag=logisticsdao.updateuserwish(lender_id,borrow_id,wishid,type);
				//String notification="borrower choose baatna delivery.please provide your address " +
				//		"and phone number and preffered pick up time";
				if(flag>0)
				{
					WishDAO wishDao=new WishDAO();
					int result=logisticsdao.update_userdetails(borrow_address,phone_number,borrow_id);
					if(result>0)
					{
						Logistics logistic=logisticsdao.addLogisticEntry(borrow_id,lender_id,wishid,type,delivery_time);
						JSONObject message=new JSONObject();

						User lenderUser=userDao.getUserDetails(lender_id);
						User borrowUser=userDao.getUserDetails(borrow_id);
						Wish wish = wishDao.getWish(wishid);
						//Logistics logistic=logisticdao.getlogistic(toUserId,userId,wishid);
						try {

							message.put("lender_user", JsonUtil.getUserJson(lenderUser));
							message.put("borrow_user", JsonUtil.getUserJson(borrowUser));
							message.put("wish", JsonUtil.getWishJson(wish));
							message.put("logistic", JsonUtil.getLogisticJson(logistic));
							message.put("action_type",CommonLib.ACTION_BAATNA_DELIVERY);
							//message.put("notifiaction", notification);

						} catch (JSONException e) {
							e.printStackTrace();
						}
						logisticsdao.sendPushToAllSessions(message, lender_id);
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
			@FormParam("userId") int borrow_id,
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
		int lender_id=userDao.userActive(accessToken);

		LogisticsDAO logisticdao=new LogisticsDAO();
		if(lender_id>0)
		{
			int flag=logisticdao.updatelogistic(pick_up_time,borrow_id,lender_id,wishid);
			//String notification="Congratulations for your fulfillment of your wish " +
			//"we will deliver it to you as soon as possible. ";
			int result=logisticdao.update_userdetails(lender_address,phone_number,lender_id);
			if(result>0 && flag>0)
			{
				JSONObject message=new JSONObject();
				WishDAO wishDao=new WishDAO();
				User borrowUser=userDao.getUserDetails(borrow_id);
				User lenderUser=userDao.getUserDetails(lender_id);
				Wish wish = wishDao.getWish(wishid);
				Logistics logistic=logisticdao.getlogistic(borrow_id,lender_id,wishid);
				try {

					message.put("borrow_user", JsonUtil.getUserJson(borrowUser));
					message.put("lender_user", JsonUtil.getUserJson(lenderUser));
					message.put("wish", JsonUtil.getWishJson(wish));
					message.put("logistic", JsonUtil.getLogisticJson(logistic));
					message.put("action_type",CommonLib.ACTION_DELIVERY);
					//	message.put("notifiaction", notification);

				} catch (JSONException e) {
					e.printStackTrace();
				}

				logisticdao.sendPushToAllSessions(message, borrow_id);
				logisticdao.sendPushToAllSessions(message, lender_id);
				return CommonLib.getResponseString(message, "", CommonLib.RESPONSE_SUCCESS);
			}
			return CommonLib.getResponseString("failure", "", CommonLib.RESPONSE_FAILURE);
		}
		return CommonLib.getResponseString("failure", "", CommonLib.RESPONSE_FAILURE);

	}
	@Path("/verification")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public JSONObject verify_delivery(@FormParam("client_id") String clientId, 
			@FormParam("app_type") String appType,
			@FormParam("userId") int toUserId ,
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
		int flag=0;
		UserDAO userDao = new UserDAO();
		int userId=userDao.userActive(accessToken);
		Logistics logistic=null;
		LogisticsDAO logisticdao=new LogisticsDAO();
		WishDAO wishdao=new WishDAO();
		if(userId>0){
			if(type==CommonLib.PRODUCT_RECIEVED){
				
				User borrowUser=userDao.getUserDetails(userId);
				User lenderUser=userDao.getUserDetails(toUserId);
				Wish wish=wishdao.getWish(wishid);
				JSONObject message=new JSONObject();
				flag=logisticdao.update_status(userId,toUserId,wishid,type);
				logistic=logisticdao.getlogistic(userId, toUserId, wishid);
				if(flag>0){
					try {

						message.put("lender_user", JsonUtil.getUserJson(lenderUser));
						message.put("borrow_user", JsonUtil.getUserJson(borrowUser));
						message.put("wish", JsonUtil.getWishJson(wish));
						message.put("logistic", JsonUtil.getLogisticJson(logistic));
						message.put("action_type",CommonLib.ACTION_PRODUCT_RECEIVED);
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
			else if(type==CommonLib.PRODUCT_GIVEN){
				User borrowUser=userDao.getUserDetails(userId);
				User lenderUser=userDao.getUserDetails(toUserId);
				Wish wish=wishdao.getWish(wishid);
				JSONObject message=new JSONObject();
				
			flag=logisticdao.update_status(toUserId,userId,wishid,type);
				logistic=logisticdao.getlogistic(toUserId, userId, wishid);
				try {

					message.put("lender_user", JsonUtil.getUserJson(lenderUser));
					message.put("borrow_user", JsonUtil.getUserJson(borrowUser));
					message.put("wish", JsonUtil.getWishJson(wish));
					message.put("logistic", JsonUtil.getLogisticJson(logistic));
					message.put("action_type",CommonLib.ACTION_PRODUCT_GIVEN);
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

	@Path("/return")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public JSONObject return_delivery(@FormParam("client_id") String clientId, 
			@FormParam("app_type") String appType,
			@FormParam("userId") int lender_id ,
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
		int borrow_id=userDao.userActive(accessToken);
		LogisticsDAO logisticdao=new LogisticsDAO();
		int flag=0;
		if(borrow_id>0)
		{
			ReturnTable return_entry=logisticdao.addreturnentry(borrow_id, lender_id,wishid, type);
			User lenderUser=userDao.getUserDetails(lender_id);
			User borrowUser=userDao.getUserDetails(borrow_id);
			if(type==CommonLib.RETURN_BAATNA)
			{
				flag=logisticdao.update_return(borrowUser,lenderUser, wishid);
			}
			Wish wish=wishDao.getWish(wishid);

			JSONObject message=new JSONObject();
			if(flag>0)
			{
				try {

					message.put("lender_user", JsonUtil.getUserJson(lenderUser));
					message.put("borrow_user", JsonUtil.getUserJson(borrowUser));
					message.put("wish", JsonUtil.getWishJson(wish));
					message.put("logistic", JsonUtil.getReturnJson(logisticdao.getreturn(lender_id,borrow_id,wishid)));
					message.put("action_type",CommonLib.ACTION_RETURN);
					//message.put("notifiaction", notification);

				} catch (JSONException e) {
					e.printStackTrace();
				}
				logisticdao.sendPushToAllSessions(message, lender_id);
				logisticdao.sendPushToAllSessions(message, borrow_id);

				return CommonLib.getResponseString(message, "", CommonLib.RESPONSE_SUCCESS);

			}
			return CommonLib.getResponseString("failure", "", CommonLib.RESPONSE_FAILURE);

		}

		return CommonLib.getResponseString("failure", "", CommonLib.RESPONSE_FAILURE);

	}
}

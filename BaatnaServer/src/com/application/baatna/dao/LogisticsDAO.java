package com.application.baatna.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jivesoftware.smack.XMPPException;

import com.application.baatna.bean.Logistics;
import com.application.baatna.bean.ReturnTable;
import com.application.baatna.bean.User;
import com.application.baatna.bean.UserWish;
import com.application.baatna.bean.Wish;
import com.application.baatna.util.CommonLib;
import com.application.baatna.util.DBUtil;
import com.application.baatna.util.GCM;

public class LogisticsDAO{
	public LogisticsDAO(){
	}
	private static final long daysec=86400000;
	public Logistics addLogisticEntry(int borrow_id,int lender_id,int wishid,int type,long delivery_time){
		Logistics logistic;
		Session session = null;
		try {
			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();

			logistic = new Logistics();
			logistic.setBorrower_id(borrow_id);
			logistic.setLender_id(lender_id);
			logistic.setWishid(wishid);
			logistic.setType(type);
			logistic.setDelivery_time(delivery_time);
			session.save(logistic);
			transaction.commit();
			session.close();

		} catch (HibernateException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();

			System.out.println("error");
			return null;
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}
		return logistic;
	}
	public int update_status(int borrowid,int lenderid,int wishid,int type){
			int result=0;
			Session session = null;
			try {
				session = DBUtil.getSessionFactory().openSession();
				Transaction transaction = session.beginTransaction();

				String sql="UPDATE LOGISTICS SET USER_VERIFICATION =:Type WHERE LENDERID =:user_two AND BORROWERID =:user_id AND WISHID =:wishid";
				SQLQuery query = session.createSQLQuery(sql);
				query.addEntity(Logistics.class);
				query.setParameter("Type", type);
				query.setParameter("user_two",lenderid );
				query.setParameter("user_id", borrowid);
				query.setParameter("wishid", wishid);
				result=query.executeUpdate();
				transaction.commit();
				session.close();
			} catch (HibernateException e) {

				System.out.println(e.getMessage());
				System.out.println("error");
			}finally {
				if (session != null && session.isOpen())
					session.close();
			}
			return result;
	}
	public Logistics getlogistic(int borrow_id,int lender_id,int wishid){
		Logistics logistic=null;
		Session session = null;
		try {
			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();
			logistic = new Logistics();

			String sql = "SELECT * FROM LOGISTICS WHERE BORROWERID = :borrow AND LENDERID =:lender AND WISHID =:wishid";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(Logistics.class);
			query.setParameter("borrow", borrow_id);
			query.setParameter("lender", lender_id);
			query.setParameter("wishid", wishid);
			java.util.List results = (java.util.List) query.list();

			for (Iterator iterator = ((java.util.List) results).iterator(); iterator.hasNext();) {
				logistic = (Logistics) iterator.next();
			}

			transaction.commit();
			session.close();

		} catch (HibernateException e) {
			System.out.println(e.getMessage());
			System.out.println("error");
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}

		return logistic;

	}
	public ReturnTable getreturn(int lenderid,int borrowid,int wishid){
		ReturnTable return_obj=null;
		Session session = null;
		try {
			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();
			return_obj = new ReturnTable();

			String sql = "SELECT * FROM RETURNTABLE WHERE BORROWID = :borrow AND LENDERID =:lender AND WISHID =:wishid";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(ReturnTable.class);
			query.setParameter("borrow",borrowid );
			query.setParameter("lender", lenderid);
			query.setParameter("wishid", wishid);
			java.util.List results = (java.util.List) query.list();

			for (Iterator iterator = ((java.util.List) results).iterator(); iterator.hasNext();) {
				return_obj = (ReturnTable) iterator.next();
			}

			transaction.commit();
			session.close();

		} catch (HibernateException e) {
			System.out.println(e.getMessage());
			System.out.println("error");
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}

		return return_obj;

	}
	public int updateuserwish(int lender_id,int borrow_id,int wishId,int type){
		int result=0;
		Session session = null;
		try {
			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();

			String sql="UPDATE USERWISH SET DELIVERY_OPTION = :Type WHERE USER_TWO_ID = :user_two AND USERID = :user_id AND WISHID = :wishid";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(UserWish.class);
			query.setParameter("Type", type);
			query.setParameter("user_two",lender_id );
			query.setParameter("user_id", borrow_id);
			query.setParameter("wishid", wishId);
			result=query.executeUpdate();
			transaction.commit();
			session.close();
		} catch (HibernateException e) {

			System.out.println(e.getMessage());
			System.out.println("error");
		}finally {
			if (session != null && session.isOpen())
				session.close();
		}
		return result;
	}
	public int update_userdetails(String address,String phone, int userId){
		int result=0;
		Session session=null;
		try{
			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();

			String sql="UPDATE USER SET ADDRESS =:address,PHONE =:phone WHERE USERID =:userid";
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter("address",address);
			query.setParameter("phone",phone);
			query.setParameter("userid", userId);
			result=query.executeUpdate();
		}catch (HibernateException e) {
			System.out.println(e.getMessage());
			System.out.println("error");
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}
		return result;
	}
	public int updatelogistic(long pick_up_time,int borrow_id,int lender_id,int wishid)
	{
		int result=0;
		Session session=null;
		try{
			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();

			String sql="UPDATE LOGISTICS SET PICKUPTIME =:pickup WHERE BORROWERID =:borrow AND LENDERID =:lender AND WISHID =:wishid";
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter("borrow", borrow_id);
			query.setParameter("pickup",pick_up_time);
			query.setParameter("wishid",wishid);
			query.setParameter("lender",lender_id);
			result=query.executeUpdate();
		}catch (HibernateException e) {
			System.out.println(e.getMessage());
			System.out.println("error");
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}
		return result;
	}
	public ReturnTable addreturnentry(int borrow_id,int lender_id,int wishid,int type)
	{
		ReturnTable return_entry;
		Session session = null;
		try {
			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();

			return_entry = new ReturnTable();
			return_entry.setBorrow_id(borrow_id);
			return_entry.setLender_id(lender_id);
			return_entry.setReturn_type(type);
			return_entry.setWishid(wishid);
			//logistic.setDelivery_time(delivery_time);
			session.save(return_entry);
			transaction.commit();
			session.close();

		} catch (HibernateException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();

			System.out.println("error");
			return null;
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}
		return return_entry;
	}
	public int update_return(User borrower,User lender,int wishid){
		int result=0;
		Session session=null;
		try{
			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();

			String sql="UPDATE RETURNTABLE SET R_PICKUPADDRESS =:pickup  " +
					"R_DELIVERYADDRESS =:delivery" +
					"WHERE BORROWID =:borrow AND LENDERID =:lender AND WISHID =:wishid";
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter("borrow", borrower.getUserId());
			query.setParameter("wishid",wishid);
			query.setParameter("pickup",borrower.getAddress());
			query.setParameter("delivery",lender.getAddress());
			query.setParameter("lender",lender.getUserId());
			result=query.executeUpdate();
		}catch (HibernateException e) {
			System.out.println(e.getMessage());
			System.out.println("error");
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}
		return result;

	}
	public void getdafaultUser(){
		Logistics logistic=null;
		User user=null;
		UserDAO userdao=new UserDAO();
		WishDAO wishdao=new WishDAO();
		Wish wish=null;
		Session session = null;
		try {
			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();
			String sql="SELECT * FROM LOGISTICS WHERE RETURN_DEADLINE <:deadline";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(Logistics.class);
			query.setParameter("deadline", System.currentTimeMillis()+daysec);
			java.util.List results = (java.util.List) query.list();

			for (Iterator iterator = ((java.util.List) results).iterator(); iterator.hasNext();) {

				logistic = (Logistics) iterator.next();

				user=userdao.getUserDetails(logistic.getBorrower_id());
				wish=wishdao.getWish(logistic.getWishid());
				JSONObject message=new JSONObject();
				String notification="please return the product to lender";
				try{
					message.put("user", user);
					message.put("wish", wish);
					message.put("notification", notification);
				}catch (JSONException e) {
					e.printStackTrace();
				}
				sendPushToReturnSessions(message,user.getUserId());

			}
			transaction.commit();
			session.close();


		} catch (HibernateException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();

			System.out.println("error");
			return;
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}

	}
	public void sendPushToReturnSessions(JSONObject notification, int userId) {

		UserDAO userDao = new UserDAO();
		ArrayList<com.application.baatna.bean.Session> nearbyUsers = userDao.getNearbyUsers(userId);
		GCM ccsClient = new GCM();
		String userName = CommonLib.projectId + "@gcm.googleapis.com";
		String password = CommonLib.apiKey;
		try {
			ccsClient.connect(userName, password);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		String messageId = ccsClient.getRandomMessageId();

		Map<String, String> payload = new HashMap<String, String>();
		payload.put("command", "something");
		payload.put("Notification", String.valueOf(notification));
		payload.put("type", "return_request");

		JSONObject object = new JSONObject();
		try {
			object.put("Notification", notification);
			object.put("actionId", "id");
			object.put("additionalParam", "value");
		} catch (JSONException exp) {
			// String error = LogMessages.FETCH_ERROR + exp.getMessage();
			// logger.log(Level.INFO, error);
			exp.printStackTrace();
		}
		payload.put("value", object.toString());
		payload.put("EmbeddedMessageId", messageId);
		Long timeToLive = 10000L;
		Boolean delayWhileIdle = false;

		// this will change
		for (com.application.baatna.bean.Session nearbyUser : nearbyUsers) {
			// send push notif to all
			ccsClient.send(GCM.createJsonMessage(nearbyUser.getPushId(), messageId, payload, null, timeToLive,
					delayWhileIdle));
		}
		ccsClient.disconnect();

	}


	public void sendPushToAllSessions(JSONObject message, int userId){
		UserDAO userDao = new UserDAO();
		ArrayList<com.application.baatna.bean.Session> nearbyUsers = userDao.getNearbyUsers(userId);
		GCM ccsClient = new GCM();
		String userName = CommonLib.projectId + "@gcm.googleapis.com";
		String password = CommonLib.apiKey;
		try {
			ccsClient.connect(userName, password);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		String messageId = ccsClient.getRandomMessageId();

		Map<String, String> payload = new HashMap<String, String>();
		payload.put("command", "something");
		payload.put("Notification", String.valueOf(message));
		payload.put("type", "logistic");

		JSONObject object = new JSONObject();
		try {
			object.put("Notification", message);
			object.put("actionId", "id");
			object.put("additionalParam", "value");
		} catch (JSONException exp) {
			// String error = LogMessages.FETCH_ERROR + exp.getMessage();
			// logger.log(Level.INFO, error);
			exp.printStackTrace();
		}
		payload.put("value", object.toString());
		payload.put("EmbeddedMessageId", messageId);
		Long timeToLive = 10000L;
		Boolean delayWhileIdle = false;

		// this will change
		for (com.application.baatna.bean.Session nearbyUser : nearbyUsers) {
			// send push notif to all
			ccsClient.send(GCM.createJsonMessage(nearbyUser.getPushId(), messageId, payload, null, timeToLive,
					delayWhileIdle));
		}
		ccsClient.disconnect();


	}
}
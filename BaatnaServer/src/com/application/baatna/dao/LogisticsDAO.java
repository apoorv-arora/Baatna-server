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
	public Logistics addLogisticEntry(int user_one,int user_two,int type,long delivery_time){
		Logistics logistic;
		Session session = null;
		try {
			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();

			logistic = new Logistics();
			logistic.setUserid_one(user_one);
			logistic.setUserid_two(user_two);
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
	public Logistics getlogistic(int user_one,int user_two,int wishid){
		Logistics logistic=null;
		Session session = null;
		try {
			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();
			logistic = new Logistics();

			String sql = "SELECT * FROM LOGISTICS WHERE USERID_ONE = :userid_one AND USERID_TWO =:userid_two AND WISHID =:wishid";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(Logistics.class);
			query.setParameter("userid_one", user_one);
			query.setParameter("userid_two", user_two);
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
	public ReturnTable getreturn(int user_one,int user_two,int wishid){
		ReturnTable return_obj=null;
		Session session = null;
		try {
			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();
			return_obj = new ReturnTable();

			String sql = "SELECT * FROM RETURNTABLE WHERE USERID_ONE = :userid_one AND USERID_TWO =:userid_two AND WISHID =:wishid";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(ReturnTable.class);
			query.setParameter("userid_one", user_one);
			query.setParameter("userid_two", user_two);
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
	public int updateuserwish(int usertwoId,int useroneId,int wishId,int type){
		int result=0;
		Session session = null;
		try {
			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();

			String sql="UPDATE USERWISH SET DELIVERY_OPTION = :Type WHERE USER_TWO_ID = :user_two AND USERID = :user_id AND WISHID = :wishid";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(UserWish.class);
			query.setParameter("Type", type);
			query.setParameter("user_two",usertwoId );
			query.setParameter("user_id", useroneId);
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
	public int updatelogistic(long pick_up_time,int userid_one,int userid_two,int wishid)
	{
		int result=0;
		Session session=null;
		try{
			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();

			String sql="UPDATE LOGISTICS SET PICKUPTIME =:pickup WHERE USERID_ONE =:userid_one AND USERID_TWO =:userid_two AND WISHID =:wishid";
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter("userid_one", userid_one);
			query.setParameter("pickup",pick_up_time);
			query.setParameter("wishid",wishid);
			query.setParameter("userid_two",userid_two);
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
	public ReturnTable addreturnentry(int user_one,int user_two,int wishid,int type)
	{
		ReturnTable return_entry;
		Session session = null;
		try {
			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();

			return_entry = new ReturnTable();
			return_entry.setUser_id_one(user_one);
			return_entry.setUser_id_two(user_two);
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
	public int update_return(User user_one,User user_two,int wishid){
		int result=0;
		Session session=null;
		try{
			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();

			String sql="UPDATE RETURNTABLE SET PICKUPADDRESS =:pickup  " +
					"DELIVERYADDRESS =:delivery" +
					"WHERE USERID_ONE =:userid_one AND USERID_TWO =:userid_two AND WISHID =:wishid";
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter("userid_one", user_one.getUserId());
			query.setParameter("wishid",wishid);
			query.setParameter("pickup",user_one.getAddress());
			query.setParameter("delivery",user_two.getAddress());
			query.setParameter("userid_two",user_two.getUserId());
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
		payload.put("type", "message");

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
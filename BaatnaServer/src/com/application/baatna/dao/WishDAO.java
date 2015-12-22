package com.application.baatna.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.mail.EmailException;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jivesoftware.smack.XMPPException;

import com.application.baatna.bean.User;
import com.application.baatna.bean.UserWish;
import com.application.baatna.bean.Wish;
import com.application.baatna.util.CommonLib;
import com.application.baatna.util.DBUtil;
import com.application.baatna.util.GCM;
import com.application.baatna.util.mailer.EmailModel;
import com.application.baatna.util.mailer.EmailUtil;

public class WishDAO {

	public WishDAO() {
	}

	public Wish addWishPost(String title, String description, long timeOfPost, int userId, int requiredFor) {

		Wish wish;
		Session session = null;
		try {
			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();

			wish = new Wish();
			wish.setTitle(title);
			wish.setDescription(description);
			wish.setTimeOfPost(timeOfPost);
			wish.setUserId(userId);
			wish.setRequiredFor(requiredFor);
			wish.setStatus(CommonLib.STATUS_ACTIVE);

			session.save(wish);
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
		return wish;
	}

	public ArrayList<Wish> getAllWishes(int userId, int start, int count) {
		ArrayList<Wish> wishes;
		Session session = null;
		try {
			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();

			// finding user info
			Wish wish = null;
			int i = 0;
			wishes = new ArrayList<Wish>();

			String sql = "SELECT * FROM WISH WHERE USERID = :userid AND STATUS = :status LIMIT :start , :count";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(Wish.class);
			query.setParameter("userid", userId);
			query.setParameter("start", start);
			query.setParameter("count", count);
			query.setParameter("status", CommonLib.STATUS_ACTIVE);

			java.util.List results = (java.util.List) query.list();

			for (Iterator iterator = ((java.util.List) results).iterator(); iterator.hasNext();) {

				wish = (Wish) iterator.next();
				wishes.add(wish);
				i++;

			}

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

		return wishes;
	}

	public ArrayList<Wish> getAllWishesBasedOnType(int userId, int start, int count, int type) {
		ArrayList<Wish> wishes = new ArrayList<Wish>();
		Session session = null;
		try {
			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();

			// finding user info
			Wish wish = null;
			wishes = new ArrayList<Wish>();

			if (type == CommonLib.WISH_OFFERED) {
				String sql = "SELECT * FROM WISH WHERE STATUS <> :status_id AND WISHID IN (SELECT WISHID FROM USERWISH WHERE USER_TWO_ID = :userid) LIMIT :start , :count";
				SQLQuery query = session.createSQLQuery(sql);
				query.addEntity(Wish.class);
				query.setParameter("userid", userId);
				query.setParameter("start", start);
				query.setParameter("count", count);
				query.setParameter("status_id", CommonLib.STATUS_DELETED);

				java.util.List results = (java.util.List) query.list();

				for (Iterator iterator = ((java.util.List) results).iterator(); iterator.hasNext();) {
					Wish wishFound = (Wish) iterator.next();
					wishes.add(wishFound);
				}
			} else if (type == CommonLib.WISH_OWN) {
				String sql = "SELECT * FROM WISH WHERE USERID = :userid AND STATUS <> :status_id LIMIT :start , :count";
				SQLQuery query = session.createSQLQuery(sql);
				query.addEntity(Wish.class);
				query.setParameter("userid", userId);
				query.setParameter("start", start);
				query.setParameter("count", count);
				query.setParameter("status_id", CommonLib.STATUS_DELETED);

				java.util.List results = (java.util.List) query.list();

				for (Iterator iterator = ((java.util.List) results).iterator(); iterator.hasNext();) {

					wish = (Wish) iterator.next();
					wishes.add(wish);
				}
			}

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

		return wishes;
	}

	public int getAllWishesCountBasedOnType(int userId, int type) {
		// ArrayList<Wish> wishes;
		int count = 0;

		Session session = null;
		try {
			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();

			// finding user info
			Wish wish = null;
			// int i = 0;
			// wishes = new ArrayList<Wish>();

			if (type == CommonLib.WISH_OFFERED) {
				String sql = "Select Count(*) from Wish where STATUS <> :status_id and WISHID IN (SELECT WISHID FROM USERWISH WHERE USER_TWO_ID = :userid)";
				SQLQuery query = session.createSQLQuery(sql);
				query.setParameter("userid", userId);
				query.setParameter("status_id", CommonLib.STATUS_DELETED);
				java.util.List results = (java.util.List) query.list();
				Object resultValue = results.get(0);
				if (resultValue instanceof BigInteger)
					count = ((BigInteger) results.get(0)).intValue();
				else
					count = 0;
			} else if (type == CommonLib.WISH_OWN) {
				String sql = "SELECT * FROM WISH WHERE USERID = :userid AND STATUS <> :status_id ";
				SQLQuery query = session.createSQLQuery(sql);
				// query.addEntity(Wish.class);
				query.setParameter("userid", userId);
				query.setParameter("status_id", CommonLib.STATUS_DELETED);

				java.util.List results = (java.util.List) query.list();
				if (results != null && results.size() > 0) {
					Object resultValue = results.get(0);
					if (resultValue instanceof BigInteger) {
						count = ((BigInteger) results.get(0)).intValue();
					} else
						count = 0;
				} else
					count = 0;
			}

			transaction.commit();
			session.close();

		} catch (HibernateException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();

			System.out.println("error");
			return 0;
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}
		return count;
		// return wishes.size();
	}

	public Wish getWish(int wishId) {
		Wish wish = null;
		Session session = null;
		try {
			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();

			// finding user info

			String sql = "SELECT * FROM WISH WHERE WISHID = :wishId ";// AND
																		// STATUS
																		// IN
																		// :status,
																		// :status2
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(Wish.class);
			query.setParameter("wishId", wishId);
			// query.setParameter("status", CommonLib.STATUS_ACTIVE);
			// query.setParameter("status", CommonLib.STATUS_ACCEPTED);
			// query.setParameter("status", CommonLib.STATUS_FULLFILLED);

			java.util.List results = (java.util.List) query.list();

			for (Iterator iterator = ((java.util.List) results).iterator(); iterator.hasNext();) {

				wish = (Wish) iterator.next();

			}

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

		return wish;
	}

	public boolean deleteWish(int wishId) {

		Session session = null;
		try {
			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();

			// Archive the wish and not deleting the wish helps in Data analysis
			String sql = "SELECT * FROM WISH WHERE wishId = :wish_id";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(Wish.class);
			query.setParameter("wish_id", wishId);
			java.util.List results = (java.util.List) query.list();
			Wish currentSession = (Wish) results.get(0);
			currentSession.setStatus(CommonLib.STATUS_DELETED);
			session.update(currentSession);

			transaction.commit();
			session.close();

		} catch (HibernateException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();

			System.out.println("error");
			return false;
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}

		// Delete this wish from feed
		FeedDAO feedDao = new FeedDAO();
		feedDao.deleteWish(wishId);

		return true;

	}

	public void sendPushToNearbyUsers(JSONObject notification, int userId) {

		UserDAO userDao = new UserDAO();
		ArrayList<com.application.baatna.bean.Session> nearbyUsers = userDao.getUsersNotEquals(userId);
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
		payload.put("type", "newWish");

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

		for (com.application.baatna.bean.Session nearbyUser : nearbyUsers) {
			// send push notif to all
			ccsClient.send(GCM.createJsonMessage(nearbyUser.getPushId(), messageId, payload, null, timeToLive,
					delayWhileIdle));
		}
		ccsClient.disconnect();

	}

	public void sendPushToUsers(JSONObject notification, int userId) {

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
		payload.put("type", "wish");

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

	public boolean updateWishedUsers(int userId, int type, int wishId) {

		Session session = null;
		Wish wish = null;
		try {
			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();

			String sql = "SELECT * FROM WISH WHERE WISHID = :wishId";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(Wish.class);
			query.setParameter("wishId", wishId);

			java.util.List results = (java.util.List) query.list();

			wish = (Wish) results.get(0);

			if (type == CommonLib.ACTION_ACCEPT_WISH) {

				String sql3 = "INSERT INTO USERWISH (WISHID, USERID, WISH_STATUS, USER_TWO_ID) VALUES (:WISHID,:USERID,:WISH_STATUS,:USER_TWO_ID)";
				SQLQuery query3 = session.createSQLQuery(sql3);
				query3.setParameter("WISHID", wishId);
				query3.setParameter("USERID", wish.getUserId());
				query3.setParameter("WISH_STATUS", CommonLib.STATUS_ACCEPTED);
				query3.setParameter("USER_TWO_ID", userId);

				query3.executeUpdate();

				String sql2 = "SELECT * FROM USER WHERE USERID = :userid";
				SQLQuery query4 = session.createSQLQuery(sql2);
				query4.addEntity(User.class);
				query4.setParameter("userid", wish.getUserId());
				java.util.List results4 = (java.util.List) query4.list();

				User mUser = null;
				for (Iterator iterator = ((java.util.List) results4).iterator(); iterator.hasNext();) {
					mUser = (User) iterator.next();
					break;
				}
				
				String currentSqlQuery = "SELECT * FROM USER WHERE USERID = :userid";
				SQLQuery currentQuery = session.createSQLQuery(currentSqlQuery);
				currentQuery.addEntity(User.class);
				currentQuery.setParameter("userid", userId);
				java.util.List currentResults = (java.util.List) currentQuery.list();

				User currentUser = null;
				for (Iterator iterator = ((java.util.List) currentResults).iterator(); iterator.hasNext();) {
					currentUser = (User) iterator.next();
					break;
				}

				if (mUser != null && currentUser != null) {
					EmailModel emailModel = new EmailModel();
					emailModel.setFrom(CommonLib.BAPP_ID);
					emailModel.setTo(mUser.getEmail());
					emailModel.setSubject("There is a new response to your request for " + wish.getTitle());
					emailModel.setContent("Hi " + CommonLib.getUserName(mUser) + "\n\n" + CommonLib.getUserName(currentUser) + " replied to your request for (a) " + wish.getTitle() + "!\n\nLet " + CommonLib.getUserName(currentUser) + " know if you're interested.\nHave you found what you're looking for?\n\nSee you around the neighbourhood.\n\nCheers\nBaatna Team");
					EmailUtil.getInstance().sendEmail(emailModel);

				}

			} else if (type == CommonLib.ACTION_DECLINE_WISH) {

				String sql3 = "INSERT INTO USERWISH (WISHID, USERID, WISH_STATUS, USER_TWO_ID) VALUES (:WISHID,:USERID,:WISH_STATUS,:USER_TWO_ID);";
				SQLQuery query3 = session.createSQLQuery(sql3);
				query3.setParameter("WISHID", wishId);
				query3.setParameter("USERID", wish.getUserId());
				query3.setParameter("WISH_STATUS", CommonLib.STATUS_DELETED);
				query3.setParameter("USER_TWO_ID", userId);

				query3.executeUpdate();
			}

			transaction.commit();
			session.close();
			return true;

		} catch (HibernateException e) {
			System.out.println(e.getMessage());
			System.out.println("error");
			e.printStackTrace();

		} finally {
			if (session != null && session.isOpen())
				session.close();
		}
		return false;
	}

	/*
	 * public Set getWishedUsers(int type, int wishId) {
	 * 
	 * Session session = null; Wish wish = null; Set users = null; try { session
	 * = DBUtil.getSessionFactory().openSession(); Transaction transaction =
	 * session.beginTransaction();
	 * 
	 * String sql =
	 * "SELECT * FROM WISH WHERE WISHID = :wishId AND STATUS = :status";
	 * SQLQuery query = session.createSQLQuery(sql);
	 * query.addEntity(Wish.class); query.setParameter("wishId", wishId);
	 * query.setParameter("status", CommonLib.STATUS_ACTIVE);
	 * 
	 * java.util.List results = (java.util.List) query.list();
	 * 
	 * for (Iterator iterator = ((java.util.List) results).iterator(); iterator
	 * .hasNext();) {
	 * 
	 * wish = (Wish) iterator.next();
	 * 
	 * }
	 * 
	 * if (type == CommonLib.STATUS_ACCEPTED){ users = wish.getAcceptedUsers();
	 * } else if (type == CommonLib.STATUS_DELETED){ users =
	 * wish.getDeclinedUsers(); }
	 * 
	 * transaction.commit(); session.close();
	 * 
	 * } catch (HibernateException e) { System.out.println(e.getMessage());
	 * System.out.println("error"); e.printStackTrace();
	 * 
	 * } finally { if(session != null && session.isOpen()) session.close(); }
	 * return users; }
	 */

	public int getWishesCount(int userId) {
		int size = 0;
		ArrayList<Wish> wishes;
		Session session = null;
		try {
			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();

			// finding user info
			// Wish wish = null;

			String sql = "SELECT COUNT(*) FROM WISH WHERE USERID = :userid AND STATUS = :status";
			SQLQuery query = session.createSQLQuery(sql);
			// query.addEntity(Wish.class);
			query.setParameter("userid", userId);
			query.setParameter("status", CommonLib.STATUS_ACTIVE);

			java.util.List results = (java.util.List) query.list();
			Object resultValue = results.get(0);
			if (resultValue instanceof BigInteger)
				size = ((BigInteger) results.get(0)).intValue();
			else
				size = 0;
			/*
			 * for (Iterator iterator = ((java.util.List) results).iterator();
			 * iterator .hasNext();) {
			 * 
			 * wish = (Wish) iterator.next(); size++; }
			 */

			transaction.commit();
			session.close();
		} catch (HibernateException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();

			System.out.println("error");
			return 0;
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}

		return size;
	}

	public boolean updateWishStatus(int userId, int type, int wishId, int offered) {

		Session session = null;
		Wish wish = null;
		try {
			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();

			String sql = "SELECT * FROM WISH WHERE WISHID = :wishId";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(Wish.class);
			query.setParameter("wishId", wishId);

			java.util.List results = (java.util.List) query.list();

			for (Iterator iterator = ((java.util.List) results).iterator(); iterator.hasNext();) {

				wish = (Wish) iterator.next();

			}

			if (offered == CommonLib.ACTION_WISH_OFFERED) {
				if (wish.getStatus() == CommonLib.STATUS_RECEIVED)
					wish.setStatus(CommonLib.STATUS_FULLFILLED);
				else
					wish.setStatus(CommonLib.STATUS_OFFERED);
			} else if (offered == CommonLib.ACTION_WISH_RECEIVED) {
				if (wish.getStatus() == CommonLib.STATUS_OFFERED)
					wish.setStatus(CommonLib.STATUS_FULLFILLED);
				else
					wish.setStatus(CommonLib.STATUS_RECEIVED);
			}

			session.update(wish);

			transaction.commit();
			session.close();
			return true;

		} catch (HibernateException e) {
			System.out.println(e.getMessage());
			System.out.println("error");
			e.printStackTrace();

		} finally {
			if (session != null && session.isOpen())
				session.close();
		}
		return false;
	}

	public List<User> getWishedUsers(int updateType, int wishId) {

		Session session = null;
		List<User> results = new ArrayList<>();
		try {
			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();

			String sql = "Select * FROM USER WHERE USERID in (SELECT USERID FROM USERWISH WHERE WISHID = :wishId AND WISH_STATUS = :status)";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(User.class);
			query.setParameter("wishId", wishId);
			query.setParameter("status", updateType);

			results = (java.util.List) query.list();

			transaction.commit();
			session.close();
		} catch (HibernateException e) {
			System.out.println(e.getMessage());
			System.out.println("error");
			e.printStackTrace();

		} finally {
			if (session != null && session.isOpen())
				session.close();
		}

		return results;

	}
	
	public boolean updateWishedNegotiation(int userId, int actionType, int wishId, int negAmount ) {
		
		Session session = null;
		Wish wish = null;
		if (actionType == CommonLib.ACTION_NEGOTIATION_ACCEPTED){

			try {
				session = DBUtil.getSessionFactory().openSession();
				Transaction transaction = session.beginTransaction();

				String sql = "SELECT * FROM WISH WHERE WISHID = :wishid";
				SQLQuery query = session.createSQLQuery(sql);
				query.addEntity(Wish.class);
				query.setParameter("wishid", wishId);

				java.util.List results = (java.util.List) query.list();

				wish = (Wish) results.get(0);

				

				String sql3 = "Update USERWISH set NEGOTIATION_STATUS=:negStatus, NEGOTIATION_AMOUNT=:negAmount WHERE USERID=:userId AND WISHID=:wishId";
				SQLQuery query3 = session.createSQLQuery(sql3);
				query3.setParameter("negStatus", true);
				query3.setParameter("negAmount", negAmount);
				query3.setParameter("wishId", wishId);
				query3.setParameter("userId", wish.getUserId());

				query3.executeUpdate();

				transaction.commit();
				session.close();
				return true;

			} catch (HibernateException e) {
				System.out.println(e.getMessage());
				System.out.println("error");
				e.printStackTrace();

			} finally {
				if (session != null && session.isOpen())
					session.close();
			}
		return false;
		}

		else if(actionType == CommonLib.ACTION_NEGOTIATION_STARTED || actionType==CommonLib.ACTION_RENEGOTIATION ){
			return true;
		}

		else{
			return false;
		}
	}


}
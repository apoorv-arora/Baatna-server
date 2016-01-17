package com.application.baatna.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import org.codehaus.jettison.json.JSONObject;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.application.baatna.bean.AllMessages;
import com.application.baatna.bean.Message;
import com.application.baatna.bean.User;
import com.application.baatna.bean.UserCompactMessage;
import com.application.baatna.bean.Wish;
import com.application.baatna.util.CommonLib;
import com.application.baatna.util.DBUtil;
import com.application.baatna.util.PushModel;
import com.application.baatna.util.PushUtil;

public class MessageDAO {

	public MessageDAO() {
	}

	public Message addMessage(String incomingMessage, boolean status, String timeOfMessage, int fromUserId,
			int toUserId, int wishId) {
		Session session = null;
		Message message;
		try {

			// 3. Get Session object
			session = DBUtil.getSessionFactory().openSession();

			// 4. Starting Transaction
			Transaction transaction = session.beginTransaction();
			message = new Message();
			message.setMessage(incomingMessage);
			message.setStatus(status);
			message.setTimeOfMessage(timeOfMessage);
			message.setFromUserId(fromUserId);
			message.setToUserId(toUserId);
			message.setWishId(wishId);

			session.save(message);
			transaction.commit();
			System.out.println("\n\n Details Added \n");
			session.close();

		} catch (HibernateException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			message = null;
			System.out.println("error");
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}
		return message;
	}

	public void sendPushToNearbyUsers(JSONObject notification, int userId, String type) {

		UserDAO userDao = new UserDAO();
		ArrayList<com.application.baatna.bean.Session> nearbyUsers = userDao.getNearbyUsers(userId);

		PushUtil pushUtil = PushUtil.getInstance();
		PushModel pushModel = new PushModel();
		pushModel.setNotification(notification);
		for (com.application.baatna.bean.Session nearbyUser : nearbyUsers) {
			pushModel.setPushId(nearbyUser.getPushId());
			pushUtil.sendPush(pushModel, type);
		}

	}

	public AllMessages getAllMessages(int fromUserId, int toUserId) {

		AllMessages allmessages = null;
		Session session = null;
		try {
			// 1. configuring hibernate
			session = DBUtil.getSessionFactory().openSession();

			// 4. Starting Transaction
			Transaction transaction = session.beginTransaction();

			// finding messages1
			System.out.println("Getting Record");
			Message message = null;
			allmessages = new AllMessages();
			int i = 0;
			allmessages.setNum1(i);
			LinkedList messages = new LinkedList();

			String sql = "SELECT * FROM MESSAGE WHERE FROMUSERID = :fromuserid AND TOUSERID = :touserid";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(Message.class);
			query.setParameter("fromuserid", fromUserId);
			query.setParameter("touserid", toUserId);

			java.util.List results = (java.util.List) query.list();

			for (Iterator iterator = ((java.util.List) results).iterator(); iterator.hasNext();) {

				message = (Message) iterator.next();
				messages.add(message);
				i++;

			}

			allmessages.setNum1(i);
			allmessages.setMessages1(messages);

			// finding messages2
			i = 0;
			allmessages.setNum2(i);
			messages = new LinkedList();

			sql = "SELECT * FROM MESSAGE WHERE FROMUSERID = :fromuserid AND TOUSERID = :touserid";
			query = session.createSQLQuery(sql);
			query.addEntity(Message.class);
			query.setParameter("fromuserid", toUserId);
			query.setParameter("touserid", fromUserId);

			results = (java.util.List) query.list();

			for (Iterator iterator = ((java.util.List) results).iterator(); iterator.hasNext();) {

				message = (Message) iterator.next();
				messages.add(message);
				i++;

			}

			allmessages.setNum2(i);
			allmessages.setMessages2(messages);

			transaction.commit();
			System.out.println("\n\n Details Added \n");
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

		System.out.println("Done here");
		return allmessages;

	}

	/**
	 * @param userId
	 *            User which has sent the deletion request
	 * @param wishId
	 *            Wish which is to be deleted
	 */
	public ArrayList<UserCompactMessage> getAcceptedUsersForMessages(int userId) {

		Session session = null;
		ArrayList<UserCompactMessage> acceptedUsers = new ArrayList<UserCompactMessage>();
		try {
			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();
			UserDAO dao = new UserDAO();

			{
				// not the wish of the current user
				String sql = "SELECT * FROM WISH WHERE USERID <> :userId";
				SQLQuery query = session.createSQLQuery(sql);
				query.addEntity(Wish.class);
				query.setParameter("userId", userId);
				java.util.List results = (java.util.List) query.list();
				for (Iterator iterator = ((java.util.List) results).iterator(); iterator.hasNext();) {
					Wish currentWish = (Wish) iterator.next();
					String sql2 = "SELECT * FROM USER WHERE USERID IN (SELECT USERID FROM USERWISH WHERE USER_TWO_ID= :userId AND WISH_STATUS NOT IN (:status_deleted AND :status_active) and WISHID =:wishId)";
					SQLQuery query2 = session.createSQLQuery(sql2);
					query2.addEntity(User.class);
					query2.setParameter("status_deleted", CommonLib.STATUS_DELETED);
					query2.setParameter("wishId", currentWish.getWishId());
					query2.setParameter("status_active", CommonLib.STATUS_ACTIVE);
					query2.setParameter("userId", userId);
					java.util.List results2 = (java.util.List) query2.list();
					for (Iterator iterator2 = ((java.util.List) results2).iterator(); iterator2.hasNext();) {
						User acceptedUser = (User) iterator2.next();
						if (!dao.isUserBlocked(userId, acceptedUser.getUserId())
								&& !dao.isUserBlocked(acceptedUser.getUserId(), userId)) {
							UserCompactMessage compatMessage = new UserCompactMessage();
							compatMessage.setUser(acceptedUser);
							compatMessage.setWish(currentWish);
							compatMessage.setType(CommonLib.WISH_ACCEPTED_CURRENT_USER);
							compatMessage.setTimestamp(currentWish.getTimeOfPost());
							acceptedUsers.add(compatMessage);
						}
					}
				}
			}

			{
				// wish of the current user
				String sql = "SELECT * FROM WISH WHERE USERID = :userId";
				SQLQuery query = session.createSQLQuery(sql);
				query.addEntity(Wish.class);
				query.setParameter("userId", userId);
				java.util.List results = (java.util.List) query.list();
				for (Iterator iterator = ((java.util.List) results).iterator(); iterator.hasNext();) {
					Wish currentWish = (Wish) iterator.next();
					String sql2 = "SELECT * FROM USER WHERE USERID IN (SELECT USER_TWO_ID FROM USERWISH WHERE WISH_STATUS NOT IN (:status_deleted AND :status_active) and WISHID =:wishId)";
					SQLQuery query2 = session.createSQLQuery(sql2);
					query2.addEntity(User.class);
					query2.setParameter("status_deleted", CommonLib.STATUS_DELETED);
					query2.setParameter("wishId", currentWish.getWishId());
					query2.setParameter("status_active", CommonLib.STATUS_ACTIVE);
					java.util.List results2 = (java.util.List) query2.list();

					for (Iterator iterator2 = ((java.util.List) results2).iterator(); iterator2.hasNext();) {
						User acceptedUser = (User) iterator2.next();
						if (!dao.isUserBlocked(userId, acceptedUser.getUserId())
								&& !dao.isUserBlocked(acceptedUser.getUserId(), userId)) {
							UserCompactMessage compatMessage = new UserCompactMessage();
							compatMessage.setUser(acceptedUser);
							compatMessage.setWish(currentWish);
							compatMessage.setType(CommonLib.CURRENT_USER_WISH_ACCEPTED);
							compatMessage.setTimestamp(currentWish.getTimeOfPost());
							acceptedUsers.add(compatMessage);
						}
					}
				}

			}

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
		return acceptedUsers;
	}

}

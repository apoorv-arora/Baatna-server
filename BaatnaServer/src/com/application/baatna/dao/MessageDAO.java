package com.application.baatna.dao;

import java.util.Iterator;
import java.util.LinkedList;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.application.baatna.bean.AllMessages;
import com.application.baatna.bean.Message;
import com.application.baatna.util.DBUtil;

public class MessageDAO {

	public MessageDAO() {
	}

	public boolean addMessage(String incomingMessage, boolean status,
			String timeOfMessage, int fromUserId, int toUserId, int wishId,
			int messageId) {
		Session session = null;
		try {

			// 3. Get Session object
			session = DBUtil.getSessionFactory().openSession();

			// 4. Starting Transaction
			Transaction transaction = session.beginTransaction();
			Message message = new Message();
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

			System.out.println("error");
			return false;
		} finally {
			if(session != null && session.isOpen())
				session.close();
		}

		return true;

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

			for (Iterator iterator = ((java.util.List) results).iterator(); iterator
					.hasNext();) {

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

			for (Iterator iterator = ((java.util.List) results).iterator(); iterator
					.hasNext();) {

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
			if(session != null && session.isOpen())
				session.close();
		}

		System.out.println("Done here");
		return allmessages;

	}
}

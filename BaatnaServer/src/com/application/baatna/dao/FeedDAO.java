package com.application.baatna.dao;

import java.util.ArrayList;
import java.util.Iterator;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.application.baatna.bean.Location;
import com.application.baatna.bean.NewsFeed;
import com.application.baatna.bean.User;
import com.application.baatna.util.DBUtil;

public class FeedDAO {

	public static final int USER_JOINED = 1;
	public static final int USER_REQUESTED_WISH = 2;
	public static final int USER_WISH_FULFILLED = 3;

	public FeedDAO() {

	}

	/**
	 * Get feed items of user's which have recently joined and are near to the
	 * provided user
	 */
	public ArrayList<NewsFeed> getNearbyUsers(Location location) {

		ArrayList<NewsFeed> nearbyUsers = new ArrayList<NewsFeed>();
		Session session = null;
		try {

			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();

			String sql = "SELECT * FROM NEWSFEED WHERE TYPE = 1";// append
																	// params
																	// for
																	// nearby
																	// filter.
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(NewsFeed.class);
			java.util.List results = (java.util.List) query.list();

			for (Iterator iterator = ((java.util.List) results).iterator(); iterator.hasNext();) {
				nearbyUsers.add((NewsFeed) iterator.next());
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
		return nearbyUsers;
	}

	/**
	 * Get feed items of user's which have recently joined and are near to the
	 * provided user
	 */
	public ArrayList<NewsFeed> getUsersWithWishes(Location location) {

		ArrayList<NewsFeed> nearbyUsers = new ArrayList<NewsFeed>();
		Session session = null;
		try {

			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();

			String sql = "SELECT * FROM NEWSFEED WHERE TYPE = 2";// append
																	// params
																	// for
																	// nearby
																	// filter.
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(NewsFeed.class);
			java.util.List results = (java.util.List) query.list();

			for (Iterator iterator = ((java.util.List) results).iterator(); iterator.hasNext();) {
				nearbyUsers.add((NewsFeed) iterator.next());
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
		return nearbyUsers;
	}

	// get feed items of type 3
	/**
	 * Get feed items of user's which have recently joined and are near to the
	 * provided user
	 */
	public ArrayList<NewsFeed> getUsersWithWishesFulfilled(Location location) {

		ArrayList<NewsFeed> nearbyUsers = new ArrayList<NewsFeed>();
		Session session = null;
		try {

			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();

			String sql = "SELECT * FROM NEWSFEED WHERE TYPE = 2";// append
																	// params
																	// for
																	// nearby
																	// filter.
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(NewsFeed.class);
			java.util.List results = (java.util.List) query.list();

			for (Iterator iterator = ((java.util.List) results).iterator(); iterator.hasNext();) {
				nearbyUsers.add((NewsFeed) iterator.next());
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
		return nearbyUsers;
	}

	/**
	 * Add user details to the DB for a newly signed up user. Updates User and
	 * Session tables.
	 * 
	 * @param type
	 *            1: userIdFirst joined, type 2: userIdFirst requested wishId,
	 *            type 3: userIdFirst gave wishId to userIdSecond
	 * 
	 */
	public boolean addFeedItem(int type, long timestamp, int userIdFirst, int userIdSecond, int wishId) {
		NewsFeed feedItem;
		Session session = null;
		try {
			session = DBUtil.getSessionFactory().openSession();

			Transaction transaction = session.beginTransaction();
			feedItem = new NewsFeed();
			feedItem.setType(type);
			feedItem.setTimestamp(timestamp);
			feedItem.setUserIdFirst(userIdFirst);
			feedItem.setUserIdSecond(userIdSecond);
			feedItem.setWishId(wishId);

			session.save(feedItem);

			transaction.commit();
			session.close();
			return true;
		} catch (HibernateException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			feedItem = null;
			System.out.println("error");
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}
		return false;
	}

	public ArrayList<NewsFeed> getFeedItems(Location location, int start, int count, int currentUserId) {
		ArrayList<NewsFeed> feedItems = new ArrayList<NewsFeed>();

		Session session = null;
		try {

			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();

			String sql = "SELECT * FROM NEWSFEED WHERE USER_ID_FIRST <> :user_id LIMIT :start , :count";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(NewsFeed.class);
			query.setParameter("user_id", currentUserId);
			query.setParameter("start", start);
			query.setParameter("count", count);
			java.util.List results = (java.util.List) query.list();

			for (Iterator iterator = ((java.util.List) results).iterator(); iterator.hasNext();) {
				feedItems.add((NewsFeed) iterator.next());
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
		return feedItems;
	}

	public int getFeedItemsCount(Location location) {
		ArrayList<NewsFeed> feedItems = new ArrayList<NewsFeed>();
		int count = 0;
		Session session = null;
		try {

			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();

			String sql = "SELECT * FROM NEWSFEED WHERE 1";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(NewsFeed.class);
			java.util.List results = (java.util.List) query.list();

			for (Iterator iterator = ((java.util.List) results).iterator(); iterator.hasNext();) {
				feedItems.add((NewsFeed) iterator.next());
				count++;
			}

			transaction.commit();
			session.close();

		} catch (HibernateException e) {
			System.out.println(e.getMessage());
			System.out.println("error");
			return 0;
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}
		return count;
	}

}

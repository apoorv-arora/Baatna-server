package com.application.baatna.dao;

import java.util.ArrayList;
import java.util.Iterator;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.application.baatna.bean.Coupon;
import com.application.baatna.bean.UserWish;
import com.application.baatna.util.CommonLib;
import com.application.baatna.util.DBUtil;

public class RedeemDao {

	public RedeemDao() {
	}

	/**
	 * Add any number of coupons with their limit
	 */
	public Coupon addCouponDetails(Coupon coupon) {
		Coupon mCoupon;
		Session session = null;
		try {
			session = DBUtil.getSessionFactory().openSession();

			Transaction transaction = session.beginTransaction();
			mCoupon = new Coupon();
			mCoupon.setCount(coupon.getCount());
			mCoupon.setImage(coupon.getImage());
			mCoupon.setName(coupon.getName());
			mCoupon.setTerms(coupon.getTerms());
			mCoupon.setValidity(coupon.getValidity());
//			mCoupon.setLimit(coupon.getLimit());

			session.save(mCoupon);
			transaction.commit();
			session.close();

		} catch (HibernateException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			mCoupon = null;
			System.out.println("error");
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}
		return mCoupon;
	}

	// Get all valid coupons.
	// for the userId fetch all the valid coupon Ids stored in UserWish table
	// for the coupon Id get the complete coupon object and return the same
	// get all available coupons
	public ArrayList<Coupon> getAllCoupons(int userId, int start, int count) {
		Session session = null;
		ArrayList<Coupon> coupons = new ArrayList<Coupon>();
		try {
			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();

			// get the number of times the user can avail all
			String sql = "SELECT * FROM USERWISH WHERE USERID = :userid AND CouponId = 0";// LIMIT :start , :count
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(UserWish.class);
			query.setParameter("userid", userId);
//			query.setParameter("start", start);
//			query.setParameter("count", count);

			java.util.List results = (java.util.List) query.list();

			for (Iterator iterator = ((java.util.List) results).iterator(); iterator.hasNext();) {

				UserWish wish = (UserWish) iterator.next();

				// Query in coupon table for the specified coupon
				String couponSql = "SELECT * FROM Coupon WHERE Count > 0";
				SQLQuery couponQuery = session.createSQLQuery(couponSql);
				couponQuery.addEntity(Coupon.class);

				java.util.List couponResults = (java.util.List) couponQuery.list();
				for (Iterator couponIterator = ((java.util.List) couponResults).iterator(); couponIterator.hasNext();) {
					coupons.add((Coupon) couponIterator.next());
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

		return coupons;
	}

	public int getCouponsCount(int userId) {
		Session session = null;
		ArrayList<Coupon> coupons = new ArrayList<Coupon>();
		try {
			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();

			// finding user info
			String sql = "SELECT * FROM USERWISH WHERE USERID = :userid AND CouponId = 0";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(UserWish.class);
			query.setParameter("userid", userId);

			java.util.List results = (java.util.List) query.list();

			for (Iterator iterator = ((java.util.List) results).iterator(); iterator.hasNext();) {

				UserWish wish = (UserWish) iterator.next();

				// Query in coupon table for the specified coupon
				String couponSql = "SELECT * FROM Coupon WHERE Count > 0";
				SQLQuery couponQuery = session.createSQLQuery(couponSql);
				couponQuery.addEntity(Coupon.class);

				java.util.List couponResults = (java.util.List) couponQuery.list();
				for (Iterator couponIterator = ((java.util.List) couponResults).iterator(); couponIterator.hasNext();) {
					coupons.add((Coupon) couponIterator.next());
				}
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

		return coupons.size();
	}

	//This is called when the user hit the redeem button.
	public boolean updateCouponOnRedeem(int userId, int couponId) {

		Session session = null;
		UserWish userWish = null;
		try {
			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();

			String sql = "SELECT * FROM USERWISH WHERE USERID = :userId";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(UserWish.class);
			query.setParameter("userId", userId);

			java.util.List results = (java.util.List) query.list();

			boolean found = false;
			for (Iterator iterator = ((java.util.List) results).iterator(); iterator.hasNext();) {
				userWish = (UserWish) iterator.next();
				// assign one coupon to this user
				if (userWish.getCouponId() == CommonLib.INVALID_COUPON) {
					found = true;
					userWish.setCouponId(couponId);
				}
			}

			if (found)
				session.update(userWish);

			transaction.commit();
			session.close();
			return found;

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

}

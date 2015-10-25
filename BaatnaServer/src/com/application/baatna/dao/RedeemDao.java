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
	public Object[] getAllCoupons(int userId) {
		Session session = null;
		Object[] couponList = new Object[2];
		ArrayList<Coupon> coupons = new ArrayList<Coupon>();
		try {
			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();

			// get the number of times the user can avail all
			String sql = "SELECT * FROM USERWISH WHERE USERID = :userid AND CouponId IS NULL";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(UserWish.class);
			query.setParameter("userid", userId);

			java.util.List results = (java.util.List) query.list();

			if (results != null && results.size() > 0) {
				// get all coupons and set the quantity to size.
				String couponSql = "SELECT * FROM Coupon WHERE Count > 0";
				SQLQuery couponQuery = session.createSQLQuery(couponSql);
				couponQuery.addEntity(Coupon.class);
				java.util.List couponResults = (java.util.List) couponQuery.list();
				for (Iterator couponIterator = ((java.util.List) couponResults).iterator(); couponIterator.hasNext();) {
					coupons.add((Coupon) couponIterator.next());
				}
			}

			couponList[0] = coupons;
			couponList[1] = results.size();

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

		return couponList;
	}

	// This is called when the user hit the redeem button.
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
				if (userWish.getCouponId() == null){//CommonLib.INVALID_COUPON) {
					found = true;
					userWish.setCouponId(""+couponId);
					break;
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
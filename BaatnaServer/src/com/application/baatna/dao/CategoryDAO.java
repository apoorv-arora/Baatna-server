package com.application.baatna.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.application.baatna.bean.Categories;
import com.application.baatna.util.CommonLib;
import com.application.baatna.util.DBUtil;

public class CategoryDAO {

	public CategoryDAO() {
		
		initialize();
	}
	
	private void initialize() {
		Session session = null;
		try {
			session = DBUtil.getSessionFactory().openSession();

			Transaction transaction = session.beginTransaction();
			
			String sql = "SELECT * FROM CATEGORIES";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(Categories.class);
			java.util.List results = (java.util.List) query.list();

			if(results != null && results.size() > 0)//already added.
				return;
			
			for(Categories category: CommonLib.getCategoriesList()) {
				session.save(category);
			}

			transaction.commit();
			session.close();

		} catch (HibernateException e) {
			System.out.println(e.getMessage());
			System.out.println("error");
		} finally {
			if(session != null && session.isOpen())
				session.close();
		}

	}
	/**
	 * Get user details based on the email\password combo. Used in case of
	 * login. TODO: Add check for access Token from session
	 * */
	public List<Categories> getCategories() {

		List<Categories> user = new ArrayList<Categories>();
		Session session = null;
		try {

			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();

			String sql = "SELECT * FROM CATEGORIES";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(Categories.class);
			java.util.List results = (java.util.List) query.list();

			for (Iterator iterator = ((java.util.List) results).iterator(); iterator
					.hasNext();) {
				user.add((Categories) iterator.next());
			}

			transaction.commit();
			session.close();

		} catch (HibernateException e) {
			System.out.println(e.getMessage());
			System.out.println("error");
		} finally {
			if(session != null && session.isOpen()) {
				session.close();
			}
		}

		return user;

	}

}

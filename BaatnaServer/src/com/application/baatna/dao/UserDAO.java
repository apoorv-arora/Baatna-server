package com.application.baatna.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.application.baatna.bean.Location;
import com.application.baatna.bean.User;
import com.application.baatna.util.CommonLib;
import com.application.baatna.util.DBUtil;

public class UserDAO {

	public UserDAO() {
	}

	/**
	 * Add user details to the DB for a newly signed up user. Updates User and
	 * Session tables.
	 */
	public User addUserDetails(String profilePic, String userName, String passWord, String email, String address,
			String phone, String bio, String fbId, String fbData, String fbToken, String fbPermissions) {
		User user;
		Session session = null;
		try {
			session = DBUtil.getSessionFactory().openSession();

			Transaction transaction = session.beginTransaction();
			user = new User();
			user.setUserName(userName);
			user.setPassWord(passWord);
			user.setEmail(email);
			user.setPhone(phone);
			user.setAddress(address);
			user.setProfilePic(profilePic);
			user.setBio(bio);
			user.setFacebookId(fbId);
			user.setFacebookData(fbData);
			user.setFacebookToken(fbToken);
			user.setFbPermission(fbPermissions);
			session.save(user);

			transaction.commit();
			session.close();

		} catch (HibernateException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			user = null;
			System.out.println("error");
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}

		return user;

	}

	/**
	 * Get user details based on the email\password combo. Used in case of
	 * login. TODO: Add check for access Token from session
	 */
	public User getUserDetails(String email, String passWord) {

		User user = null;
		Session session = null;
		try {

			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();
			user = new User();

			String sql = "SELECT * FROM USER WHERE EMAIL = :email_id && PASSW = :password";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(User.class);
			query.setParameter("email_id", email);
			query.setParameter("password", passWord);
			java.util.List results = (java.util.List) query.list();

			for (Iterator iterator = ((java.util.List) results).iterator(); iterator.hasNext();) {
				user = (User) iterator.next();
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

		return user;

	}

	/**
	 * Get user details based on the facebook id
	 */
	public User getUserDetails(String facebookId) {

		User user = null;
		Session session = null;
		try {

			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();
			user = new User();

			String sql = "SELECT * FROM USER WHERE FACEBOOKID = :facebookId";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(User.class);
			query.setParameter("facebookId", facebookId);
			java.util.List results = (java.util.List) query.list();

			for (Iterator iterator = ((java.util.List) results).iterator(); iterator.hasNext();) {
				user = (User) iterator.next();
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

		return user;

	}

	/**
	 * Get the user details based on the userId
	 */
	public User getUserDetails(int userId) {

		User user = null;
		Session session = null;
		try {
			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();
			user = new User();

			String sql = "SELECT * FROM USER WHERE USERID = :userid";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(User.class);
			query.setParameter("userid", userId);
			java.util.List results = (java.util.List) query.list();

			for (Iterator iterator = ((java.util.List) results).iterator(); iterator.hasNext();) {
				user = (User) iterator.next();
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

		return user;

	}

	/**
	 * Get session details for a particular userId and accessToken
	 */
	public com.application.baatna.bean.Session getSessionDetails(int userId, String accessToken) {
		com.application.baatna.bean.Session loginSession = null;
		Session session = null;
		try {

			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();
			loginSession = new com.application.baatna.bean.Session();

			// finding user info
			System.out.println("Getting Record");

			String sql = "SELECT * FROM SESSION WHERE USERID = :userid && ACCESS_TOKEN = :accessToken";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(User.class);
			query.setParameter("userid", userId);
			query.setParameter("accessToken", accessToken);
			java.util.List results = (java.util.List) query.list();

			for (Iterator iterator = ((java.util.List) results).iterator(); iterator.hasNext();) {
				loginSession = (com.application.baatna.bean.Session) iterator.next();
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

		return loginSession;

	}

	/**
	 * Utility method to generate an accessToken.
	 */
	public String generateAccessToken(int userId) {

		String accessToken = "";
		Transaction transaction = null;
		Session session = null;
		try {

			session = DBUtil.getSessionFactory().openSession();
			transaction = session.beginTransaction();

			Random rand = new Random();

			int aT = 0;

			for (int i = 0; i < 6; i++) {

				aT = aT * 10 + rand.nextInt(10);

			}
			accessToken = aT + "";
			// setting access token for the user

			String sql = "UPDATE SESSION SET ACCESS_TOKEN = " + accessToken + "  WHERE USERID = " + userId;
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(User.class);
			int result = query.executeUpdate();

			transaction.commit();
			session.close();

		} catch (HibernateException e) {
			transaction.rollback();
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.out.println("error");
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}

		return accessToken;

	}

	/**
	 * Delete an accessToken for a particular user
	 */
	public boolean nullifyAccessToken(int userId, String accessToken) {

		Session session = null;

		try {

			session = DBUtil.getSessionFactory().openSession();

			Transaction transaction = session.beginTransaction();

			String sql = "DELETE FROM SESSION WHERE ACCESS_TOKEN = :access_token && USERID = :user_id";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(com.application.baatna.bean.Session.class);
			query.setParameter("access_token", accessToken);
			query.setParameter("user_id", userId);
			int result = query.executeUpdate();
			CommonLib.BLog(result + "");
			transaction.commit();
			session.close();
			return true;

		} catch (HibernateException e) {
			System.out.println(e.getMessage());
			System.out.println("error");
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}

		return false;
	}

	/**
	 * Removes the push notification Id
	 */
	public int nullifyPushId(int userId) {

		Session session = null;

		int pushId = 0;

		try {

			session = DBUtil.getSessionFactory().openSession();

			Transaction transaction = session.beginTransaction();

			String sql = "DELETE FROM SESSION WHERE PUSHID = :pushId && USERID = :user_id";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(com.application.baatna.bean.Session.class);
			query.setParameter("pushId", pushId);
			query.setParameter("user_id", userId);
			int result = query.executeUpdate();

			transaction.commit();
			session.close();

		} catch (HibernateException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.out.println("error");
			return -1;
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}

		return pushId;

	}

	public boolean UserExists(String email) {

		User user = null;
		Session session = null;
		try {
			session = DBUtil.getSessionFactory().openSession();

			Transaction transaction = session.beginTransaction();

			user = new User();

			int i = 0;

			String sql = "SELECT * FROM USER WHERE EMAIL = :email_id";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(User.class);
			query.setParameter("email_id", email);
			java.util.List results = (java.util.List) query.list();

			for (Iterator iterator = ((java.util.List) results).iterator(); iterator.hasNext();) {
				user = (User) iterator.next();
				i++;
			}

			transaction.commit();
			session.close();

			if (i > 0)
				return true;
			else
				return false;

		} catch (HibernateException e) {
			System.out.println(e.getMessage());
			System.out.println("error");
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}

		return false;

	}

	public int userActive(String accessToken) {

		com.application.baatna.bean.Session user = null;
		Session session = null;
		try {
			session = DBUtil.getSessionFactory().openSession();

			Transaction transaction = session.beginTransaction();

			user = new com.application.baatna.bean.Session();

			int i = 0;

			String sql = "SELECT * FROM SESSION WHERE ACCESS_TOKEN = :access_token";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(com.application.baatna.bean.Session.class);
			query.setParameter("access_token", accessToken);
			java.util.List results = (java.util.List) query.list();

			if (results != null) {
				for (Iterator iterator = ((java.util.List) results).iterator(); iterator.hasNext();) {
					user = (com.application.baatna.bean.Session) iterator.next();
					i = user.getUserId();

				}
			}

			transaction.commit();
			session.close();
			if (i > 0)
				return i;
			else
				return 0;

		} catch (HibernateException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.out.println("error");
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}

		return 0;

	}

	public boolean updatePushId(String pushId, String accessToken) {

		Session session = null;

		try {

			session = DBUtil.getSessionFactory().openSession();

			Transaction transaction = session.beginTransaction();

			String sql = "SELECT * FROM SESSION WHERE ACCESS_TOKEN = :access_token";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(com.application.baatna.bean.Session.class);
			query.setParameter("access_token", accessToken);
			java.util.List results = (java.util.List) query.list();
			com.application.baatna.bean.Session currentSession = (com.application.baatna.bean.Session) results.get(0);
			currentSession.setPushId(pushId);
			session.update(currentSession);

			transaction.commit();
			session.close();
		} catch (HibernateException e) {
			System.out.println(e.getMessage());
			System.out.println("error");
			e.printStackTrace();
			return false;
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}

		return true;

	}

	public boolean updateLocation(double lat, double lon, String accessToken) {

		Session session = null;

		try {

			session = DBUtil.getSessionFactory().openSession();

			Transaction transaction = session.beginTransaction();

			String sql = "SELECT * FROM SESSION WHERE ACCESS_TOKEN = :access_token";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(com.application.baatna.bean.Session.class);
			query.setParameter("access_token", accessToken);
			java.util.List results = (java.util.List) query.list();
			com.application.baatna.bean.Session currentSession = (com.application.baatna.bean.Session) results.get(0);
			Location location = new Location(lat, lon);
			currentSession.setLocation(location);
			session.update(currentSession);

			transaction.commit();
			session.close();
		} catch (HibernateException e) {
			System.out.println(e.getMessage());
			System.out.println("error");
			e.printStackTrace();
			return false;
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}

		return true;

	}

	public boolean editUserDetails(int userId, String userName, String passWord, String email, String address,
			String phone, String bio) {

		Session session = null;
		try {
			session = DBUtil.getSessionFactory().openSession();

			Transaction transaction = session.beginTransaction();
			User user = getUserDetails(userId);

			user.setUserName(userName);
			user.setPassWord(passWord);
			user.setEmail(email);
			user.setPhone(phone);
			user.setAddress(address);
			user.setBio(bio);

			session.save(user);
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

		return true;

	}

	public boolean editProfilePic(int userId, String profilePic) {

		Session session = null;
		try {
			session = DBUtil.getSessionFactory().openSession();

			Transaction transaction = session.beginTransaction();
			String sql = "UPDATE USER SET PROFILE_PIC = :profilePic  WHERE USERID = :user_id";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(User.class);
			query.setParameter("profilePic", profilePic);
			query.setParameter("user_id", userId);
			int result = query.executeUpdate();

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

		return true;

	}

	public boolean verifyUserEmail(User user, boolean isVerified) {

		Session session = null;
		try {
			session = DBUtil.getSessionFactory().openSession();

			Transaction transaction = session.beginTransaction();
			user.setIsVerified(isVerified ? 1 : 0);

			session.save(user);
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

		return true;

	}

	public boolean addSession(int userId, String accessToken, String registrationId, Location location) {
		Session session = null;
		try {
			session = DBUtil.getSessionFactory().openSession();

			Transaction transaction = session.beginTransaction();
			com.application.baatna.bean.Session loginSession = new com.application.baatna.bean.Session();
			loginSession.setUserId(userId);

			loginSession.setAccessToken(accessToken);
			loginSession.setLocation(location);
			loginSession.setPushId(registrationId);

			session.save(loginSession);

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

		return true;
	}

	/**
	 * Get nearby user's session, so as to provide location
	 */
	public ArrayList<com.application.baatna.bean.Session> getNearbyUsers() {

		ArrayList<com.application.baatna.bean.Session> users = null;

		Session session = null;
		try {

			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();
			users = new ArrayList<com.application.baatna.bean.Session>();

			String sql = "SELECT * FROM SESSION LIMIT 50";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(com.application.baatna.bean.Session.class);
			java.util.List results = (java.util.List) query.list();

			for (Iterator iterator = ((java.util.List) results).iterator(); iterator.hasNext();) {
				users.add((com.application.baatna.bean.Session) iterator.next());
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
		return users;
	}

	/**
	 * Get nearby users object
	 */
	public ArrayList<User> getNearbyUsers(Location location) {

		ArrayList<User> users = null;

		return users;
	}

	public boolean updateInstitution(String institutionName, String studentId, int userId, int isInstitutionVerified) {

		Session session = null;

		try {

			session = DBUtil.getSessionFactory().openSession();

			Transaction transaction = session.beginTransaction();

			String sql = "SELECT * FROM USER WHERE USERID = :userId";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(User.class);
			query.setParameter("userId", userId);
			java.util.List results = (java.util.List) query.list();
			User currentSession = (User) results.get(0);
			currentSession.setInstitutionName(institutionName);
			currentSession.setStudentId(studentId);
			currentSession.setIsInstitutionVerified(isInstitutionVerified);
			session.update(currentSession);
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

	public boolean validateInstitution(String institutionName) {
		for (String name : CommonLib.getInstitutionsList()) {
			if (institutionName.equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

}

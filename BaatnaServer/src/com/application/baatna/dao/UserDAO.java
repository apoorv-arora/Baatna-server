package com.application.baatna.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.application.baatna.bean.Institution;
import com.application.baatna.bean.Location;
import com.application.baatna.bean.User;
import com.application.baatna.bean.UserWish;
import com.application.baatna.bean.Wish;
import com.application.baatna.util.CommonLib;
import com.application.baatna.util.DBUtil;
import com.application.baatna.util.JsonUtil;
import com.application.baatna.util.PushModel;
import com.application.baatna.util.PushUtil;
import com.application.baatna.util.facebook.Friends;

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
			user.setTimestamp(System.currentTimeMillis());
			user.setModified(0);
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

	public com.application.baatna.bean.Session getSession(int userId) {
		com.application.baatna.bean.Session loginSession = null;
		Session session = null;
		try {

			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();
			loginSession = new com.application.baatna.bean.Session();

			// finding user info
			System.out.println("Getting Record");

			String sql = "SELECT * FROM SESSION WHERE USERID = :userid";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(com.application.baatna.bean.Session.class);
			query.setParameter("userid", userId);
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

			long aT = 0;

			for (int i = 0; i < 6; i++) {

				aT = aT * 10 + rand.nextInt(10);

			}
			accessToken = aT + "" + (System.currentTimeMillis() / 100);
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
			currentSession.setModified(System.currentTimeMillis());
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
			currentSession.setModified(System.currentTimeMillis());
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
			user.setModified(System.currentTimeMillis());

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
			String sql = "UPDATE USER SET PROFILE_PIC = :profilePic, MODIFIED = :modified   WHERE USERID = :user_id";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(User.class);
			query.setParameter("profilePic", profilePic);
			query.setParameter("user_id", userId);
			query.setParameter("modified", System.currentTimeMillis());
			int result = query.executeUpdate();

			User user = getUserDetails(userId);
			user.setModified(System.currentTimeMillis());

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
			loginSession.setCreated(System.currentTimeMillis());
			loginSession.setModified(0);

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
	public ArrayList<com.application.baatna.bean.Session> getNearbyUsers(double latitude, double longitude) {

		ArrayList<com.application.baatna.bean.Session> users = null;

		Session session = null;
		try {

			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();
			users = new ArrayList<com.application.baatna.bean.Session>();

			String sql = "Select * from SESSION order by "
					+ "3956 * 2 * ASIN(SQRT(POWER(SIN((:latitude - SESSION.LATITUDE) * pi()/180 / 2), 2) + COS(:latitude * pi()/180) * COS(SESSION.LATITUDE * pi()/180) * POWER(SIN((:longitude - SESSION.LONGITUDE) * pi()/180 / 2), 2)))"
					+ "limit :count";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(com.application.baatna.bean.Session.class);
			query.setParameter("latitude", latitude);
			query.setParameter("longitude", longitude);
			query.setParameter("count", 1000);
			
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

	public ArrayList<com.application.baatna.bean.Session> getUsersNotEquals(int userId) {

		ArrayList<com.application.baatna.bean.Session> users = null;

		Session session = null;
		User user;
		boolean shouldAdd= false;
		UserDAO dao= new UserDAO();
		User currentUser= new User();
		currentUser=dao.getUserDetails(userId);
		try {

			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();
			users = new ArrayList<com.application.baatna.bean.Session>();

			user= new User();
			
			
			com.application.baatna.bean.Session userSession = dao.getAllSessions(userId).get(0);
			String sql = "Select * from SESSION order by"
					+"3956 * 2 * ASIN(SQRT(POWER(SIN((:latitude - SESSION.LATITUDE) * pi()/180 / 2), 2) + COS(:latitude * pi()/180) * COS(SESSION.LATITUDE * pi()/180) * POWER(SIN((longitude - SESSION.LONGITUDE) * pi()/180 / 2), 2)))"
					+"limit 500";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(com.application.baatna.bean.Session.class);
			query.setParameter("user_id", userId);
			query.setParameter("latitude",userSession.getLocation().getLatitude());
			query.setParameter("longitude", userSession.getLocation().getLongitude());
			java.util.List results2 = (java.util.List) query.list();

			
			for (Iterator iterator = ((java.util.List) results2).iterator(); iterator.hasNext();) {
				com.application.baatna.bean.Session currentSesion = (com.application.baatna.bean.Session) iterator
						.next();
				user=(User)iterator.next();
				if( CommonLib.isFacebookCheckValid ) {
					shouldAdd = Friends.isFriendOnFacebook(user.getFacebookId(),
							currentUser.getFacebookId(), user.getFacebookToken());
				}
				if (shouldAdd)
				{
					users.add(currentSesion);
				}
				
//				users.add(currentSesion);
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

	public ArrayList<com.application.baatna.bean.Session> getNearbyUsers(int userId) {

		ArrayList<com.application.baatna.bean.Session> users = null;

		Session session = null;
		try {

			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();
			users = new ArrayList<com.application.baatna.bean.Session>();

			String sql = "SELECT * FROM SESSION WHERE USERID = :user_id LIMIT 50";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(com.application.baatna.bean.Session.class);
			query.setParameter("user_id", userId);
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

	public boolean updateInstitution(String institutionName, String studentId, int userId, int isInstitutionVerified,
			String branchName, int year, String phoneNumber) {

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
			currentSession.setPhoneNumber(phoneNumber);
			currentSession.setYear(year);
			currentSession.setBranchName(branchName);
			currentSession.setIsInstitutionVerified(isInstitutionVerified);
			currentSession.setModified(System.currentTimeMillis());
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
		for (Institution name : CommonLib.getInstitutionsList()) {
			if (institutionName.equalsIgnoreCase(name.getInstitutionName())) {
				return true;
			}
		}
		if (institutionName.equalsIgnoreCase("-1"))
			return true;
		return false;
	}

	public boolean setRatingForUser(int currentUser, int userId, double rating,int wishId,int userWishId) {
		Session session = null;
		boolean retVal = false;

		try {

			session = DBUtil.getSessionFactory().openSession();

			Transaction transaction = session.beginTransaction();
			
			{
				if(currentUser==userWishId)
				{
				String sql="UPDATE USERWISH SET U1RATEDU2= :rating WHERE USERID= :currentUser AND USER_TWO_ID= :userId AND WISHID= :wishId";
				SQLQuery query = session.createSQLQuery(sql);
				query.addEntity(UserWish.class);
				query.setParameter("userId", userId);
				query.setParameter("currentUser", currentUser);
				query.setParameter("wishId",wishId);
				query.setParameter("rating", rating);
				
				query.executeUpdate();
				}
				else if(userId==userWishId)
				{
					String sql="UPDATE USERWISH SET U2RATEDU1= :rating WHERE USERID= :userId AND USER_TWO_ID= :currentUser AND WISHID= :wishId";
					SQLQuery query = session.createSQLQuery(sql);
					query.addEntity(UserWish.class);
					query.setParameter("userId", userId);
					query.setParameter("currentUser", currentUser);
					query.setParameter("wishId",wishId);
					query.setParameter("rating", rating);
					query.executeUpdate();	
				}
			}

			/*{
			String sql = "SELECT count(*) FROM USERRATING WHERE Reviewed= :userId AND Reviewer= :userIdtwo";
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter("userId", userId);
			query.setParameter("userIdtwo", currentUser);
			java.util.List results = (java.util.List) query.list();
			if (results.get(0) instanceof BigInteger) {
				int count = ((BigInteger) results.get(0)).intValue();
				retVal = true;
				if (count == 0) {
					String sql2 = "INSERT INTO USERRATING(Reviewer,Reviewed,Rating) VALUES(:reviewer,:reviewed,:rating)";
					SQLQuery query2 = session.createSQLQuery(sql2);
					query2.addEntity(com.application.baatna.bean.UserRating.class);
					query2.setParameter("reviewer", currentUser);
					query2.setParameter("reviewed", userId);
					query2.setParameter("rating", rating);
					query2.executeUpdate();
				} else if (count == 1) {
					String sql2 = "Update USERRATING Set Rating = :rating where Reviewer = :reviewer and Reviewed = :reviewed";
					SQLQuery query2 = session.createSQLQuery(sql2);
					query2.addEntity(com.application.baatna.bean.UserRating.class);
					query2.setParameter("rating", rating);
					query2.setParameter("reviewer", currentUser);
					query2.setParameter("reviewed", userId);
					query2.executeUpdate();
					retVal = true;
				}
			}
			}*/
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
		return retVal;

	}

	/*public boolean editRatingForUser(int userIdtwo, int userId, double rating) {
		Session session = null;
		boolean retVal = false;

		try {

			session = DBUtil.getSessionFactory().openSession();

			Transaction transaction = session.beginTransaction();

			String sql = "SELECT count(*) FROM USERRATING WHERE USERID_ONE= :userId AND USERID_TWO= :userIdtwo";
			// String sql = "UPDATE USERRATING SET Rating = :rating, WHERE
			// USERID = :user_id";
			SQLQuery query = session.createSQLQuery(sql);
			// query.addEntity(com.application.baatna.bean.UserRating.class);
			query.setParameter("userId", userId);
			query.setParameter("userIdtwo", userIdtwo);
			java.util.List results = (java.util.List) query.list();
			if (results.get(0) instanceof BigInteger) {
				int count = ((BigInteger) results.get(0)).intValue();
				if (count != 0) {
					String sql2 = "UPDATE USERRATING SET RATING= :rating WHERE USERID_ONE= :userId AND USERID_TWO= :userIdtwo";
					SQLQuery query2 = session.createSQLQuery(sql2);
					query2.addEntity(com.application.baatna.bean.UserRating.class);
					query2.setParameter("userId", userId);
					query2.setParameter("userIdtwo", userIdtwo);
					query2.setParameter("rating", rating);
					query2.executeUpdate();
					retVal = true;
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
		return retVal;

	}*/

	public void setUserDayRating() {

		Session session = null;
		double rating = 0;
		int userId;
		double avg1;
		double avg2;

		try {
			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();

			//String sql = "SELECT er.Reviewed, (Select avg(Rating) as AvgRating from UserRating ur where ur.Reviewed = er.Reviewed) temp from UserRating er";
		    String sql="SELECT er.USERID,(SELECT avg(U2RATEDU1) AS AvgRating from USERWISH uw where uw.USERID=er.USERID and uw.Wish_Status=er.Wish_Status and uw.U2RATEDU1<>:b) avg1, (SELECT avg(U1RATEDU2) AS AvgRatingTwo from USERWISH aw WHERE aw.USER_TWO_ID=er.USERID and aw.Wish_Status=er.Wish_Status and aw.U1RATEDU2<>:a) avg2 from USERWISH er WHERE er.WISH_STATUS= :status";
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter("a", 0);
			query.setParameter("b", 0);
			query.setParameter("status",CommonLib.STATUS_FULLFILLED);
		    query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
	
			List results=query.list();
			for(Object object:results)
			{
				Map row=(Map)object;
				userId=(int)row.get("USERID");
				if(row.get("avg1")!=null)
				avg1=(double)row.get("avg1");
				else
					avg1=0;
				if(row.get("avg2")!=null)
				avg2=(double)row.get("avg2");
				else
					avg2=0;
				if(avg1!=0 && avg2 !=0)
				rating=(avg1+avg2)/2;
				else
					rating=Math.max(avg1, avg2);
				if(rating!= 0){
				String sql2="UPDATE USER SET RATING= :rating WHERE USERID= :userId";
				SQLQuery query2=session.createSQLQuery(sql2);
				query2.addEntity(User.class);
				query2.setParameter("rating", rating);
				query2.setParameter("userId", userId);
				
				query2.executeUpdate();
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

	}

	public ArrayList<com.application.baatna.bean.Session> getAllSessions(int userId){

		//all sessions of user userId
		ArrayList<com.application.baatna.bean.Session> users = null;

		Session session = null;
		try {

			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();
			users = new ArrayList<com.application.baatna.bean.Session>();

			String sql = "SELECT * FROM SESSION WHERE USERID = :user_id ;";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(com.application.baatna.bean.Session.class);
			query.setParameter("user_id", userId);
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

	public boolean usersEverInteracted(int currentUserId, int userId) {
		Session session = null;
		boolean retVal = false;
		try {

			session = DBUtil.getSessionFactory().openSession();

			Transaction transaction = session.beginTransaction();

			String sql = "SELECT count(*) FROM USERWISH WHERE ( USERID = :currentUserId and USER_TWO_ID =:userId) or (USERID = :currentUserId1 and USER_TWO_ID =:userId1)";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(User.class);
			query.setParameter("currentUserId", currentUserId);
			query.setParameter("userId", userId);
			query.setParameter("currentUserId1", userId);
			query.setParameter("userId1", currentUserId);

			java.util.List results = (java.util.List) query.list();
			// check if the current user can rate the userId.
			if (results.get(0) instanceof BigInteger && ((BigInteger) results.get(0)).intValue() > 0) {
				retVal = true;
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
		return retVal;

	}

	/**
	 * Get the user details based on the userId
	 */
	public Object[] getRating(int currentUserId, int userId) {

		Object[] objects = new Object[2];
		Session session = null;
		try {
			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();

			String sql = "SELECT count(*) FROM USERWISH WHERE ( USERID = :currentUserId and USER_TWO_ID =:userId) or (USERID = :currentUserId1 and USER_TWO_ID =:userId1)";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(User.class);
			query.setParameter("currentUserId", currentUserId);
			query.setParameter("userId", userId);
			query.setParameter("currentUserId1", userId);
			query.setParameter("userId1", currentUserId);

			java.util.List results = (java.util.List) query.list();
			// check if the current user can rate the userId.
			if (results.get(0) instanceof BigInteger && ((BigInteger) results.get(0)).intValue() > 0) {
				objects[0] = true;
				// fetch the previous rating value
				String userRatingSql = "SELECT Rating FROM UserRating WHERE Reviewer =:reviewer and Reviewed = :reviewed";
				SQLQuery userRatingQuery = session.createSQLQuery(userRatingSql);
				userRatingQuery.addEntity(User.class);
				userRatingQuery.setParameter("reviewer", currentUserId);
				userRatingQuery.setParameter("reviewed", userId);
				java.util.List userRatingResults = (java.util.List) query.list();
				if (userRatingResults.get(0) instanceof BigInteger) {
					objects[1] = ((BigInteger) results.get(0)).intValue();
				}
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

		return objects;
	}

	public void sendPushToAllSessions(JSONObject notification, int userId){

		//send to all sessions of user userId
		ArrayList<com.application.baatna.bean.Session> users = getAllSessions(userId);

		PushModel pushModel = new PushModel();
		pushModel.setNotification(notification);
		PushUtil pushUtil = PushUtil.getInstance();

			for (com.application.baatna.bean.Session user : users) {
				// send push notif to all
				pushModel.setPushId(user.getPushId());
				pushUtil.sendPush(pushModel);
			}

			
	}

	/*public ArrayList<com.application.baatna.bean.User> usersToBeRated(int userId) {
		
		Session session=null;
		ArrayList<com.application.baatna.bean.User>users=null;
		try
		{
			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();

			users= new ArrayList<com.application.baatna.bean.User>();
			String sql = "SELECT * FROM USER WHERE USERID IN (SELECT USER_TWO_ID FROM USERWISH WHERE USERID= :userId AND WISH_STATUS= :status)";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(com.application.baatna.bean.User.class);
			query.setParameter("userId", userId);
			query.setParameter("status", CommonLib.STATUS_ACCEPTED);
			

			java.util.List results = (java.util.List) query.list();
			UserDAO dao= new UserDAO();
			for (Iterator iterator = ((java.util.List) results).iterator(); iterator.hasNext();) {
				users.add((com.application.baatna.bean.User)iterator.next());
			}
			
			String sql2="SELECT * FROM USER WHERE USERID IN (SELECT USERID FROM USERWISH WHERE USER_TWO_ID= :userIdone AND WISH_STATUS= :statusone)";
			SQLQuery query2=session.createSQLQuery(sql2);
			query2.addEntity(com.application.baatna.bean.User.class);
			query2.setParameter("userIdone",userId);
			query2.setParameter("statusone",CommonLib.STATUS_ACCEPTED);
			
			java.util.List results2=(java.util.List)query2.list();
			for (Iterator iterator = ((java.util.List) results2).iterator(); iterator.hasNext();) {
				users.add((com.application.baatna.bean.User)iterator.next());
			}
			transaction.commit();
			session.close();
		}catch (HibernateException e) {
			System.out.println(e.getMessage());
			System.out.println("error");
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}

		return users;
	}*/
	public JSONArray usersToBeRated(int userId)
	{
		Session session=null;
		JSONArray usersJson= new JSONArray();
		ArrayList<com.application.baatna.bean.User>users=null;
		try
		{
			session = DBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();
			
			{
			String sql = "SELECT * FROM USERWISH WHERE USERID=:userId AND WISH_STATUS= :status";
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(com.application.baatna.bean.UserWish.class);
			query.setParameter("userId", userId);
			query.setParameter("status", CommonLib.STATUS_FULLFILLED); 
			
			
			java.util.List results= (java.util.List)query.list();
			for (Iterator iterator = ((java.util.List) results).iterator(); iterator.hasNext();) {
				User user= null;
				Wish wish= null;
				UserWish userWish= (UserWish)iterator.next();
				String sql2="SELECT * FROM USER WHERE USERID= :userId";
				SQLQuery query2 = session.createSQLQuery(sql2);
				query2.addEntity(User.class);
				query2.setParameter("userId",userWish.getUserTwoId());
				
				java.util.List results2=(java.util.List)query2.list();
				for (Iterator iterator2 = ((java.util.List) results2).iterator(); iterator2.hasNext();)
				{
					user=(User)iterator2.next();
					break;
				}
				
				String sql3="SELECT * FROM WISH WHERE USERID= :userId AND WISHID= :wishId";
				SQLQuery query3=session.createSQLQuery(sql3);
				query3.addEntity(Wish.class);
				query3.setParameter("userId",userWish.getUserId());
				query3.setParameter("wishId", userWish.getWishId());
				
				java.util.List results3=(java.util.List)query3.list();
				for (Iterator iterator3 = ((java.util.List) results3).iterator(); iterator3.hasNext();)
				{
					wish=(Wish)iterator3.next();
					break;
				}
				
				try{
					if(user!=null && wish!=null){
				JSONObject userWishJson=JsonUtil.getUserWishJson(user, wish);
				usersJson.put(userWishJson);}
				}catch(JSONException e)
				{
					e.printStackTrace();
				}
			}
			}
			{
				String sql = "SELECT * FROM USERWISH WHERE USER_TWO_ID=:userId AND WISH_STATUS= :status";
				SQLQuery query = session.createSQLQuery(sql);
				query.addEntity(com.application.baatna.bean.UserWish.class);
				query.setParameter("userId", userId);
				query.setParameter("status", CommonLib.STATUS_FULLFILLED); 
				
				
				java.util.List results= (java.util.List)query.list();
				for (Iterator iterator = ((java.util.List) results).iterator(); iterator.hasNext();) {
					User user= null;
					Wish wish= null;
					UserWish userWish= (UserWish)iterator.next();
					String sql2="SELECT * FROM USER WHERE USERID= :userId";
					SQLQuery query2 = session.createSQLQuery(sql2);
					query2.addEntity(User.class);
					query2.setParameter("userId",userWish.getUserId());
					
					java.util.List results2=(java.util.List)query2.list();
					for (Iterator iterator2 = ((java.util.List) results2).iterator(); iterator2.hasNext();)
					{
						user=(User)iterator2.next();
						break;
					}
					
					String sql3="SELECT * FROM WISH WHERE USERID= :userId AND WISHID= :wishId";
					SQLQuery query3=session.createSQLQuery(sql3);
					query3.addEntity(Wish.class);
					query3.setParameter("userId",userWish.getUserId());
					query3.setParameter("wishId", userWish.getWishId());
					
					java.util.List results3=(java.util.List)query3.list();
					for (Iterator iterator3 = ((java.util.List) results3).iterator(); iterator3.hasNext();)
					{
						wish=(Wish)iterator3.next();
						break;
					}
					
					try{
						if(user!=null && wish!=null){
					JSONObject userWishJson=JsonUtil.getUserWishJson(user, wish);
					usersJson.put(userWishJson);}
					}catch(JSONException e)
					{
						e.printStackTrace();
					}
				}
			}
		
	} catch (HibernateException e) {
		System.out.println(e.getMessage());
		System.out.println("error");
		e.printStackTrace();

	} finally {
		if (session != null && session.isOpen())
			session.close();
	}
		return usersJson;
	
}
}

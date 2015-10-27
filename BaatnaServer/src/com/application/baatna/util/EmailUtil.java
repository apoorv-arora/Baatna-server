package com.application.baatna.util;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.mail.EmailException;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.application.baatna.bean.User;
import com.application.baatna.types.EmailType;

public class EmailUtil {

	private static final String hostName = "smtp.gmail.com";
	private static final int portNumber = 465;
	private static final String senderEmailId = "android.notifications@gmail.com";

	/**
	 * @param receivers_email_id
	 *            Recipient's email ID
	 * @param name
	 *            Recipient's name
	 * @param Email
	 *            type
	 */
	public static void sendEmail(String receivers_email_id, User user, EmailType emailType) throws EmailException {

		// Get system properties
		Properties properties = new Properties();

		// Setup mail server
		properties.setProperty("mail.smtp.host", hostName);
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.port", portNumber);
		properties.put("mail.smtp.auth", "true");

		// Get the default Session object.
		Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("android@baatna.com", "android.baatna");
			}
		});

		session.setDebug(true);

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(senderEmailId));

			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(receivers_email_id));

			// Set Subject: header field
			message.setSubject("This is the Subject Line!");

			JSONObject userJsonObject = new JSONObject();
			userJsonObject.put("user_id", user.getUserId() + "");
			CryptoHelper helper = new CryptoHelper();
			String blob = helper.encrypt(userJsonObject.toString(), null, null);
			String verifyUrl = CommonLib.SERVER_WITHOUT_VERSION + "/user/verifyEmail?blob=" + blob;

			message.setText("Hello, this is sample for to check send " + "email using JavaMailAPI " + verifyUrl);
			// Send message
			Transport.send(message);
			System.out.println("Sent message successfully....");
		} catch (MessagingException mex) {
			mex.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void sendFeedback( String receivers_email_id, String log, String text, EmailType emailType) throws EmailException {

		// Get system properties
		Properties properties = new Properties();

		// Setup mail server
		properties.setProperty("mail.smtp.host", hostName);
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.port", portNumber);
		properties.put("mail.smtp.auth", "true");

		// Get the default Session object.
		Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("android@baatna.com", "android.baatna");
			}
		});

		session.setDebug(true);

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(senderEmailId));

			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(receivers_email_id));

			// Set Subject: header field
			message.setSubject("Baatna Android App Feedback");

			message.setText("" + text + "\n\n\n" + log);
			// Send message
			Transport.send(message);
			System.out.println("Sent message successfully....");
		} catch (MessagingException mex) {
			mex.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
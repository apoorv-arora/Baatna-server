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

import com.application.baatna.types.EmailType;

public class EmailUtil {

	private static final String hostName = "smtp.gmail.com";
	private static final int portNumber = 465;
	private static final String senderEmailId = "android@baatna.com";

	/**
	 * @param receivers_email_id
	 *            Recipient's email ID
	 * @param name
	 *            Recipient's name
	 * @param Email
	 *            type
	 */
	public static void sendEmail(String receivers_email_id, EmailType emailType) throws EmailException {

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

		session.setDebug(false);

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(senderEmailId));

			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(receivers_email_id));

			// Set Subject: header field
			message.setSubject("Welcome to your local Baatna Community!!");

			// JSONObject userJsonObject = new JSONObject();
			// userJsonObject.put("user_id", user.getUserId() + "");
			// CryptoHelper helper = new CryptoHelper();
			// String blob = helper.encrypt(userJsonObject.toString(), null,
			// null);
			// String verifyUrl = CommonLib.SERVER_WITHOUT_VERSION +
			// "/user/verifyEmail?blob=" + blob;

			String text = "Hey,"
					+ "\n\nWelcome to Baatna! Thank you for becoming a member of the local Baatna community!"
					+ "\n\nBaatna enables you to borrow the things you need from people in your neighborhood. Right here, right now, for free."
					+ "\n\nHow does it work?"
					+ "\n\nYou know those moments where you need to use something that you do not need to own? Tell Baatna what you are looking for and we'll find friendly neighbors willing to lend it to you. Looking for something right now? Just go to the app and post your need."
					+ "\n\nIn return you can share your stuff when it's convenient. If one of your neighbors is looking for something, we will let you know. It's up to you if you want to lend out your stuff. Be an awesome neighbor and share the love!"
					+ "\n\nWe're doing our best to make Baatna more efficient and useful for you everyday. Incase you have any feedback, please get back to us at - android@baatna.com."
					+ "\nWe would love to hear from you." + "\n\nCheers" + "\nBaatna Team";

			message.setText(text);
			// Send message
			Transport.send(message);
			System.out.println("Sent message successfully....");
		} catch (MessagingException mex) {
			mex.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void sendFeedback(String receivers_email_id, String log, String text, EmailType emailType)
			throws EmailException {

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
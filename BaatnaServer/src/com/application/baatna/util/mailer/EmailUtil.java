package com.application.baatna.util.mailer;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.application.baatna.util.CommonLib;

/**
 * Singleton class for sending emails.
 */
public class EmailUtil {

	private static volatile EmailUtil sInstance;
	private final String HOST_NAME = "smtp.gmail.com";
	private final int PORT_NUMBER = 465;
	private final String START_TLS_ENABLED = "true";
	private final String AUTH_FLAG = "true";

	/**
	 * Empty constructor to prevent multiple objects in memory
	 */
	private EmailUtil() {
	}

	/**
	 * Implementation of double check'd locking scheme.
	 */
	public static EmailUtil getInstance() {

		if (sInstance == null) {
			synchronized (EmailUtil.class) {
				if (sInstance == null) {
					sInstance = new EmailUtil();
				}
			}
		}
		return sInstance;
	}

	/**
	 * Function - send a mail as per detailed in the model in a separate thread.
	 * 
	 * @param emailModel
	 */
	public void sendEmail(EmailModel emailModel) {

		Runnable runnable = new Runnable() {
			public void run() {
				// Get system properties
				Properties properties = new Properties();

				// Setup mail server
				properties.setProperty("mail.smtp.host", HOST_NAME);
				properties.put("mail.smtp.starttls.enable", START_TLS_ENABLED);
				properties.put("mail.smtp.port", PORT_NUMBER);
				properties.put("mail.smtp.auth", AUTH_FLAG);

				// Get the default Session object.
				Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(CommonLib.BAPP_ID, CommonLib.BAPP_PWD);
					}
				});

				// Enable the debugger for any logs
				session.setDebug(false);

				try {
					// Create a MimeMessage object.
					MimeMessage message = new MimeMessage(session);

					// Set From: header field of the header.
					message.setFrom(new InternetAddress(emailModel.getFrom()));

					// Set To: header field of the header.
					message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailModel.getTo()));

					// Set Subject: header field
					message.setSubject(emailModel.getSubject());

					// Set Content: header field
					message.setText(emailModel.getContent());

					// Let's try sending the object
					Transport.send(message);

				} catch (MessagingException mex) {
					mex.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		Thread newThread = new Thread(runnable);
		newThread.start();
	}

}
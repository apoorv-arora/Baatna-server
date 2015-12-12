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

import com.application.baatna.model.EmailModel;
import com.application.baatna.types.EmailType;

public class EmailUtil {

	private static final String hostName = "smtp.gmail.com";
	private static final int portNumber = 465;
	public static final String senderEmailId = "hello@baatna.com";

	/**
	 * @param receivers_email_id
	 *            Recipient's email ID
	 * @param name
	 *            Recipient's name
	 * @param Email
	 *            type
	 */
	public static void sendEmail(EmailModel emailModel) throws EmailException {

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
				return new PasswordAuthentication("hello@baatna.com", "hello.baatna");
			}
		});

		session.setDebug(false);

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(emailModel.getFromAddress()));

			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailModel.getToAddress()));

			// Set Subject: header field
			message.setSubject(emailModel.getEmailSubject());

			message.setText(emailModel.getEmailContent());
			Transport.send(message);
		} catch (MessagingException mex) {
			mex.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
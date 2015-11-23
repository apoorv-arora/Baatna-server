package com.application.baatna.util;

import org.apache.commons.mail.EmailException;
import org.apache.log4j.Logger;

import com.application.baatna.types.EmailType;

public class EmailSender implements Runnable {

	private static final Logger logger = Logger.getLogger(EmailSender.class);

	private String email_id, name;
	private EmailType emailType;

	public EmailSender(String email, String name, EmailType emailType) {
		this.email_id = email;
		this.name = name;
		this.emailType = emailType;
	}

	@Override
	public void run() {
		try {
			String logger_str = String.format("Sending %s email to %s",
					emailType.toString(), email_id);
			logger.info(logger_str);
			EmailUtil.sendEmail(email_id, emailType);
		} catch (EmailException e) {
			logger.error("Exception occurred while sending email", e);
		}
	}
}
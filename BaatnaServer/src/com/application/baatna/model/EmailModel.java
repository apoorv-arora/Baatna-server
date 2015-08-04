package com.application.baatna.model;

public class EmailModel {

	private String emailSubject;
	private String emailContent;

	public String getEmailSubject() {
		return emailSubject;
	}

	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	public String getEmailContent() {
		return emailContent;
	}

	public void setEmailContent(String emailContent) {
		this.emailContent = emailContent;
	}

	@Override
	public String toString() {
		return "EmailModel [emailSubject=" + emailSubject + ", emailContent="
				+ emailContent + "]";
	}
}
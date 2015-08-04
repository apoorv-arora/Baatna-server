package com.application.baatna.bean;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Token {

	private int accessToken, refreshToken;

	public Token() {
	}

	public Token(int accessToken, int refreshToken) {

		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}

	public int getAccessToken() {

		return accessToken;
	}

	public void setAccessToken(int accessToken) {
		this.accessToken = accessToken;
	}

	public int getRefreshToken() {

		return refreshToken;
	}

	public void setRefreshToken(int refreshToken) {
		this.refreshToken = refreshToken;
	}

}

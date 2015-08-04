package com.application.baatna.bean;

import java.util.LinkedList;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AllMessages {

	private LinkedList messages1, messages2;
	private int num1, num2;

	public AllMessages() {
	}

	public LinkedList getMessages1() {
		return messages1;
	}

	public void setMessages1(LinkedList messages1) {
		this.messages1 = messages1;
	}

	public LinkedList getMessages2() {
		return messages2;
	}

	public void setMessages2(LinkedList messages2) {
		this.messages2 = messages2;
	}

	public int getNum1() {
		return num1;
	}

	public void setNum1(int num1) {
		this.num1 = num1;
	}

	public int getNum2() {
		return num2;
	}

	public void setNum2(int num2) {
		this.num2 = num2;
	}

}
package com.application.baatna.bean;

import java.io.Serializable;

public class ReturnTable implements Serializable{
	
	private int return_id;
	private int user_id_one;
	private int user_id_two;
	private int wishid;
	private long return_date;
	private long return_deadline;
	private String return_pickup;
	private String return_delivery;
	private int return_type;
	/* 1 for self return
	 * 2 for baatna return
	 */
	public ReturnTable(){
		
	}
	public int getReturn_id() {
		return return_id;
	}
	public void setReturn_id(int return_id) {
		this.return_id = return_id;
	}
	
	public int getUser_id_one() {
		return user_id_one;
	}
	public void setUser_id_one(int user_id_one) {
		this.user_id_one = user_id_one;
	}
	public int getUser_id_two() {
		return user_id_two;
	}
	public void setUser_id_two(int user_id_two) {
		this.user_id_two = user_id_two;
	}
	
	public int getWishid() {
		return wishid;
	}
	public void setWishid(int wishid) {
		this.wishid = wishid;
	}
	
	public long getReturn_date() {
		return return_date;
	}
	public void setReturn_date(long return_date) {
		this.return_date = return_date;
	}
	public long getReturn_deadline() {
		return return_deadline;
	}
	public void setReturn_deadline(long return_deadline) {
		this.return_deadline = return_deadline;
	}
	public String getReturn_pickup() {
		return return_pickup;
	}
	public void setReturn_pickup(String return_pickup) {
		this.return_pickup = return_pickup;
	}
	public String getReturn_delivery() {
		return return_delivery;
	}
	public void setReturn_delivery(String return_delivery) {
		this.return_delivery = return_delivery;
	}
	public int getReturn_type() {
		return return_type;
	}
	public void setReturn_type(int return_type) {
		this.return_type = return_type;
	}
	
	
	
}
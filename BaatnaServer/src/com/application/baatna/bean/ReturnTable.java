package com.application.baatna.bean;

import java.io.Serializable;

public class ReturnTable implements Serializable{
	
	private int return_id;
	private int borrow_id;
	private int lender_id;
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
	
	
	
	public int getBorrow_id() {
		return borrow_id;
	}
	public void setBorrow_id(int borrow_id) {
		this.borrow_id = borrow_id;
	}
	public int getLender_id() {
		return lender_id;
	}
	public void setLender_id(int lender_id) {
		this.lender_id = lender_id;
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
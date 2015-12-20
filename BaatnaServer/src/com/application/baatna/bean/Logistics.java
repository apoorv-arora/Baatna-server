package com.application.baatna.bean;

import java.io.Serializable;

public class Logistics implements Serializable {
	private int Logistic_id;
	private int borrower_id; //user_id of borrower
	private int lender_id;//user_id of lender
	private int type;
	// self delivered=1 and baatnadelivery=2
	private int status;
	/*
	 * 1 for requested
	 * 2 for pickup from lender
	 * 3 for delivered to borrower
	 * 4 for return pickup
	 * 5 for returned
	 */
	private long pick_up_time;
	private long delivery_time;
	private int amount;
	private int wishid;
	private long return_deadline;
	private int user_verification;
	
	public int getWishid() {
		return wishid;
	}
	public void setWishid(int wishid) {
		this.wishid = wishid;
	}
	public Logistics(){	

	}
	/*public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}*/
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}


	public Long getPick_up_time() {
		return pick_up_time;
	}
	public void setPick_up_time(long pick_up_time) {
		this.pick_up_time = pick_up_time;
	}
	public Long getDelivery_time() {
		return delivery_time;
	}
	public void setDelivery_time(long delivery_time) {
		this.delivery_time = delivery_time;
	}
	public int getLogistic_id() {
		return Logistic_id;
	}
	public void setLogistic_id(int logistic_id) {
		this.Logistic_id = logistic_id;
	}

	public int getBorrower_id() {
		return borrower_id;
	}
	public void setBorrower_id(int borrower_id) {
		this.borrower_id = borrower_id;
	}
	public int getLender_id() {
		return lender_id;
	}
	public void setLender_id(int lender_id) {
		this.lender_id = lender_id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public long getReturn_deadline() {
		return return_deadline;
	}
	public void setReturn_deadline(long return_deadline) {
		this.return_deadline = return_deadline;
	}
	public int getUser_verification() {
		return user_verification;
	}
	public void setUser_verification(int user_verification) {
		this.user_verification = user_verification;
	}

}
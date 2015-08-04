package com.application.baatna.bean;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class User implements Serializable {
	private int userId;
	private String profilePic;
	private String userName;
	private String passWord;
	private String email;
	private String address;
	private String phone;
	private String bio;
	private int isVerified;
	private String facebookId;
	private String facebookData;
	private String facebookToken;
	private String fbPermission;
	private int isInstitutionVerified;
	private String institutionName;
	private String studentId;
	
	private Set<Wish> acceptedWishes = new HashSet<Wish>();
	private Set<Wish> declinedWishes = new HashSet<Wish>();

	public User() {
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getProfilePic() {
		return profilePic;
	}

	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public int getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(int isVerified) {
		this.isVerified = isVerified;
	}

	public String getFacebookId() {
		return facebookId;
	}

	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}

	public String getFacebookData() {
		return facebookData;
	}

	public void setFacebookData(String facebookData) {
		this.facebookData = facebookData;
	}

	public String getFacebookToken() {
		return facebookToken;
	}

	public void setFacebookToken(String facebookToken) {
		this.facebookToken = facebookToken;
	}

	public String getFbPermission() {
		return fbPermission;
	}

	public void setFbPermission(String fbPermission) {
		this.fbPermission = fbPermission;
	}

	public int getIsInstitutionVerified() {
		return isInstitutionVerified;
	}

	public void setIsInstitutionVerified(int isInstitutionVerified) {
		this.isInstitutionVerified = isInstitutionVerified;
	}

	public String getInstitutionName() {
		return institutionName;
	}

	public void setInstitutionName(String institutionName) {
		this.institutionName = institutionName;
	}

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public Set<Wish> getAcceptedWishes() {
		return acceptedWishes;
	}

	public void setAcceptedWishes(Set<Wish> acceptedWishes) {
		this.acceptedWishes = acceptedWishes;
	}

	public Set<Wish> getDeclinedWishes() {
		return declinedWishes;
	}

	public void setDeclinedWishes(Set<Wish> declinedWishes) {
		this.declinedWishes = declinedWishes;
	}
	
}

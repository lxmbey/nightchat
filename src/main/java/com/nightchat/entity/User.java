package com.nightchat.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User {
	@Id
	@Column(length = 64)
	private String id;
	@Column(unique = true)
	private String phoneNum;
	private String nickname;
	private String sex;
	private Date birthday;
	private String password;

	public User() {

	}

	public User(String id, String phoneNum, String nickname, String sex, Date birthday, String password) {
		super();
		this.id = id;
		this.phoneNum = phoneNum;
		this.nickname = nickname;
		this.sex = sex;
		this.birthday = birthday;
		this.password = password;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}

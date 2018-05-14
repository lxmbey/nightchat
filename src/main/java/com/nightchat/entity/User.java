package com.nightchat.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "user")
public class User extends BaseEntity {
	@Column(unique = true)
	private String phoneNum;
	@Column(length = 50)
	private String nickname;
	@Column(length = 4)
	private String sex;
	private Date birthday;
	private String password;
	private String headImgUrl;

	@Column(updatable = false, length = 100)
	private String registIp;
	// 设备ID
	@Column(updatable = false, length = 100)
	private String deviceId;
	// 经度
	private double longitude;
	// 纬度
	private double latitude;

	public User() {

	}

	public User(String id, String phoneNum, String nickname, String sex, Date birthday, String password, String headImgUrl) {
		this.id = id;
		this.phoneNum = phoneNum;
		this.nickname = nickname;
		this.sex = sex;
		this.birthday = birthday;
		this.password = password;
		this.headImgUrl = headImgUrl;
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

	public String getHeadImgUrl() {
		return headImgUrl;
	}

	public void setHeadImgUrl(String headImgUrl) {
		this.headImgUrl = headImgUrl;
	}

	public String getRegistIp() {
		return registIp;
	}

	public void setRegistIp(String registIp) {
		this.registIp = registIp;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

}

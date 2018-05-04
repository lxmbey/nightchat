package com.nightchat.view;

import io.swagger.annotations.ApiModelProperty;

public class RegistReq {
	public String phoneNum;
	public String nickname;
	@ApiModelProperty("‘男’or‘女’")
	public String sex;
	@ApiModelProperty("生日：yyyy-MM-dd")
	public String birthday;
	public String password;
	@ApiModelProperty("短信验证码")
	public String smsCode;

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

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSmsCode() {
		return smsCode;
	}

	public void setSmsCode(String smsCode) {
		this.smsCode = smsCode;
	}

}

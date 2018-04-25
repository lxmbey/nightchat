package com.nightchat.view;

public class UserInfoResp extends BaseResp {
	public String id;
	public String phoneNum;
	public String nickname;
	public String sex;
	public String birthday;

	public UserInfoResp() {

	}

	public UserInfoResp(String id, String phoneNum, String nickname, String sex, String birthday) {
		super();
		this.id = id;
		this.phoneNum = phoneNum;
		this.nickname = nickname;
		this.sex = sex;
		this.birthday = birthday;
	}
}

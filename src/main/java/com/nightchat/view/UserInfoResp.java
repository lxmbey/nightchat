package com.nightchat.view;

public class UserInfoResp extends BaseResp {
	public String id;
	public String phoneNum;
	public String nickname;
	public String sex;
	public String birthday;
	public String headImgUrl;

	public UserInfoResp() {

	}

	public UserInfoResp(String id, String phoneNum, String nickname, String sex, String birthday, String headImgUrl) {
		super();
		this.id = id;
		this.phoneNum = phoneNum;
		this.nickname = nickname;
		this.sex = sex;
		this.birthday = birthday;
		this.headImgUrl = headImgUrl;
	}
}

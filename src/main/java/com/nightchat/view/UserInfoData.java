package com.nightchat.view;

import com.nightchat.entity.User;
import com.nightchat.utils.DateUtils;

public class UserInfoData {
	public String id;
	public String phoneNum;
	public String nickname;
	public String sex;
	public String birthday;
	public String headImgUrl;

	public UserInfoData() {

	}

	public UserInfoData(String id, String phoneNum, String nickname, String sex, String birthday, String headImgUrl) {
		super();
		this.id = id;
		this.phoneNum = phoneNum;
		this.nickname = nickname;
		this.sex = sex;
		this.birthday = birthday;
		this.headImgUrl = headImgUrl;
	}

	public static UserInfoData fromUser(User user) {
		return new UserInfoData(user.getId(), user.getPhoneNum(), user.getNickname(), user.getSex(), DateUtils.formatDate(DateUtils.YearMonthDay, user.getBirthday()),
				user.getHeadImgUrl());
	}
}

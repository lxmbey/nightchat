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
}

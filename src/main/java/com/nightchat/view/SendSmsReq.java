package com.nightchat.view;

import io.swagger.annotations.ApiModelProperty;

public class SendSmsReq {
	public String phoneNum;
	@ApiModelProperty("图形验证码")
	public String imgCode;
}

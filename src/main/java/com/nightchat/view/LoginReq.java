package com.nightchat.view;

import io.swagger.annotations.ApiModelProperty;

public class LoginReq {
	public String phoneNum;
	public String password;

	@ApiModelProperty("经度")
	public String longitude;
	@ApiModelProperty("纬度")
	public String latitude;
}

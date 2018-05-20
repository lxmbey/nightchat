package com.nightchat.view;

import io.swagger.annotations.ApiModelProperty;

public class LoginRespData {
	@ApiModelProperty("登录后的会话KEY，需要用session_key作为key加入请求头里面")
	public String sessionKey;
	
	@ApiModelProperty("用户基本信息")
	public UserInfoData userData;
}

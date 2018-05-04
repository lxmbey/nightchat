package com.nightchat.view;

import io.swagger.annotations.ApiModelProperty;

public class LoginResp extends BaseResp {
	@ApiModelProperty("登录后的会话KEY，需要用session_key作为key加入请求头里面")
	public String sessionKey;
}

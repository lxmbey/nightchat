package com.nightchat.view;

import io.swagger.annotations.ApiModelProperty;

public class UploadTokenResp extends BaseResp {
	@ApiModelProperty("")
	public String accessKeyId;
	public String accessKeySecret;
	public String securityToken;
	public String expiration;
}

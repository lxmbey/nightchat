package com.nightchat.view;

import io.swagger.annotations.ApiModelProperty;

public class UploadTokenData {
	public String accessKeyId;
	public String accessKeySecret;
	public String securityToken;
	@ApiModelProperty("token到期时间戳")
	public long expiration;

	@ApiModelProperty("头像上传路径")
	public String headPath;
	@ApiModelProperty("聊天图片上传路径")
	public String imgPath;
	@ApiModelProperty("聊天语音上传路径")
	public String voicePath;

	public UploadTokenData(String accessKeyId, String accessKeySecret, String securityToken, long expiration) {
		super();
		this.accessKeyId = accessKeyId;
		this.accessKeySecret = accessKeySecret;
		this.securityToken = securityToken;
		this.expiration = expiration;
	}

}

package com.nightchat.view;

public class UploadTokenData {
	public String accessKeyId;
	public String accessKeySecret;
	public String securityToken;
	public String expiration;

	public UploadTokenData(String accessKeyId, String accessKeySecret, String securityToken, String expiration) {
		super();
		this.accessKeyId = accessKeyId;
		this.accessKeySecret = accessKeySecret;
		this.securityToken = securityToken;
		this.expiration = expiration;
	}

}

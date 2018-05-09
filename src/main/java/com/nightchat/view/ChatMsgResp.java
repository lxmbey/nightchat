package com.nightchat.view;

public class ChatMsgResp {
	public int msgType;
	public String content;
	public UserInfoData userInfo;
	public String sendTime;

	public ChatMsgResp() {

	}

	public ChatMsgResp(int msgType, String content, String sendTime) {
		super();
		this.msgType = msgType;
		this.content = content;
		this.sendTime = sendTime;
	}

}

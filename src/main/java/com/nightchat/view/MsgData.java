package com.nightchat.view;

import io.swagger.annotations.ApiModelProperty;

public class MsgData {
	@ApiModelProperty("0-文本1-语音2-图片")
	public int msgType;
	public String content;
	public String sendTime;

	public MsgData() {

	}

	public MsgData(int msgType, String content, String sendTime) {
		super();
		this.msgType = msgType;
		this.content = content;
		this.sendTime = sendTime;
	}

}

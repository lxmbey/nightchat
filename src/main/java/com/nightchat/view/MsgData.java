package com.nightchat.view;

import io.swagger.annotations.ApiModelProperty;

public class MsgData {
	public String id;
	@ApiModelProperty("0-文本1-语音2-图片")
	public int msgType;
	public String content;
	public long sendTime;

	public MsgData() {

	}

	public MsgData(String id, int msgType, String content, long sendTime) {
		super();
		this.id = id;
		this.msgType = msgType;
		this.content = content;
		this.sendTime = sendTime;
	}

}

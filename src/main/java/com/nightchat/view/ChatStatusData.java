package com.nightchat.view;

import io.swagger.annotations.ApiModelProperty;

public class ChatStatusData {
	public boolean isOpen;
	@ApiModelProperty("可匹配次数,-1表示无限")
	public int matchNum;
	@ApiModelProperty("开启倒计时秒")
	public long countdown;
}

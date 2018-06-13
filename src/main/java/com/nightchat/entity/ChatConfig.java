package com.nightchat.entity;

import javax.persistence.Entity;

@Entity(name = "chat_config")
public class ChatConfig extends BaseEntity {
	// 每天可匹配次数,-1无限制
	private int matchNum;
	private int beginHour;
	private int endHour;

	public ChatConfig() {

	}

	public ChatConfig(int matchNum, int beginHour, int endHour) {
		super();
		this.matchNum = matchNum;
		this.beginHour = beginHour;
		this.endHour = endHour;
	}

	public int getMatchNum() {
		return matchNum;
	}

	public void setMatchNum(int matchNum) {
		this.matchNum = matchNum;
	}

	public int getBeginHour() {
		return beginHour;
	}

	public void setBeginHour(int beginHour) {
		this.beginHour = beginHour;
	}

	public int getEndHour() {
		return endHour;
	}

	public void setEndHour(int endHour) {
		this.endHour = endHour;
	}

}

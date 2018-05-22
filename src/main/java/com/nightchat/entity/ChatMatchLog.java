package com.nightchat.entity;

import java.util.Date;

import javax.persistence.Entity;

/**
 * 聊天匹配记录
 */
@Entity(name = "chat_match_log")
public class ChatMatchLog extends BaseEntity {
	private Date matchDate;
	private String userId;
	private String otherUserId;

	public ChatMatchLog() {

	}

	public ChatMatchLog(String id, Date matchDate, String userId, String otherUserId) {
		super();
		this.id = id;
		this.matchDate = matchDate;
		this.userId = userId;
		this.otherUserId = otherUserId;
	}

	public Date getMatchDate() {
		return matchDate;
	}

	public void setMatchDate(Date matchDate) {
		this.matchDate = matchDate;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getOtherUserId() {
		return otherUserId;
	}

	public void setOtherUserId(String otherUserId) {
		this.otherUserId = otherUserId;
	}

}

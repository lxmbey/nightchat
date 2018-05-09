package com.nightchat.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "user_msg")
public class UserMsg extends BaseEntity {
	private String userId;
	private String sendUserId;
	private int status;
	private Date sendDate;
	private int msgType;
	@Column(length = 2000)
	private String msgContent;

	public UserMsg() {

	}

	public UserMsg(String id, String userId, String sendUserId, int status, Date sendDate, int msgType, String msgContent) {
		super();
		this.id = id;
		this.userId = userId;
		this.sendUserId = sendUserId;
		this.status = status;
		this.sendDate = sendDate;
		this.msgType = msgType;
		this.msgContent = msgContent;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSendUserId() {
		return sendUserId;
	}

	public void setSendUserId(String sendUserId) {
		this.sendUserId = sendUserId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getSendDate() {
		return sendDate;
	}

	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	public String getMsgContent() {
		return msgContent;
	}

	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}

}

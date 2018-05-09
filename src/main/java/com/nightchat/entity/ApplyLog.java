package com.nightchat.entity;

import java.util.Date;

import javax.persistence.Entity;

@Entity(name = "apply_log")
public class ApplyLog extends BaseEntity {
	private String applyUserId;
	private String friendUserId;
	private Date applyDate;

	public ApplyLog() {

	}

	public ApplyLog(String id, String applyUserId, String friendUserId, Date applyDate) {
		super();
		this.id = id;
		this.applyUserId = applyUserId;
		this.friendUserId = friendUserId;
		this.applyDate = applyDate;
	}

	public String getApplyUserId() {
		return applyUserId;
	}

	public void setApplyUserId(String applyUserId) {
		this.applyUserId = applyUserId;
	}

	public String getFriendUserId() {
		return friendUserId;
	}

	public void setFriendUserId(String friendUserId) {
		this.friendUserId = friendUserId;
	}

	public Date getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}

}

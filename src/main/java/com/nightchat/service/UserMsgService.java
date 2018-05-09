package com.nightchat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nightchat.dao.BaseDao;
import com.nightchat.entity.UserMsg;

@Service
@Transactional(rollbackFor = { Exception.class })
public class UserMsgService {
	@Autowired
	private BaseDao baseDao;

	public void add(UserMsg userMsg) {
		baseDao.getSession().save(userMsg);
	}
}

package com.nightchat.service;

import java.util.List;

import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nightchat.dao.BaseDao;
import com.nightchat.entity.ChatMatchLog;

@Service
@Transactional(rollbackFor = { Exception.class })
public class ChatMatchLogService {
	@Autowired
	private BaseDao baseDao;

	public void add(ChatMatchLog chatMatchLog) {
		baseDao.getSession().save(chatMatchLog);
	}

	public List<ChatMatchLog> findLog(String userId) {
		String hql = "from chat_match_log where userId=:userId";
		Query<ChatMatchLog> query = baseDao.getSession().createQuery(hql, ChatMatchLog.class);
		query.setParameter("userId", userId);
		List<ChatMatchLog> list = query.list();
		return list;
	}
}

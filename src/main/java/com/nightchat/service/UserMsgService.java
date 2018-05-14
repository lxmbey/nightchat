package com.nightchat.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.query.Query;
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

	/**
	 * 获取未读消息，key-发送者ID
	 * 
	 * @param userId
	 * @return
	 */
	public Map<String, List<UserMsg>> getUnreadMsg(String userId) {
		Map<String, List<UserMsg>> msgMap = new HashMap<>();

		String hql = "from user_msg where userId =:userId and status = 0 order by sendDate";
		Query<UserMsg> query = baseDao.getSession().createQuery(hql, UserMsg.class);
		query.setParameter("userId", userId);
		List<UserMsg> list = query.list();
		for (UserMsg m : list) {
			List<UserMsg> ulist = msgMap.get(m.getSendUserId());
			if (ulist == null) {
				ulist = new ArrayList<>();
				msgMap.put(m.getSendUserId(), ulist);
			}
			ulist.add(m);
		}
		return msgMap;
	}
}

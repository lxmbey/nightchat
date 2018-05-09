package com.nightchat.service;

import java.util.List;

import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nightchat.dao.BaseDao;
import com.nightchat.entity.ApplyLog;

@Service
@Transactional(rollbackFor = { Exception.class })
public class ApplyLogService {
	@Autowired
	private BaseDao baseDao;

	public void add(ApplyLog applyLog) {
		baseDao.getSession().save(applyLog);
	}

	public ApplyLog findLog(String applyId, String friendId) {
		String hql = "from ApplyLog where applyUserId=:userId and friendUserId=:friendId";
		Query<ApplyLog> query = baseDao.getSession().createQuery(hql, ApplyLog.class);
		query.setParameter("userId", applyId);
		query.setParameter("friendId", friendId);
		List<ApplyLog> list = query.list();
		return list.isEmpty() ? null : list.get(0);
	}
}

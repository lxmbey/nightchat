package com.nightchat.service;

import java.util.List;

import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nightchat.dao.BaseDao;
import com.nightchat.entity.User;

@Service
@Transactional(rollbackFor = { Exception.class })
public class UserService {
	@Autowired
	private BaseDao baseDao;

	public User getById(String id) {
		return baseDao.get(id, User.class);
	}

	public User getByPhone(String phoneNum) {
		String sql = "from User where phoneNum = :phoneNum";
		Query<User> query = baseDao.getSession().createQuery(sql, User.class);
		query.setParameter("phoneNum", phoneNum);
		return query.uniqueResult();
	}

	public void add(User user) {
		baseDao.getSession().save(user);
	}

	public void update(User user) {
		baseDao.getSession().update(user);
	}

	public List<User> searchUser(int size, String selfId) {
		Query<User> query = baseDao.getSession().createQuery("from User where id !=:selfId", User.class).setMaxResults(size);
		query.setParameter("selfId", selfId);
		return query.list();
	}
}

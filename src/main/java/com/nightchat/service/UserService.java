package com.nightchat.service;

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

	public User get(int id) {
		return baseDao.get(id, User.class);
	}
}

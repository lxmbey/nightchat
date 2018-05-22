package com.nightchat.service;

import java.util.Date;
import java.util.List;

import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nightchat.dao.BaseDao;
import com.nightchat.entity.ApplyLog;
import com.nightchat.entity.User;
import com.nightchat.entity.UserFriend;
import com.nightchat.utils.StringUtils;

@Service
@Transactional(rollbackFor = { Exception.class })
public class UserFriendService {
	@Autowired
	private BaseDao baseDao;

	public void add(UserFriend userFriend) {
		baseDao.getSession().save(userFriend);
	}

	public boolean isExist(String userId, String friendId) {
		String hql = "from user_friend where userId =:userId and friend.id = :friendId";
		Query<UserFriend> query = baseDao.getSession().createQuery(hql, UserFriend.class);
		query.setParameter("userId", userId);
		query.setParameter("friendId", friendId);
		return !query.list().isEmpty();
	}

	public void addFriend(User applyUser, User friendUser, ApplyLog applyLog) {
		UserFriend friend1 = new UserFriend();
		friend1.setId(StringUtils.randomUUID());
		friend1.setCreateDate(new Date());
		friend1.setUserId(applyUser.getId());
		friend1.setFriend(friendUser);
		add(friend1);

		UserFriend friend2 = new UserFriend();
		friend2.setId(StringUtils.randomUUID());
		friend2.setCreateDate(new Date());
		friend2.setUserId(friendUser.getId());
		friend2.setFriend(applyUser);
		add(friend2);

		baseDao.delete(applyLog);
	}

	public List<UserFriend> getFriends(String userId) {
		String hql = "from user_friend where userId =:userId";
		Query<UserFriend> query = baseDao.getSession().createQuery(hql, UserFriend.class);
		query.setParameter("userId", userId);
		List<UserFriend> list = query.list();

		return list;
	}

	public void deleteFriend(String userId, String friendId) {
		String hql = "delete from user_friend where (userId =:userId and friend.id = :friendId) or (userId =:friendId and friend.id = :userId)";
		Query<?> query = baseDao.getSession().createQuery(hql);
		query.setParameter("userId", userId);
		query.setParameter("friendId", friendId);
		query.executeUpdate();
	}
}

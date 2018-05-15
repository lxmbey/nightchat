package com.nightchat.dao;

import java.io.Serializable;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class BaseDao {
	@Autowired
	private SessionFactory sessionFactory;

	public Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	public <T> T get(String id, Class<T> cla) {
		return getSession().get(cla, id);
	}

	public void delete(Object obj) {
		getSession().delete(obj);
	}

	public Serializable add(Object obj) {
		return getSession().save(obj);
	}

	public void update(Object obj) {
		getSession().update(obj);
	}
}

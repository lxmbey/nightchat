package com.nightchat.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nightchat.dao.BaseDao;
import com.nightchat.entity.ChatConfig;

@Service
@Transactional
public class ChatConfigService {
	@Autowired
	private BaseDao baseDao;

	public ChatConfig getChatConfig() {
		ChatConfig config = baseDao.get("1", ChatConfig.class);
		if (config == null) {
			config = new ChatConfig("1", -1, 10, 2);
			baseDao.add(config);
		}
		return config;
	}

	public void initConfig() {
		getChatConfig();
	}
}

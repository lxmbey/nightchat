package com.nightchat.controller;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nightchat.common.Const;
import com.nightchat.common.Packet;
import com.nightchat.net.Request;

@Controller("chat")
public class ChatController {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	public String sendMsg(Request request) {

		return "success";
	}

	public String chatLogin(Request request) {
		JSONObject json = JSON.parseObject(request.packet.data);
		String sessionKey = json.getString("session_key");
		if (sessionKey != null) {
			String userId = redisTemplate.opsForValue().get(Const.REDIS_SESSION_KEY + sessionKey);
			if (userId != null) {
				
			}
		}
		return "success";
	}

	public void heart(Request request) {
		request.channel.writeAndFlush(new Packet("heart", ""));
	}
}

package com.nightchat.controller;

import org.springframework.stereotype.Controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nightchat.net.Request;

@Controller("chat")
public class ChatController {

	public String sendMsg(Request request) {

		return "success";
	}

	public String chatLogin(Request request) {
		JSONObject json = JSON.parseObject(request.packet.data);
		json.getString("session_key");
		return "success";
	}

	public void heart(Request request) {

	}
}

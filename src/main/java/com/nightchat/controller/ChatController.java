package com.nightchat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nightchat.common.Const;
import com.nightchat.common.NotLogin;
import com.nightchat.common.Packet;
import com.nightchat.common.Push;
import com.nightchat.entity.User;
import com.nightchat.net.Request;
import com.nightchat.service.UserService;
import com.nightchat.utils.DateUtils;
import com.nightchat.view.BaseResp;
import com.nightchat.view.ChatMsgResp;
import com.nightchat.view.ChatReq;
import com.nightchat.view.UserInfoResp;

import io.netty.channel.Channel;

@Controller("chat")
public class ChatController {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Autowired
	private UserService userService;

	// 发送聊天消息
	public String sendMsg(Request request) {
		ChatReq chatReq = JSON.parseObject(request.packet.data, ChatReq.class);
		ChatMsgResp chatMsgResp = new ChatMsgResp(chatReq.msgType, chatReq.content, DateUtils.currentDatetime());
		chatMsgResp.userInfo = Const.onlineChannel.get(request.channel);
		receiveMsg(Const.onlineUser.get(chatReq.receiverId), chatMsgResp);
		return JSON.toJSONString(BaseResp.SUCCESS);
	}

	// 聊天登录
	@NotLogin
	public String chatLogin(Request request) {
		JSONObject json = JSON.parseObject(request.packet.data);
		String sessionKey = json.getString("session_key");
		if (sessionKey != null) {
			String userId = redisTemplate.opsForValue().get(Const.REDIS_SESSION_KEY + sessionKey);
			if (userId != null) {
				User user = userService.getById(userId);
				Const.onlineChannel.put(request.channel,
						new UserInfoResp(user.getId(), user.getPhoneNum(), user.getNickname(), user.getSex(), DateUtils.formatDate(DateUtils.YearMonthDay, user.getBirthday())));
				Const.onlineUser.put(userId, request.channel);
				return JSON.toJSONString(BaseResp.SUCCESS);
			}
		}
		return JSON.toJSONString(BaseResp.fail("session_key不正确"));
	}

	// 心跳
	@NotLogin
	public void heart(Request request) {
		request.channel.writeAndFlush(new Packet("heart", ""));
	}

	@Push
	public void receiveMsg(Channel channel, ChatMsgResp msg) {
		channel.writeAndFlush(new Packet("receiveMsg", JSON.toJSONString(msg)));
	}

}

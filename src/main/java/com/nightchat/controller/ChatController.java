package com.nightchat.controller;

import java.text.MessageFormat;
import java.util.Date;

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
import com.nightchat.entity.UserMsg;
import com.nightchat.net.Request;
import com.nightchat.service.UserMsgService;
import com.nightchat.service.UserService;
import com.nightchat.utils.PushUtil;
import com.nightchat.utils.StringUtils;
import com.nightchat.view.BaseResp;
import com.nightchat.view.ChatMsgResp;
import com.nightchat.view.ChatReq;
import com.nightchat.view.MsgData;
import com.nightchat.view.UserInfoData;

import io.netty.channel.Channel;

@Controller("chat")
public class ChatController {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Autowired
	private UserService userService;

	@Autowired
	private UserMsgService userMsgService;

	// 发送聊天消息
	public String sendMsg(Request request) {
		ChatReq chatReq = JSON.parseObject(request.packet.data, ChatReq.class);
		ChatMsgResp chatMsgResp = new ChatMsgResp();
		chatMsgResp.userInfo = Const.onlineChannel.get(request.channel);
		Channel channel = Const.onlineUser.get(chatReq.receiverId);
		UserInfoData sendUser = Const.onlineChannel.get(request.channel);
		UserMsg userMsg = new UserMsg(StringUtils.randomUUID(), chatReq.receiverId, sendUser.id, 0, new Date(), chatReq.msgType, chatReq.content);
		MsgData msgData = new MsgData(userMsg.getId(), chatReq.msgType, chatReq.content, userMsg.getSendDate().getTime());
		chatMsgResp.msgData = msgData;
		if (channel == null) {// 不在线
			User receiverUser = userService.getById(chatReq.receiverId);
			if (receiverUser != null) {
				userMsgService.add(userMsg);
				String alert = userMsg.getMsgContent();
				if (userMsg.getMsgType() == Const.MsgType.IMG.value) {
					alert = MessageFormat.format("{0}发来了一张图片", sendUser.nickname);
				} else if (userMsg.getMsgType() == Const.MsgType.VOICE.value) {
					alert = MessageFormat.format("{0}发来了一条语音", sendUser.nickname);
				}
				PushUtil.sendPushWithCallback(receiverUser.getId(), alert);
			}
		} else {
			// userMsg.setStatus(1);// 已读
			// userMsgService.add(userMsg);
			receiveMsg(channel, chatMsgResp);
		}
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
				// 移除上次登录信息
				Channel old = Const.onlineUser.get(userId);
				if (old != null) {
					Const.onlineChannel.remove(old);
				}
				Const.onlineChannel.put(request.channel, UserInfoData.fromUser(user));
				Const.onlineUser.put(userId, request.channel);
				return JSON.toJSONString(BaseResp.SUCCESS);
			}
		}
		return JSON.toJSONString(BaseResp.fail("session_key不正确"));
	}

	// 心跳
	@NotLogin
	public String heart(Request request) {
		return "";
	}

	@Push
	public static void receiveMsg(Channel channel, ChatMsgResp msg) {
		channel.writeAndFlush(new Packet("receiveMsg", JSON.toJSONString(msg)));
	}

	@Push
	public static void applyFriend(Channel channel, UserInfoData infoData) {
		channel.writeAndFlush(new Packet("applyFriend", JSON.toJSONString(infoData)));
	}

	@Push
	public static void agreeApply(Channel channel, UserInfoData infoData) {
		channel.writeAndFlush(new Packet("agreeApply", JSON.toJSONString(infoData)));
	}

}

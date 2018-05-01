package com.nightchat.common;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;

public class Const {
	public static String SESSION_KEY = "session_key";
	/**
	 * redis里面的userKey前缀
	 */
	public static String REDIS_USER_KEY = "redis_user_key:";
	/**
	 * redis里面的sessionKey前缀
	 */
	public static String REDIS_SESSION_KEY = "redis_session_key:";
	/**
	 * redis里面的短信验证码key前缀
	 */
	public static String REDIS_SMS_KEY = "redis_sms_key:";

	/**
	 * redis里面的图形验证码key前缀
	 */
	public static String REDIS_IMG_KEY = "redis_img_key:";
	
	public static ConcurrentHashMap<Channel, String> onlineChannel = new ConcurrentHashMap<>();
}

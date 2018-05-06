package com.nightchat.common;

import java.util.concurrent.ConcurrentHashMap;

import com.nightchat.view.UserInfoResp;

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
	
	/**
	 * IP当天发送短信条数KEY
	 */
	public static String REDIS_IP_SMS_COUNT_KEY = "redis_ip_sms_count_key:";
	
	/**
	 * 手机当天发送短信条数
	 */
	public static String REDIS_PHONE_SMS_COUNT_KEY = "redis_phone_sms_count_key:";
	
	public static ConcurrentHashMap<Channel, UserInfoResp> onlineChannel = new ConcurrentHashMap<>();

	public static ConcurrentHashMap<String, Channel> onlineUser = new ConcurrentHashMap<>();
}

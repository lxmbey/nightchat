package com.nightchat.utils;

import java.util.Random;
import java.util.UUID;

public class StringUtils {
	public static Random random = new Random();

	public static boolean isEmpty(String str) {
		return str == null || str.length() <= 0;
	}

	/**
	 * 生成4位数验证码
	 * 
	 * @return
	 */
	public static String generateSmsCode() {
		String code = String.valueOf(random.nextInt(9999 - 1000) + 1000);
		return code;
	}

	public static String randomUUID() {
		String uuid = UUID.randomUUID().toString();
		return uuid.replaceAll("-", "");
	}
}

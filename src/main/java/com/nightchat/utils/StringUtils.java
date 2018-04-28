package com.nightchat.utils;

import java.security.MessageDigest;
import java.util.Random;
import java.util.UUID;

public class StringUtils {

	private static String randString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";//随机产生的字符串

	// private static String numberString = "0123456789";

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

	/**
	 * 随机生成验证码
	 * 
	 * @return
	 */
	public static String generateRandomStr(int length) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(randString.charAt(random.nextInt(length)));
		}
		return sb.toString();
	}

	public static String randomUUID() {
		String uuid = UUID.randomUUID().toString();
		return uuid.replaceAll("-", "");
	}

	/**
	 * 利用java原生的摘要实现SHA256加密
	 * 
	 * @param str 加密后的报文
	 * @return
	 */
	public static String getSHA256StrJava(String str) {
		MessageDigest messageDigest;
		String encodeStr = "";
		try {
			messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(str.getBytes("UTF-8"));
			encodeStr = byte2Hex(messageDigest.digest());
		} catch (Exception e) {

		}
		return encodeStr;
	}

	/**
	 * 将byte转为16进制
	 * 
	 * @param bytes
	 * @return
	 */
	private static String byte2Hex(byte[] bytes) {
		StringBuffer stringBuffer = new StringBuffer();
		String temp = null;
		for (int i = 0; i < bytes.length; i++) {
			temp = Integer.toHexString(bytes[i] & 0xFF);
			if (temp.length() == 1) {
				//1得到一位的进行补0操作
				stringBuffer.append("0");
			}
			stringBuffer.append(temp);
		}
		return stringBuffer.toString();
	}
}

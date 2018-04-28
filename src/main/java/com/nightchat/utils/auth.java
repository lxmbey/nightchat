package com.nightchat.utils;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.util.Base64;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import java.security.*;
import java.io.*;

public class auth {
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 is unsupported", e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MessageDigest不支持MD5Util", e);
        }
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    public static byte[] hashHmac(String data, String key)
            throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        mac.init(signingKey);
        return mac.doFinal(data.getBytes());
    }

    public static String sign(String key, String secret, String method, String uri, String date, String policy,
            String md5) throws Exception {
        String value = method + "&" + uri + "&" + date;
        if (policy != "") {
            value = value + "&" + policy;
        }

        if (md5 != "") {
            value = value + "&" + md5;
        }
        byte[] hmac = hashHmac(value, secret);
        String sign = Base64.getEncoder().encodeToString(hmac);
        return "UPYUN " + key + ":" + sign;
    }


    public static String getRfc1123Time() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
    }

    public static void main(String[] args) throws Exception {
        String key = "upyun";
        String secret = "secret";
        String method = "GET";
        String uri = "/v1/apps/";

        String date = getRfc1123Time();

        System.out.println(date);

        System.out.println(md5(secret));

        // 上传，处理，内容识别有存储
        System.out.println(sign(key, md5(secret), method, uri, date, "", ""));

        // 内容识别无存储，容器云
        System.out.println(sign(key, secret, method, uri, date, "", ""));
    }
}
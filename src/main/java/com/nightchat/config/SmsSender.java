package com.nightchat.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nightchat.utils.StringUtils;
import com.nightchat.utils.http.HttpClientManager;

@Component
public class SmsSender {
	private Logger logger = LogManager.getLogger(getClass());
	@Autowired
	private HttpClientManager httpClientManager;

	private String sdkappid;
	private String appkey;

	static String url = "https://yun.tim.qq.com/v5/tlssmssvr/sendsms?sdkappid={}&random={}";

	public boolean sendSmsByTpl(String phoneNum, String tpl, String sign, String[] params) {
		long time = System.currentTimeMillis() / 1000;
		String random = StringUtils.generateRandomStr(6);
		url = String.format(url, sdkappid, random);
		String sigStr = "appkey=" + appkey + "&random=" + random + "&time=" + time + "&mobile=" + phoneNum;
		String sig = StringUtils.getSHA256StrJava(sigStr);
		JSONObject jo = new JSONObject();
		jo.put("sign", sign);
		jo.put("sig", sig);
		JSONObject tel = new JSONObject();
		tel.put("mobile", phoneNum);
		tel.put("nationcode", "86");
		jo.put("tel", tel);
		jo.put("time", time);
		jo.put("tpl_id", tpl);
		jo.put("params", params);
		String content;
		try {
			content = httpClientManager.httpJsonPost(url, jo.toJSONString()).getContent();
			JSONObject res = JSON.parseObject(content);
			Integer result = res.getInteger("result");
			if (result != null && result == 0) {
				return true;
			}
			logger.warn("发送短信失败：" + res);
		} catch (Exception e) {
			logger.error("", e);
		}
		return false;
	}
}

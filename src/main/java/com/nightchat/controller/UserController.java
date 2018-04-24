package com.nightchat.controller;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nightchat.common.Const;
import com.nightchat.common.NotLogin;
import com.nightchat.entity.User;
import com.nightchat.service.UserService;
import com.nightchat.utils.StringUtils;
import com.nightchat.view.BaseResponse;
import com.nightchat.view.BaseResponse.StatusCode;
import com.nightchat.view.LoginResp;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController("user")
@RequestMapping("user")
@Api(tags = "user")
public class UserController {

	private Logger log = LogManager.getLogger(getClass());

	@Autowired
	private UserService userService;

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@NotLogin
	@ApiOperation(value = "注册接口", notes = "")
	@RequestMapping(value = "regist", method = RequestMethod.POST)
	public String regist() {
		return "success";
	}

	@NotLogin
	@ApiOperation(value = "登录接口", notes = "phoneNum手机号,password密码")
	@RequestMapping(value = "login", method = RequestMethod.POST)
	public LoginResp login(String phoneNum, String password) {
		LoginResp resp = new LoginResp();
		User user = userService.getByPhone(phoneNum);
		if (user != null && MD5Encoder.encode(password.getBytes()).equals(user.getPassword())) {
			resp.code = StatusCode.SUCCESS.value;
			String sessionKey = StringUtils.randomUUID();
			redisTemplate.opsForValue().set(Const.REDIS_SESSION_KEY + sessionKey, user.getId(), 30, TimeUnit.MINUTES);
			resp.sessionKey = sessionKey;
		} else {
			resp.code = StatusCode.FAIL.value;
			resp.msg = "用户名或密码错误";
		}
		return resp;
	}

	@NotLogin
	@ApiOperation(value = "发短信接口", notes = "phoneNum手机号")
	@RequestMapping(value = "sendSms", method = RequestMethod.POST)
	public BaseResponse sendSms(String phoneNum) {
		String code = StringUtils.generateSmsCode();
		code = "1234";
		redisTemplate.opsForValue().set(Const.REDIS_VERIFY_KEY + phoneNum, code, 10, TimeUnit.MINUTES);
		return BaseResponse.SUCCESS;
	}
}

package com.nightchat.controller;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.nightchat.common.Const;
import com.nightchat.common.NotLogin;
import com.nightchat.entity.User;
import com.nightchat.service.UserService;
import com.nightchat.utils.DateUtils;
import com.nightchat.utils.StringUtils;
import com.nightchat.view.BaseResp;
import com.nightchat.view.BaseResp.StatusCode;
import com.nightchat.view.LoginResp;
import com.nightchat.view.RegistReq;
import com.nightchat.view.UserInfoResp;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController("user")
@RequestMapping("user")
@Api(tags = "user")
public class UserController {

	// private Logger log = LogManager.getLogger(getClass());

	@Autowired
	private UserService userService;

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@NotLogin
	@ApiOperation(value = "注册接口", notes = "")
	@RequestMapping(value = "regist", method = RequestMethod.POST)
	public BaseResp regist(RegistReq registReq) {
		User old = userService.getByPhone(registReq.phoneNum);
		if (old != null) {
			return BaseResp.fail("手机号已存在");
		}
		String code = redisTemplate.opsForValue().get(Const.REDIS_VERIFY_KEY + registReq.phoneNum);
		if (code != null && code.equals(registReq.smsCode)) {
			redisTemplate.delete(Const.REDIS_VERIFY_KEY + registReq.phoneNum);
			User user = new User(StringUtils.randomUUID(), registReq.phoneNum, registReq.nickname, registReq.sex, DateUtils.parseDate(DateUtils.YearMonthDay, registReq.birthday),
					DigestUtils.md5DigestAsHex(registReq.password.getBytes()));
			userService.add(user);
			return BaseResp.SUCCESS;
		} else {
			return BaseResp.fail("短信验证码错误");
		}
	}

	@NotLogin
	@ApiOperation(value = "登录接口", notes = "phoneNum手机号,password密码")
	@RequestMapping(value = "login", method = RequestMethod.POST)
	public LoginResp login(String phoneNum, String password) {
		LoginResp resp = new LoginResp();
		User user = userService.getByPhone(phoneNum);
		if (user != null && password != null && DigestUtils.md5DigestAsHex(password.getBytes()).equals(user.getPassword())) {
			resp.code = StatusCode.SUCCESS.value;
			String sessionKey = StringUtils.randomUUID();

			//TODO 考虑加事务
			String oldSessionKey = redisTemplate.opsForValue().get(Const.REDIS_USER_KEY + user.getId());
			redisTemplate.delete(Const.REDIS_SESSION_KEY + oldSessionKey);
			redisTemplate.opsForValue().set(Const.REDIS_USER_KEY + user.getId(), sessionKey);
			redisTemplate.opsForValue().set(Const.REDIS_SESSION_KEY + sessionKey, user.getId(), 30, TimeUnit.MINUTES);

			resp.sessionKey = sessionKey;
		} else {
			resp.code = StatusCode.FAIL.value;
			resp.msg = "用户名或密码错误";
		}
		return resp;
	}

	@ApiOperation(value = "获取用户信息", notes = "")
	@RequestMapping(value = "getUserInfo", method = RequestMethod.POST)
	public UserInfoResp getUserInfo() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String sessionKey = request.getHeader(Const.SESSION_KEY);
		String userId = redisTemplate.opsForValue().get(Const.REDIS_SESSION_KEY + sessionKey);
		User user = userService.getById(userId);
		UserInfoResp infoResp = new UserInfoResp(user.getId(), user.getPhoneNum(), user.getNickname(), user.getSex(),
				DateUtils.formatDate(DateUtils.YearMonthDay, user.getBirthday()));
		return infoResp;
	}

	@NotLogin
	@ApiOperation(value = "发短信接口", notes = "phoneNum手机号")
	@RequestMapping(value = "sendSms", method = RequestMethod.POST)
	public BaseResp sendSms(String phoneNum) {
		String code = StringUtils.generateSmsCode();
		code = "1234";
		redisTemplate.opsForValue().set(Const.REDIS_VERIFY_KEY + phoneNum, code, 10, TimeUnit.MINUTES);
		return BaseResp.SUCCESS;
	}
}

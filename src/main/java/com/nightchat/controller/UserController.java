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

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.sts.model.v20150401.AssumeRoleRequest;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;
import com.nightchat.common.Const;
import com.nightchat.common.NotLogin;
import com.nightchat.config.AliyunConfig;
import com.nightchat.config.SmsSender;
import com.nightchat.entity.User;
import com.nightchat.service.UserService;
import com.nightchat.utils.DateUtils;
import com.nightchat.utils.PngUtil;
import com.nightchat.utils.StringUtils;
import com.nightchat.view.BaseResp;
import com.nightchat.view.BaseResp.StatusCode;
import com.nightchat.view.LoginResp;
import com.nightchat.view.RegistReq;
import com.nightchat.view.UploadTokenResp;
import com.nightchat.view.UserInfoResp;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("user")
@Api(tags = "user")
public class UserController {

	// private Logger log = LogManager.getLogger(getClass());

	@Autowired
	private UserService userService;

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Autowired
	private SmsSender smsSender;

	@Autowired
	private AliyunConfig aliyunConfig;

	@NotLogin
	@ApiOperation(value = "注册接口", notes = "")
	@RequestMapping(value = "regist", method = RequestMethod.POST)
	public BaseResp regist(RegistReq registReq) {
		User old = userService.getByPhone(registReq.phoneNum);
		if (old != null) {
			return BaseResp.fail("手机号已存在");
		}
		if (registReq.nickname == null || registReq.birthday == null || registReq.sex == null || registReq.password == null
				|| (!registReq.sex.equals("男") && !registReq.sex.equals("女"))) {
			return BaseResp.fail("输入参数错误");
		}
		String code = redisTemplate.opsForValue().get(Const.REDIS_SMS_KEY + registReq.phoneNum);
		if (code != null && code.equals(registReq.smsCode)) {
			redisTemplate.delete(Const.REDIS_SMS_KEY + registReq.phoneNum);
			User user = new User(StringUtils.randomUUID(), registReq.phoneNum, registReq.nickname, registReq.sex, DateUtils.parseDate(DateUtils.YearMonthDay, registReq.birthday),
					DigestUtils.md5DigestAsHex(registReq.password.getBytes()), "");
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

			// TODO 考虑加事务
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
				DateUtils.formatDate(DateUtils.YearMonthDay, user.getBirthday()), user.getHeadImgUrl());
		return infoResp;
	}

	@NotLogin
	@ApiOperation(value = "获取图形验证码", notes = "返回base64编码后的PNG图片内容")
	@RequestMapping(value = "getPngImg", method = RequestMethod.POST)
	public BaseResp getPngImg(String phoneNum) {
		if (StringUtils.isEmpty(phoneNum)) {
			return BaseResp.fail("手机号码不能为空");
		}
		String code = StringUtils.generateRandomStr(4);
		String imgStr = PngUtil.getRandCode(code);
		redisTemplate.opsForValue().set(Const.REDIS_IMG_KEY + phoneNum, code, 5, TimeUnit.MINUTES);

		BaseResp resp = BaseResp.SUCCESS;
		resp.data = imgStr;
		return resp;
	}

	@NotLogin
	@ApiOperation(value = "发短信接口", notes = "phoneNum手机号,imgCode图形验证码")
	@RequestMapping(value = "sendSms", method = RequestMethod.POST)
	public BaseResp sendSms(String phoneNum, String imgCode) {
		String redisCode = redisTemplate.opsForValue().get(Const.REDIS_IMG_KEY + phoneNum);
		if (redisCode == null || imgCode == null || !redisCode.equalsIgnoreCase(imgCode.toUpperCase())) {
			return BaseResp.fail("验证码错误");
		}
		String code = StringUtils.generateSmsCode();
		smsSender.sendSmsByTpl(phoneNum, "114901", "NightChat科技", new String[] { code, "10" });
		redisTemplate.opsForValue().set(Const.REDIS_SMS_KEY + phoneNum, code, 10, TimeUnit.MINUTES);
		return BaseResp.SUCCESS;
	}

	@ApiOperation(value = "修改基本资料", notes = "")
	@RequestMapping(value = "updateUserInfo", method = RequestMethod.POST)
	public BaseResp updateUserInfo(String nickname, String birthday, String headImg) {
		if (StringUtils.isEmpty(nickname) || StringUtils.isEmpty(birthday) || StringUtils.isEmpty(headImg)) {
			return BaseResp.fail("输入参数错误");
		}

		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String sessionKey = request.getHeader(Const.SESSION_KEY);
		String userId = redisTemplate.opsForValue().get(Const.REDIS_SESSION_KEY + sessionKey);
		User user = userService.getById(userId);
		user.setBirthday(DateUtils.parseDate(DateUtils.YearMonthDay, birthday));
		user.setNickname(nickname);
		user.setHeadImgUrl(headImg);
		userService.update(user);

		return BaseResp.SUCCESS;
	}

	@ApiOperation(value = "修改密码", notes = "")
	@RequestMapping(value = "updatePwd", method = RequestMethod.POST)
	public BaseResp updatePwd(String oldPwd, String newPwd) {
		if (StringUtils.isEmpty(oldPwd) || StringUtils.isEmpty(newPwd)) {
			return BaseResp.fail("输入参数错误");
		}

		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String sessionKey = request.getHeader(Const.SESSION_KEY);
		String userId = redisTemplate.opsForValue().get(Const.REDIS_SESSION_KEY + sessionKey);
		User user = userService.getById(userId);
		if (!user.getPassword().equals(DigestUtils.md5DigestAsHex(oldPwd.getBytes()))) {
			return BaseResp.fail("原密码错误");
		}
		user.setPassword(DigestUtils.md5DigestAsHex(newPwd.getBytes()));
		userService.update(user);

		return BaseResp.SUCCESS;
	}

	@NotLogin
	@ApiOperation(value = "找回密码", notes = "")
	@RequestMapping(value = "findPwd", method = RequestMethod.POST)
	public BaseResp findPwd(String phoneNum, String smsCode, String newPwd) {
		if (StringUtils.isEmpty(phoneNum) || StringUtils.isEmpty(smsCode) || StringUtils.isEmpty(newPwd)) {
			return BaseResp.fail("输入参数错误");
		}
		User user = userService.getByPhone(phoneNum);
		if (user == null) {
			return BaseResp.fail("用户不存在");
		}
		String code = redisTemplate.opsForValue().get(Const.REDIS_SMS_KEY + phoneNum);
		if (code != null && code.equals(smsCode)) {
			user.setPassword(DigestUtils.md5DigestAsHex(newPwd.getBytes()));
			userService.update(user);
			return BaseResp.SUCCESS;
		}

		return BaseResp.fail("短信验证码错误");
	}

	@ApiOperation(value = "获取图片上传Token")
	@RequestMapping(value = "getUploadToken", method = RequestMethod.POST)
	public UploadTokenResp getUploadToken() {
		UploadTokenResp resp = new UploadTokenResp();

		String accessKeyId = aliyunConfig.getAccessKeyID();
		String accessKeySecret = aliyunConfig.getAccessKeySecret();
		// RoleArn 需要在 RAM 控制台上获取
		String roleArn = aliyunConfig.getRoleArn();
		long durationSeconds = aliyunConfig.getTokenExpireTime();
		String policy = StringUtils.readFileContent(aliyunConfig.getPolicyFile());
		// RoleSessionName 是临时Token的会话名称，自己指定用于标识你的用户，主要用于审计，或者用于区分Token颁发给谁
		// 但是注意RoleSessionName的长度和规则，不要有空格，只能有'-' '_' 字母和数字等字符
		// 具体规则请参考API文档中的格式要求
		String roleSessionName = "alice-001";

		// 此处必须为 HTTPS
		ProtocolType protocolType = ProtocolType.HTTPS;

		try {
			final AssumeRoleResponse stsResponse = assumeRole(accessKeyId, accessKeySecret, roleArn, roleSessionName, policy, protocolType, durationSeconds);
			resp.accessKeyId = stsResponse.getCredentials().getAccessKeyId();
			resp.accessKeySecret = stsResponse.getCredentials().getAccessKeySecret();
			resp.securityToken = stsResponse.getCredentials().getSecurityToken();
			resp.expiration = stsResponse.getCredentials().getExpiration();
		} catch (ClientException e) {
			resp.code = StatusCode.FAIL.value;
			resp.msg = e.getErrCode() + ":" + e.getErrMsg();
		}
		return resp;
	}

	private AssumeRoleResponse assumeRole(String accessKeyId, String accessKeySecret, String roleArn, String roleSessionName, String policy, ProtocolType protocolType,
			long durationSeconds) throws ClientException {
		try {
			// 创建一个 Aliyun Acs Client, 用于发起 OpenAPI 请求
			IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
			DefaultAcsClient client = new DefaultAcsClient(profile);

			// 创建一个 AssumeRoleRequest 并设置请求参数
			final AssumeRoleRequest request = new AssumeRoleRequest();
			request.setVersion("2015-04-01");
			request.setMethod(MethodType.POST);
			request.setProtocol(protocolType);

			request.setRoleArn(roleArn);
			request.setRoleSessionName(roleSessionName);
			request.setPolicy(policy);
			request.setDurationSeconds(durationSeconds);

			// 发起请求，并得到response
			final AssumeRoleResponse response = client.getAcsResponse(request);

			return response;
		} catch (ClientException e) {
			throw e;
		}
	}

}

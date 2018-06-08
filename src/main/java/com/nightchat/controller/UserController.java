package com.nightchat.controller;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSON;
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
import com.nightchat.entity.ApplyLog;
import com.nightchat.entity.ChatMatchLog;
import com.nightchat.entity.User;
import com.nightchat.entity.UserFriend;
import com.nightchat.entity.UserMsg;
import com.nightchat.service.ApplyLogService;
import com.nightchat.service.ChatMatchLogService;
import com.nightchat.service.UserFriendService;
import com.nightchat.service.UserMsgService;
import com.nightchat.service.UserService;
import com.nightchat.utils.DateUtils;
import com.nightchat.utils.DistanceUtil;
import com.nightchat.utils.PngUtil;
import com.nightchat.utils.PushUtil;
import com.nightchat.utils.StringUtils;
import com.nightchat.view.AgreeApplyReq;
import com.nightchat.view.ApplyFriendReq;
import com.nightchat.view.BaseResp;
import com.nightchat.view.BaseResp.StatusCode;
import com.nightchat.view.ChatInfoBean;
import com.nightchat.view.ChatMatchReq;
import com.nightchat.view.CheckImgCodeReq;
import com.nightchat.view.FindPwdReq;
import com.nightchat.view.LocationReq;
import com.nightchat.view.LoginReq;
import com.nightchat.view.LoginRespData;
import com.nightchat.view.MsgData;
import com.nightchat.view.PngImgData;
import com.nightchat.view.RegistReq;
import com.nightchat.view.SendSmsReq;
import com.nightchat.view.UnreadMsgData;
import com.nightchat.view.UpdatePwdReq;
import com.nightchat.view.UpdateUserInfoReq;
import com.nightchat.view.UploadTokenData;
import com.nightchat.view.UserInfoData;

import io.netty.channel.Channel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("user")
@Api(tags = "user")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Autowired
	private SmsSender smsSender;

	@Autowired
	private AliyunConfig aliyunConfig;

	@Autowired
	private ApplyLogService applyLogService;

	@Autowired
	private UserFriendService userFriendService;

	@Autowired
	private UserMsgService userMsgService;

	@Value("${sms.daycount}")
	private int smsDayCount;

	// token有效期天数
	@Value("${chat.tokenDay}")
	private int tokenDay;

	@Autowired
	private ChatMatchLogService matchLogService;

	@NotLogin
	@ApiOperation(value = "注册接口", notes = "")
	@RequestMapping(value = "regist", method = RequestMethod.POST)
	public BaseResp<Void> regist(@RequestBody RegistReq registReq) {
		if (registReq.nickname == null || registReq.birthday == null || registReq.sex == null || registReq.password == null
				|| (!registReq.sex.equals("男") && !registReq.sex.equals("女"))) {
			return BaseResp.fail("输入参数错误");
		}
		User old = userService.getByPhone(registReq.phoneNum);
		if (old != null) {
			return BaseResp.fail("手机号已存在");
		}
		String code = redisTemplate.opsForValue().get(Const.REDIS_SMS_KEY + registReq.phoneNum);
		if (code != null && code.equals(registReq.smsCode)) {
			redisTemplate.delete(Const.REDIS_SMS_KEY + registReq.phoneNum);
			User user = new User(StringUtils.randomUUID(), registReq.phoneNum, registReq.nickname, registReq.sex, DateUtils.parseDate(DateUtils.YearMonthDay, registReq.birthday),
					DigestUtils.md5DigestAsHex(registReq.password.getBytes()), "");
			user.setRegistIp(StringUtils.getIpAddr());
			user.setDeviceId(getHeadParam("device_id"));
			String deviceType = getHeadParam("device_type");
			if (StringUtils.isNotEmpty(deviceType)) {
				user.setDeviceType(StringUtils.parseInt(deviceType));
			}
			user.setLongitude(registReq.longitude);
			user.setLatitude(registReq.latitude);
			userService.add(user);
			return BaseResp.SUCCESS;
		} else {
			return BaseResp.fail("短信验证码错误");
		}
	}

	@NotLogin
	@ApiOperation(value = "登录接口", notes = "phoneNum手机号,password密码")
	@RequestMapping(value = "login", method = RequestMethod.POST)
	public BaseResp<LoginRespData> login(@RequestBody LoginReq loginReq) {
		BaseResp<LoginRespData> resp = new BaseResp<>();
		User user = userService.getByPhone(loginReq.phoneNum);
		if (user != null && loginReq.password != null && DigestUtils.md5DigestAsHex(loginReq.password.getBytes()).equals(user.getPassword())) {
			String sessionKey = StringUtils.randomUUID();

			// TODO 考虑加事务
			String oldSessionKey = redisTemplate.opsForValue().get(Const.REDIS_USER_KEY + user.getId());
			redisTemplate.delete(Const.REDIS_SESSION_KEY + oldSessionKey);
			redisTemplate.opsForValue().set(Const.REDIS_USER_KEY + user.getId(), sessionKey, tokenDay, TimeUnit.DAYS);
			redisTemplate.opsForValue().set(Const.REDIS_SESSION_KEY + sessionKey, user.getId(), tokenDay, TimeUnit.DAYS);

			LoginRespData data = new LoginRespData();
			data.sessionKey = sessionKey;
			data.userData = UserInfoData.fromUser(user);
			resp.data = data;

			user.setLoginDate(new Date());
			userService.update(user);
		} else {
			resp.code = StatusCode.FAIL.value;
			resp.msg = "用户名或密码错误";
		}
		return resp;
	}

	@ApiOperation(value = "上传地理位置")
	@RequestMapping(value = "location", method = RequestMethod.POST)
	public BaseResp<Void> location(@RequestBody LocationReq req) {
		// TODO
		return BaseResp.SUCCESS;
	}

	@ApiOperation(value = "退出登录")
	@RequestMapping(value = "logout", method = RequestMethod.POST)
	public BaseResp<Void> logout() {
		String userId = getCurrentUserId();
		String sessionKey = redisTemplate.opsForValue().get(Const.REDIS_USER_KEY + userId);
		redisTemplate.delete(Const.REDIS_SESSION_KEY + sessionKey);
		redisTemplate.delete(Const.REDIS_USER_KEY + userId);
		Channel channel = Const.onlineUser.get(userId);
		if (channel != null) {
			channel.close();
		}
		return BaseResp.SUCCESS;
	}

	@ApiOperation(value = "获取用户信息", notes = "")
	@RequestMapping(value = "getUserInfo", method = RequestMethod.POST)
	public BaseResp<UserInfoData> getUserInfo() {
		String userId = getCurrentUserId();
		User user = userService.getById(userId);
		UserInfoData data = UserInfoData.fromUser(user);
		return BaseResp.success(data);
	}

	@ApiOperation(value = "获取好友列表")
	@RequestMapping(value = "getFriends", method = RequestMethod.POST)
	public BaseResp<List<UserInfoData>> getFriends() {
		List<UserInfoData> datas = new ArrayList<>();
		String userId = getCurrentUserId();
		List<UserFriend> list = userFriendService.getFriends(userId);
		for (UserFriend u : list) {
			datas.add(UserInfoData.fromUser(u.getFriend()));
		}
		return BaseResp.success(datas);
	}

	@ApiOperation(value = "获取未读消息")
	@RequestMapping(value = "getUnreadMsg", method = RequestMethod.POST)
	public BaseResp<List<UnreadMsgData>> getUnreadMsg() {
		List<UnreadMsgData> datas = new ArrayList<>();
		String userId = getCurrentUserId();
		Map<String, List<UserMsg>> map = userMsgService.getUnreadMsg(userId);
		for (Entry<String, List<UserMsg>> entry : map.entrySet()) {
			UnreadMsgData data = new UnreadMsgData();
			data.userInfo = UserInfoData.fromUser(userService.getById(entry.getKey()));
			for (UserMsg m : entry.getValue()) {
				data.msgData.add(new MsgData(m.getId(), m.getMsgType(), m.getMsgContent(), m.getSendDate().getTime()));
			}
			datas.add(data);
		}
		return BaseResp.success(datas);
	}

	@NotLogin
	@ApiOperation(value = "获取图形验证码", notes = "返回base64编码后的PNG图片内容")
	@RequestMapping(value = "getPngImg", method = RequestMethod.POST)
	public BaseResp<PngImgData> getPngImg() {
		BaseResp<PngImgData> resp = new BaseResp<>();
		String deviceId = getHeadParam("device_id");
		if (StringUtils.isEmpty(deviceId)) {
			resp.code = StatusCode.FAIL.value;
			resp.msg = "设备ID不能为空";
			return resp;
		}
		String code = StringUtils.generateRandomStr(4);
		String imgStr = PngUtil.getRandCode(code);
		redisTemplate.opsForValue().set(Const.REDIS_IMG_KEY + deviceId, code, 5, TimeUnit.MINUTES);

		PngImgData data = new PngImgData();
		data.pngStr = imgStr;
		resp.data = data;
		return resp;
	}

	@NotLogin
	@ApiOperation(value = "检查图形验证码是否正确")
	@RequestMapping(value = "checkImgCode", method = RequestMethod.POST)
	public BaseResp<Void> checkImgCode(@RequestBody CheckImgCodeReq req) {
		String deviceId = getHeadParam("device_id");
		if (StringUtils.isEmpty(deviceId)) {
			return BaseResp.fail("设备ID不能为空");
		}
		String code = redisTemplate.opsForValue().get(Const.REDIS_IMG_KEY + deviceId);
		if (StringUtils.isEmpty(req.imgCode) || code == null || !code.equals(req.imgCode)) {
			return BaseResp.fail("图形验证码错误");
		}
		return BaseResp.SUCCESS;
	}

	@NotLogin
	@ApiOperation(value = "发短信接口", notes = "phoneNum手机号,imgCode图形验证码")
	@RequestMapping(value = "sendSms", method = RequestMethod.POST)
	public BaseResp<Void> sendSms(@RequestBody SendSmsReq sendSmsReq) {
		String phoneNum = sendSmsReq.phoneNum;
		String imgCode = sendSmsReq.imgCode;
		String deviceId = getHeadParam("device_id");
		if (StringUtils.isEmpty(phoneNum) || StringUtils.isEmpty(imgCode) || StringUtils.isEmpty(deviceId)) {
			return BaseResp.fail("输入参数错误");
		}

		String redisCode = redisTemplate.opsForValue().get(Const.REDIS_IMG_KEY + deviceId);
		if (redisCode == null || imgCode == null || !redisCode.equalsIgnoreCase(imgCode.toUpperCase())) {
			return BaseResp.fail("图形验证码错误");
		}
		String oldCode = redisTemplate.opsForValue().get(Const.REDIS_SMS_KEY + phoneNum);
		if (oldCode != null) {
			return BaseResp.fail("已发送过短信验证码，有效期内可重复使用");
		}
		long delayTime = DateUtils.betweenTaskHourMillis(0, 0);// 发送记录清空倒计时
		String num = redisTemplate.opsForValue().get(Const.REDIS_PHONE_SMS_COUNT_KEY + phoneNum);
		int phoneSendNum = 0;
		int ipSendNum = 0;
		if (num != null) {
			phoneSendNum = StringUtils.parseInt(num);
			if (phoneSendNum >= smsDayCount) {
				return BaseResp.fail("手机号码发送短信条数达到上限");
			}
		}
		String ip = StringUtils.getIpAddr();
		num = redisTemplate.opsForValue().get(Const.REDIS_IP_SMS_COUNT_KEY + ip);
		if (num != null) {
			ipSendNum = StringUtils.parseInt(num);
			if (ipSendNum >= smsDayCount) {
				return BaseResp.fail("IP发送短信条数达到上限");
			}
		}
		String code = StringUtils.generateSmsCode();
		smsSender.sendSmsByTpl(phoneNum, "114901", "NightChat科技", new String[] { code, "5" });
		redisTemplate.opsForValue().set(Const.REDIS_SMS_KEY + phoneNum, code, 5, TimeUnit.MINUTES);

		redisTemplate.opsForValue().set(Const.REDIS_PHONE_SMS_COUNT_KEY + phoneNum, String.valueOf(phoneSendNum + 1), delayTime, TimeUnit.MILLISECONDS);
		redisTemplate.opsForValue().set(Const.REDIS_IP_SMS_COUNT_KEY + ip, String.valueOf(ipSendNum + 1), delayTime, TimeUnit.MILLISECONDS);
		return BaseResp.SUCCESS;
	}

	@ApiOperation(value = "修改基本资料", notes = "")
	@RequestMapping(value = "updateUserInfo", method = RequestMethod.POST)
	public BaseResp<Void> updateUserInfo(@RequestBody UpdateUserInfoReq req) {
		String nickname = req.nickname;
		String birthday = req.birthday;
		String headImg = req.headImg;

		String userId = getCurrentUserId();
		User user = userService.getById(userId);
		if (StringUtils.isNotEmpty(birthday)) {
			user.setBirthday(DateUtils.parseDate(DateUtils.YearMonthDay, birthday));
		}
		if (StringUtils.isNotEmpty(nickname)) {
			user.setNickname(nickname);
		}
		if (StringUtils.isNotEmpty(headImg)) {
			user.setHeadImgUrl(headImg);
		}
		userService.update(user);

		return BaseResp.SUCCESS;
	}

	@ApiOperation(value = "修改密码", notes = "")
	@RequestMapping(value = "updatePwd", method = RequestMethod.POST)
	public BaseResp<Void> updatePwd(@RequestBody UpdatePwdReq req) {
		String oldPwd = req.oldPwd;
		String newPwd = req.newPwd;
		if (StringUtils.isEmpty(oldPwd) || StringUtils.isEmpty(newPwd)) {
			return BaseResp.fail("输入参数错误");
		}

		String userId = getCurrentUserId();
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
	public BaseResp<Void> findPwd(@RequestBody FindPwdReq req) {
		String phoneNum = req.phoneNum;
		String smsCode = req.smsCode;
		String newPwd = req.newPwd;
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
	public BaseResp<UploadTokenData> getUploadToken() {
		BaseResp<UploadTokenData> resp = new BaseResp<>();

		String accessKeyId = aliyunConfig.getAccessKeyID();
		String accessKeySecret = aliyunConfig.getAccessKeySecret();
		// RoleArn 需要在 RAM 控制台上获取
		String roleArn = aliyunConfig.getRoleArn();
		long durationSeconds = aliyunConfig.getTokenExpireTime();
		String policy = StringUtils.readFileContent(aliyunConfig.getPolicyFile());
		policy = policy.replace("${aliyun.bucket}", aliyunConfig.getBucket());
		// RoleSessionName 是临时Token的会话名称，自己指定用于标识你的用户，主要用于审计，或者用于区分Token颁发给谁
		// 但是注意RoleSessionName的长度和规则，不要有空格，只能有'-' '_' 字母和数字等字符
		// 具体规则请参考API文档中的格式要求
		String sessionKey = getHeadParam(Const.SESSION_KEY);
		String roleSessionName = sessionKey;

		// 此处必须为 HTTPS
		ProtocolType protocolType = ProtocolType.HTTPS;

		try {
			final AssumeRoleResponse stsResponse = assumeRole(accessKeyId, accessKeySecret, roleArn, roleSessionName, policy, protocolType, durationSeconds);
			UploadTokenData data = new UploadTokenData(stsResponse.getCredentials().getAccessKeyId(), stsResponse.getCredentials().getAccessKeySecret(),
					stsResponse.getCredentials().getSecurityToken(), stsResponse.getCredentials().getExpiration());
			String userId = getCurrentUserId();
			data.headPath = "head/" + userId + ".png";
			data.imgPath = MessageFormat.format("chat/{0}/img/{1}/", DateUtils.currentDate(), userId);
			data.voicePath = MessageFormat.format("chat/{0}/voice/{1}/", DateUtils.currentDate(), userId);
			resp.data = data;
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

	@ApiOperation(value = "聊天匹配")
	@RequestMapping(value = "chatMatch", method = RequestMethod.POST)
	public BaseResp<UserInfoData> chatMatch(@RequestBody ChatMatchReq req) {
		BaseResp<UserInfoData> resp = new BaseResp<>();
		String userId = getCurrentUserId();
		User user = userService.getById(userId);

		ChatInfoBean chatInfoBean = null;
		String chatInfoStr = (String) redisTemplate.opsForHash().get(Const.CHAT_INFO_KEY, userId);
		if (chatInfoStr == null) {
			chatInfoBean = new ChatInfoBean();
			chatInfoBean.userInfo = UserInfoData.fromUser(user);
		} else {
			chatInfoBean = JSON.parseObject(chatInfoStr, ChatInfoBean.class);
		}
		chatInfoBean.locationReq = req;
		chatInfoBean.updateDate = new Date();
		redisTemplate.opsForHash().put(Const.CHAT_INFO_KEY, userId, JSON.toJSONString(chatInfoBean));

		List<UserFriend> friendList = userFriendService.getFriends(userId);
		List<ChatMatchLog> matchLogList = matchLogService.findLog(userId);

		UserInfoData matchUserInfo = null;
		List<ChatInfoBean> chatInfoList = new ArrayList<>();
		List<Object> str = redisTemplate.opsForHash().values(Const.CHAT_INFO_KEY);
		for (Object o : str) {
			ChatInfoBean t = JSON.parseObject(o.toString(), ChatInfoBean.class);
			if (!t.userInfo.sex.equals(req.sex)) {
				continue;
			}
			boolean exist = false;
			// 是好友
			for (UserFriend f : friendList) {
				if (f.getFriend().getId().equals(t.userInfo.id)) {
					exist = true;
					break;
				}
			}
			// 曾经匹配过
			for (ChatMatchLog m : matchLogList) {
				if (m.getOtherUserId().equals(t.userInfo.id)) {
					// exist = true;
					// break;
				}
			}
			if (exist) {
				continue;
			}
			chatInfoList.add(t);
		}
		Collections.sort(chatInfoList, (c1, c2) -> {
			return (int) (c2.updateDate.getTime() - c1.updateDate.getTime());
		});

		int count = 1;
		int km = 10;
		ok: while (true) {
			for (ChatInfoBean c : chatInfoList) {
				if (DistanceUtil.getDistance(req.longitude, req.latitude, c.locationReq.longitude, c.locationReq.latitude) <= count * km) {
					matchUserInfo = c.userInfo;
					break ok;
				}
			}
			if (count <= 10) {
				km += 10;
			} else {
				km += 100;
			}
			if (km >= 3500) {
				break;
			}
		}

		if (matchUserInfo == null) {
			List<User> users = userService.searchUser(100, user, req.sex);
			if (!users.isEmpty()) {
				User u = users.get(StringUtils.randomInt(users.size()));
				matchUserInfo = UserInfoData.fromUser(u);
			}
		}
		if (matchUserInfo == null) {
			resp.code = StatusCode.FAIL.value;
			resp.msg = "未匹配到合适用户";
			return resp;
		}
		resp.data = matchUserInfo;
		// 保存匹配记录
		matchLogService.add(new ChatMatchLog(StringUtils.randomUUID(), new Date(), userId, matchUserInfo.id));
		return resp;
	}

	@ApiOperation(value = "申请添加好友")
	@RequestMapping(value = "applyFriend", method = RequestMethod.POST)
	public BaseResp<Void> applyFriend(@RequestBody ApplyFriendReq req) {
		String friendId = req.friendId;
		String userId = getCurrentUserId();
		User friendUser = userService.getById(friendId);
		if (friendUser == null) {
			return BaseResp.fail("好友不存在");
		}
		if (userFriendService.isExist(userId, friendId)) {
			return BaseResp.fail("对方和你已经是好友关系");
		}
		User user = userService.getById(userId);
		ApplyLog applyLog = applyLogService.findLog(userId, friendId);
		if (applyLog == null) {
			applyLog = new ApplyLog(StringUtils.randomUUID(), userId, friendId, new Date());
			applyLogService.add(applyLog);
		}
		Channel channel = Const.onlineUser.get(friendId);
		if (channel == null) {
			PushUtil.sendPushWithCallback(friendId, MessageFormat.format("{0}申请添加你为好友", user.getNickname()));
		} else {
			ChatController.applyFriend(channel, UserInfoData.fromUser(user));
		}

		return BaseResp.SUCCESS;
	}

	@ApiOperation(value = "同意好友申请", notes = "applyUserId-发起申请者ID")
	@RequestMapping(value = "agreeApply", method = RequestMethod.POST)
	public BaseResp<Void> agreeApply(@RequestBody AgreeApplyReq req) {
		String applyUserId = req.applyUserId;
		String userId = getCurrentUserId();
		User friendUser = userService.getById(applyUserId);
		if (friendUser == null) {
			return BaseResp.fail("好友不存在");
		}
		if (userFriendService.isExist(userId, applyUserId)) {
			return BaseResp.fail("对方和你已经是好友关系");
		}
		ApplyLog applyLog = applyLogService.findLog(applyUserId, userId);
		if (applyLog == null) {
			return BaseResp.fail("未找到申请记录");
		}
		User user = userService.getById(userId);
		userFriendService.addFriend(friendUser, user, applyLog);
		Channel channel = Const.onlineUser.get(applyUserId);
		if (channel != null) {
			ChatController.agreeApply(channel, UserInfoData.fromUser(user));
		}

		return BaseResp.SUCCESS;
	}

	@ApiOperation(value = "删除好友")
	@RequestMapping(value = "deleteFriend", method = RequestMethod.POST)
	public BaseResp<Void> deleteFriend(@RequestBody ApplyFriendReq req) {
		String friendId = req.friendId;
		String userId = getCurrentUserId();
		User friendUser = userService.getById(friendId);
		if (friendUser == null) {
			return BaseResp.fail("好友不存在");
		}
		userFriendService.deleteFriend(userId, friendId);
		return BaseResp.SUCCESS;
	}

	/**
	 * 获取当前用户的ID
	 * 
	 * @return
	 */
	private String getCurrentUserId() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String userId = (String) request.getAttribute("userId");
		return userId;
	}

	/**
	 * 获取Head参数
	 */
	private String getHeadParam(String key) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String value = request.getHeader(key);
		return value;
	}
}

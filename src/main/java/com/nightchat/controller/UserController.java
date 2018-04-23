package com.nightchat.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nightchat.entity.User;
import com.nightchat.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Controller("user")
@RequestMapping("user")
@Api(tags = "user")
public class UserController {

	private Logger log = LogManager.getLogger(getClass());

	@Autowired
	private UserService userService;

	@ApiOperation(value = "注册接口", notes = "")
	@RequestMapping(value = "regist", method = RequestMethod.POST)
	@ResponseBody
	public String regist() {
		return "success";
	}

	@ApiOperation(value = "登录接口", notes = "phoneNum手机号,password密码")
	@RequestMapping(value = "login", method = RequestMethod.POST)
	@ResponseBody
	public String login(String phoneNum, String password) {
		User user = userService.getByPhone(phoneNum);
		return "success";
	}
}

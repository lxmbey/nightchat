package com.nightchat.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nightchat.net.Request;
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

	@ApiOperation("登录接口")
	@RequestMapping(name = "login", method = RequestMethod.POST)
	@ResponseBody
	public String login(Request req) {
		log.info(req.packet.data);
		userService.get(1);
		return "success";
	}
}

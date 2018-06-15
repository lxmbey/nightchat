package com.nightchat.test;

import java.util.Date;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.ApplicationContext;

import com.nightchat.entity.User;
import com.nightchat.service.UserService;
import com.nightchat.utils.StringUtils;

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class, scanBasePackages = "com.nightchat")
public class AddUser {
	public static String[] prefix = new String[] { "152", "136", "188", "177", "158", "166", "180" };

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(AddUser.class, args);
		UserService userService = context.getBean(UserService.class);
		for (int i = 0; i < 100; i++) {
			String phoneNum = prefix[StringUtils.randomInt(prefix.length)] + StringUtils.generateSmsCode() + StringUtils.generateSmsCode();
			User user = new User(StringUtils.randomUUID(), phoneNum, phoneNum, StringUtils.randomInt(2) == 0 ? "男" : "女", new Date(), "", "");
			userService.add(user);
		}
	}
}

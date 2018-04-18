package com.nightchat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.nightchat.common.AllFunctions;

@SpringBootApplication
public class AppRun {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(AppRun.class, args);
		context.getBean(AllFunctions.class).init();
	}
}

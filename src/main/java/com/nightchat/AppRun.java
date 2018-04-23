package com.nightchat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.ApplicationContext;

import com.nightchat.common.Functions;

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
public class AppRun {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(AppRun.class, args);
		context.getBean(Functions.class).init();
	}
}

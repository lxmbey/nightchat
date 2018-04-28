package com.nightchat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.nightchat.utils.http.HttpClientManager;

@Component
public class HttpConfig {

	@Bean
	public HttpClientManager xrHttp() {
		try {
			return new HttpClientManager(50, 100);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

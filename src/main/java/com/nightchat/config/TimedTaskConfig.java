package com.nightchat.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.nightchat.common.Const;
import com.nightchat.common.ThreadPool;
import com.nightchat.utils.DateUtils;
import com.nightchat.view.ChatInfoBean;

@Component
public class TimedTaskConfig {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@PostConstruct
	public void redisTask() {
		ThreadPool.scheduleWithFixedDelay(() -> {
			List<Object> str = redisTemplate.opsForHash().values(Const.CHAT_INFO_KEY);
			List<String> keys = new ArrayList<>();
			for (Object o : str) {
				ChatInfoBean t = JSON.parseObject(o.toString(), ChatInfoBean.class);
				if (System.currentTimeMillis() - t.updateDate.getTime() >= TimeUnit.DAYS.toMillis(3)) {
					keys.add(t.userInfo.id);
				}
			}
			redisTemplate.opsForHash().delete(Const.CHAT_INFO_KEY, keys);
		}, DateUtils.betweenTaskHourMillis(5, 0), TimeUnit.DAYS.toMillis(1), TimeUnit.DAYS);
	}
}

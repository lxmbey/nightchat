package com.nightchat.common;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class ConfigAspect {

	//Controller层切点  
	//	@Pointcut("execution (* com.xption.credit.engine.controller.ConfigController.update*(..))" + " || execution (* com.xption.credit.engine.controller.ConfigController.add*(..))"
	//			+ " || execution (* com.xption.credit.engine.controller.ConfigController.delete*(..))")
	//	public void controllerAspect() {
	//	}
	//
	//	/**
	//	 * 环绕通知 用于检测授权、记录用户的操作
	//	 * 
	//	 * @param joinPoint 切点
	//	 */
	//	@Around("controllerAspect()")
	//	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
	//		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	//		String accreditCode = request.getHeader("accreditCode");
	//		boolean b = false;
	//		if (accreditCode != null) {
	//			b = redisTemplate.execute(new RedisCallback<Boolean>() {
	//
	//				@Override
	//				public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
	//					connection.select(redisIndex);
	//					return connection.exists(redisTemplate.getStringSerializer().serialize(accreditCode));
	//				}
	//			});
	//		}
	//		if (!b) {
	//			throw new RuntimeException("未授权操作");
	//		}
	//		Object obj = joinPoint.proceed();
	//		return obj;
	//	}

}
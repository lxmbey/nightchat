package com.nightchat.config;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.nightchat.common.Const;
import com.nightchat.common.NotLogin;
import com.nightchat.common.NotLoginException;

@Aspect
@Component
public class AspectConfig {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	// Controller层切点  
	@Pointcut("execution (* com.nightchat.controller..*(..))")
	public void controllerAspect() {
	}

	/**
	 * 环绕通知 用于检测授权、记录用户的操作
	 * 
	 * @param joinPoint 切点
	 */
	@Around("controllerAspect()")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		Signature signature = joinPoint.getSignature();
		MethodSignature methodSignature = (MethodSignature) signature;
		Method targetMethod = methodSignature.getMethod();
		NotLogin notLogin = targetMethod.getAnnotation(NotLogin.class);
		if (notLogin == null) {// 需要登陆的方法
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			String sessionKey = request.getHeader(Const.SESSION_KEY);
			boolean login = false;
			if (sessionKey != null) {
				Object id = redisTemplate.opsForValue().get(Const.REDIS_SESSION_KEY + sessionKey);
				if (id != null) {
					login = true;
				}
			}
			if (!login) {
				throw new NotLoginException();
			}
		}
		Object obj = joinPoint.proceed();
		return obj;
	}

}
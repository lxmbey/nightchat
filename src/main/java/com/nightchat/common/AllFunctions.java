package com.nightchat.common;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.ReflectionUtils;

/**
 * 游戏逻辑服务器所有提供给客户端调用的接口扫描，初始化并建立名称到Method的映射
 */
@Component
public class AllFunctions {

	private static Map<String, MethodWrapper> methodMap = new HashMap<String, MethodWrapper>();

	@Autowired
	private ApplicationContext appCtx;

	/**
	 * 获取接口中定义的所有方法
	 */
	private Map<String, Method> getProtocolMethods(Class<?> instanceClass) {
		Map<String, Method> parentMethodMap = new HashMap<>();
		if (instanceClass != null) {
			Method[] methods = instanceClass.getDeclaredMethods();
			if (methods != null) {
				for (Method method : methods) {
					parentMethodMap.put(method.getName(), method);
				}
			}
		}
		return parentMethodMap;
	}

	private String getClassIdentity(Class<?> clazz) {
		Controller name = clazz.getAnnotation(Controller.class);
		if (name != null && !StringUtils.isBlank(name.value())) {
			return name.value();
		}
		Class<?>[] interfaces = clazz.getInterfaces();
		if (interfaces != null) {
			for (Class<?> inter : interfaces) {
				Controller interLogic = inter.getAnnotation(Controller.class);
				if (interLogic != null && !StringUtils.isBlank(interLogic.value())) {
					return interLogic.value();
				}
			}
		}
		return clazz.getSimpleName();
	}

	public void init() {
		Map<String, Object> controlerMap = appCtx.getBeansWithAnnotation(Controller.class);
		Map<String, Object> instanceMap = new HashMap<String, Object>();
		for (Object instance : controlerMap.values()) {
			if (instance != null) {
				instanceMap.put(instance.getClass().getSimpleName(), instance);
			}
		}
		for (Map.Entry<String, Object> entry : instanceMap.entrySet()) {
			Object obj = entry.getValue();
			Class<?> realClass = AopUtils.getTargetClass(obj);
			String instanceName = getClassIdentity(realClass);
			Map<String, Method> parentMethodMap = getProtocolMethods(realClass);
			if (parentMethodMap != null) {
				for (Map.Entry<String, Method> parentMethodEntry : parentMethodMap.entrySet()) {
					String methodName = parentMethodEntry.getKey();
					Method parentMethod = parentMethodEntry.getValue();
					String methodIdentity = instanceName + "/" + methodName;
					if (methodMap.containsKey(methodIdentity)) {
						throw new RuntimeException("duplicate method with name " + methodIdentity);
					}
					if (parentMethod.getAnnotation(Push.class) != null) {// 推送接口不处理
						continue;
					}
					Method method = ReflectionUtils.findMethod(obj.getClass(), methodName,
							parentMethod.getParameterTypes());
					method.setAccessible(true);
					boolean notLogin = (method.getAnnotation(NotLogin.class) != null);
					if (!notLogin) {
						notLogin = (parentMethod.getAnnotation(NotLogin.class) != null);
					}
					Class<?>[] args = method.getParameterTypes();
					MethodWrapper wrapper = new MethodWrapper(methodName, notLogin, method, args, obj);
					methodMap.put(methodIdentity, wrapper);
				}
			}
		}
	}

	public static MethodWrapper getMethod(String name) {
		return methodMap.get(name);
	}

	/**
	 * 对于 {@link Method} 的一个包装
	 */
	public static class MethodWrapper {
		public String name;
		public boolean notLogin;// 不需要登录
		public Method method;
		public Class<?>[] args;
		public Object instance;

		public MethodWrapper(String name, boolean notLogin, Method method, Class<?>[] args, Object instance) {
			this.name = name;
			this.notLogin = notLogin;
			this.method = method;
			this.args = args;
			this.instance = instance;
		}
	}
}

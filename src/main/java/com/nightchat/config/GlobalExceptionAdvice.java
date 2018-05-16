package com.nightchat.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import com.nightchat.common.NotLoginException;
import com.nightchat.view.BaseResp;
import com.nightchat.view.BaseResp.StatusCode;

/**
 * 全局异常处理
 * 
 * @author lxm
 *
 */
@RestController
@ControllerAdvice
public class GlobalExceptionAdvice {
	private static final Logger logger = LogManager.getLogger(GlobalExceptionAdvice.class);

	@ExceptionHandler(value = Throwable.class)
	public BaseResp<Void> errorHandler(Throwable e) {
		if (e instanceof NotLoginException) {
			return new BaseResp<Void>(StatusCode.SESSION_TIMEOUT.value, "未登录操作");
		}
		logger.error(e.getMessage(), e);
		return new BaseResp<Void>(StatusCode.ERROR.value, "系统异常：" + e.getMessage());
	}
}

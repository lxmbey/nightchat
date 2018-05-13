package com.nightchat.common;

/**
 * 未登录异常
 * 
 * @author lxm
 *
 */
public class NotLoginException extends RuntimeException {

	private static final long serialVersionUID = -1998723463856618597L;

	public NotLoginException() {
		super();
	}

	public NotLoginException(String msg) {
		super(msg);
	}
}

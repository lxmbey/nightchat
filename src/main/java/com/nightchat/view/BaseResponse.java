package com.nightchat.view;

public class BaseResponse {
	public int code;
	public String msg;

	public static BaseResponse SUCCESS = new BaseResponse(1, "");

	public BaseResponse() {

	}

	public BaseResponse(int code, String msg) {
		super();
		this.code = code;
		this.msg = msg;
	}

	public enum StatusCode {
		SUCCESS(1), FAIL(2), SESSION_TIMEOUT(3);
		public int value;

		private StatusCode(int value) {
			this.value = value;
		}
	}

}

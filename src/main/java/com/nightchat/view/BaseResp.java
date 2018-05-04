package com.nightchat.view;

import io.swagger.annotations.ApiModelProperty;

public class BaseResp {
	@ApiModelProperty("请求状态码")
	public int code = StatusCode.SUCCESS.value;
	@ApiModelProperty("提示信息")
	public String msg = "";

	@ApiModelProperty("通用数据")
	public String data;

	public static BaseResp SUCCESS = new BaseResp(1, "");

	public BaseResp() {

	}

	public BaseResp(int code, String msg) {
		super();
		this.code = code;
		this.msg = msg;
	}

	public static BaseResp fail(String msg) {
		return new BaseResp(StatusCode.FAIL.value, msg);
	}

	public enum StatusCode {
		SUCCESS(1), FAIL(2), SESSION_TIMEOUT(3);
		public int value;

		private StatusCode(int value) {
			this.value = value;
		}
	}

}

package com.nightchat.view;

import java.util.HashMap;
import java.util.Map;

import io.swagger.annotations.ApiModelProperty;

public class BaseResp {
	@ApiModelProperty("请求状态码，200成功400失败401会话超时500程序错误")
	public int code = StatusCode.SUCCESS.value;
	@ApiModelProperty("提示信息")
	public String msg = "";

	@ApiModelProperty("数据")
	public Map<String, Object> data = new HashMap<>();

	public static BaseResp SUCCESS = new BaseResp(StatusCode.SUCCESS.value, "");

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
		SUCCESS(200), FAIL(400), SESSION_TIMEOUT(401), ERROR(500);
		public int value;

		private StatusCode(int value) {
			this.value = value;
		}
	}

}

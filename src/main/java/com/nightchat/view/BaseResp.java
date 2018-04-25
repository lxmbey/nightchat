package com.nightchat.view;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("基础返回")
public class BaseResp {
	@ApiModelProperty("请求状态码")
	public int code = StatusCode.SUCCESS.value;
	@ApiModelProperty("提示信息")
	public String msg = "";

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

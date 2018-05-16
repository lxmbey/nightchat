package com.nightchat.view;

import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * @author lxm
 *
 * @param <Data> 返回数据类型
 */
public class BaseResp<Data> {
	@ApiModelProperty("请求状态码，200成功400失败401会话超时500程序错误")
	public int code = StatusCode.SUCCESS.value;
	@ApiModelProperty("提示信息")
	public String msg = "";

	@ApiModelProperty("返回数据")
	public Data data;

	public static BaseResp<Void> SUCCESS = new BaseResp<>();

	public static BaseResp<Object> PUSH_DATA = new BaseResp<>(1254);

	public BaseResp() {

	}

	/**
	 * 成功并返回数据
	 * 
	 * @param data 返回的数据
	 * @return
	 */
	public static <T> BaseResp<T> success(T data) {
		return new BaseResp<T>(data);
	}

	public BaseResp(Data data) {
		this.data = data;
	}

	public BaseResp(int code, String msg) {
		super();
		this.code = code;
		this.msg = msg;
	}

	public static BaseResp<Void> fail(String msg) {
		return new BaseResp<Void>(StatusCode.FAIL.value, msg);
	}

	public enum StatusCode {
		SUCCESS(200), FAIL(400), SESSION_TIMEOUT(401), ERROR(500);
		public int value;

		private StatusCode(int value) {
			this.value = value;
		}
	}

}

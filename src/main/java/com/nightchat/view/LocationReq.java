package com.nightchat.view;

import io.swagger.annotations.ApiModelProperty;

public class LocationReq {
	@ApiModelProperty("经度")
	public double longitude;
	@ApiModelProperty("纬度")
	public double latitude;
}

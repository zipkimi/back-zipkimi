package com.safeinterior.board.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel
public class FraudPreventionGetRequest {
	@ApiModelProperty(name = "유저 ID", value = "", example = "1", required = true, notes = "")
	private Long userId;

	private String title;

	private String subTitle;

	private String content;
}

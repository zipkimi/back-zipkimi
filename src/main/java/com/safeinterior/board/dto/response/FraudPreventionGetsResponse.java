package com.safeinterior.board.dto.response;

import java.time.ZonedDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class FraudPreventionGetsResponse {
	@ApiModelProperty(name = "게시글 고유번호", value = "", example = "1", required = true, notes = "")
	private Long id;

	@ApiModelProperty(name = "제목", value = "", example = "TEXT 제목", required = false, notes = "영문기준 200자 이하")
	private String title;

	@ApiModelProperty(name = "부제목", value = "", example = "TEXT 부제목", required = false, notes = "영문기준 300자 이하")
	private String subTitle;

	@ApiModelProperty(name = "내용", value = "", example = "TEXT 내용", required = false, notes = "")
	private String content;

	@ApiModelProperty(name = "게시글 유형", value = "", example = "fraudPrevention", required = false, notes = "")
	private String type;

	@ApiModelProperty(name = "게시글 등록일자", value = "", example = "2022-12-26 17:22:32", required = false, notes = "")
	private ZonedDateTime regDt;
}

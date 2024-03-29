package com.zipkimi.global.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@ApiModel
@AllArgsConstructor
@RequiredArgsConstructor
public class FraudPreventionGetResponse {

    @ApiModelProperty(name = "게시글 고유번호", example = "1", required = true)
    private Long id;

    @ApiModelProperty(name = "제목", example = "TEXT 제목", notes = "영문기준 200자 이하")
    private String title;

    @ApiModelProperty(name = "부제목", example = "TEXT 부제목", notes = "영문기준 300자 이하")
    private String subTitle;

    @ApiModelProperty(name = "내용", example = "TEXT 내용")
    private String content;

    @ApiModelProperty(name = "게시글 등록일자", example = "2022-12-26 17:22:32")
    private ZonedDateTime regDt;

}

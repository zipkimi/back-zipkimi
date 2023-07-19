package com.zipkimi.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@ApiModel
@AllArgsConstructor
@RequiredArgsConstructor
public class SmsAuthNumberPostResponse {

    @ApiModelProperty(name = "결과", example = "인증번호를 전송하였습니다., 이미 사용중인 휴대전화 번호입니다.", required = true)
    private String message;

    @ApiModelProperty(name = "SMS 인증 아이디", example = "123")
    private long smsAuthId;
}

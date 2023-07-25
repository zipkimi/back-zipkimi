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
public class BaseResponse {

    @ApiModelProperty(name = "응답 메시지", example = "본인 인증에 성공했습니다., 인증번호를 확인해주세요.")
    private String message;
}

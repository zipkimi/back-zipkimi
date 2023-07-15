package com.zipkimi.dto.response;

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
public class SmsAuthNumberPostResponse {

    @ApiModelProperty(name = "결과", example = "인증번호를 전송하였습니다., 이미 사용중인 휴대전화 번호입니다.", required = true)
    private String result;
}

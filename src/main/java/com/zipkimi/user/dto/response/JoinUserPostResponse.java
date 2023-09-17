package com.zipkimi.user.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@ApiModel
public class JoinUserPostResponse {

    @ApiModelProperty(name = "결과", example = "본인 인증에 성공했습니다., 인증번호를 확인해주세요.")
    private String message;

    @ApiModelProperty(value = "이름", name = "name", example = "유춘식")
    private String name;

    @ApiModelProperty(value = "사용자 아이디", name = "userId", example = "abc1223@gmail.com")
    private Long userId;

}

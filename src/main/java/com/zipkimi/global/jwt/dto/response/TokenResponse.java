package com.zipkimi.global.jwt.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {

    private String grantType;
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpireDate;

    @ApiModelProperty(name = "결과", example = "인증번호를 전송하였습니다., 이미 사용중인 휴대전화 번호입니다.", required = true)
    private String message;

}

package com.zipkimi.global.jwt.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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

    @JsonInclude(Include.NON_NULL)
    private String grantType;

    @JsonInclude(Include.NON_NULL)
    private String accessToken;

    @JsonInclude(Include.NON_NULL)
    private String refreshToken;

    @JsonInclude(Include.NON_NULL)
    private Long accessTokenExpireDate;

    @ApiModelProperty(name = "결과", example = "인증번호를 전송하였습니다., 이미 사용중인 휴대전화 번호입니다.", required = true)
    private String message;

}

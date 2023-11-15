package com.zipkimi.builder.dto.response;

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
public class JoinBuilderUserPostResponse {

    @ApiModelProperty(name = "결과", example = "본인 인증에 성공했습니다., 인증번호를 확인해주세요.")
    private String message;

    @ApiModelProperty(value = "업체명", name = "name", example = "한샘하우스")
    private String builderName;

    @ApiModelProperty(value = "시공사 회원 이메일", name = "email", example = "builderMember@gmail.com")
    private String email;

}

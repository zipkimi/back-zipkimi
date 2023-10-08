package com.zipkimi.builder.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@Builder
@ApiModel
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class JoinBuilderUserPostRequest {

    @NonNull
    @ApiModelProperty(value = "시공사 회원 이메일", name = "email", example = "builderMember@gmail.com")
    private String email;

    @NonNull
    @ApiModelProperty(value = "패스워드", name = "pw", example = "1234")
    private String password;

    @NonNull
    @ApiModelProperty(value = "업체명", name = "name", example = "집킴이하우스")
    private String builderName;

    @NonNull
    @ApiModelProperty(value = "SMS 인증 아이디", name = "smsAuthId", example = "1")
    private Long smsAuthId;

}

package com.zipkimi.user.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@RequiredArgsConstructor
@NoArgsConstructor
@ApiModel
public class JoinUserPostRequest {

    @NonNull
    @ApiModelProperty(value = "이메일", name = "email", example = "abc1223@gmail.com")
    String email;

    @NonNull
    @ApiModelProperty(value = "패스워드", name = "pw", example = "1234")
    String pw;

    @NonNull
    @ApiModelProperty(value = "이름", name = "name", example = "유춘식")
    String name;

    @NonNull
    @ApiModelProperty(value = "SMS 인증 아이디", name = "smsAuthId", example = "1234")
    Long smsAuthId;

}

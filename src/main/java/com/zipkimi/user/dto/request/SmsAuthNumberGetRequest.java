package com.zipkimi.user.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@ApiModel
@NoArgsConstructor
@RequiredArgsConstructor
public class SmsAuthNumberGetRequest {

    @NonNull
    @ApiModelProperty(name = "SMS 인증 아이디", example = "123")
    private long smsAuthId;

    @NonNull
    @ApiModelProperty(name = "휴대폰 번호", example = "01000000000", required = true)
    private String phoneNumber;

    @NonNull
    @ApiModelProperty(name = "인증 번호", example = "0000", required = true)
    private String smsAuthNumber;
}

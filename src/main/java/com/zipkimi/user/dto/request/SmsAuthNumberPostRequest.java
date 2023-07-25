package com.zipkimi.user.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@ApiModel
@NoArgsConstructor
@RequiredArgsConstructor
public class SmsAuthNumberPostRequest {
    @NonNull
    @ApiModelProperty(name = "휴대폰 번호", example = "01000000000", required = true)
    private String phoneNumber;

}

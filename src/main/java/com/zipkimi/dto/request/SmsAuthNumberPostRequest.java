package com.zipkimi.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

@Getter
@Builder
@ApiModel
@RequiredArgsConstructor
public class SmsAuthNumberPostRequest {

    @NonNull
    // TODO  글자수, 타입 유효성 추가
    @ApiModelProperty(name = "휴대폰 번호", example = "01000000000", required = true)
    private String phoneNumber;

}

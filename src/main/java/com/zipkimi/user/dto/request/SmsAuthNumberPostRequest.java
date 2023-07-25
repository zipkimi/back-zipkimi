package com.zipkimi.user.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class SmsAuthNumberPostRequest {

    @NonNull
    @ApiModelProperty(name = "휴대폰 번호", example = "01000000000", required = true)
    private String phoneNumber;

}

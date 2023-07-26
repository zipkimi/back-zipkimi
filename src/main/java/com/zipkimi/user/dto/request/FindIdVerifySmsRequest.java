package com.zipkimi.user.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true) // 기본 생성자 추가
public class FindIdVerifySmsRequest {

    @NonNull
    @ApiModelProperty(value = "휴대폰번호", name = "phoneNumber", notes = "이곳에 휴대폰번호를 넣어주세요", example = "01000000000", required = true)
    private String phoneNumber;

    @NonNull
    @ApiModelProperty(value = "인증번호", name = "smsAuthNumber", notes = "이곳에 인증번호를 넣어주세요", example = "1234", required = true)
    private String smsAuthNumber;
}

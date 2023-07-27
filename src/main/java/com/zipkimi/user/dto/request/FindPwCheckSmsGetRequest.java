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
public class FindPwCheckSmsGetRequest {

    @NonNull
    @ApiModelProperty(value = "SMS 인증 아이디", name = "smsAuthId", example = "1234")
    private long smsAuthId;

    @NonNull
    @ApiModelProperty(value = "휴대폰번호", name = "phoneNumber", example = "01000000000", required = true)
    private String phoneNumber;

    @NonNull
    @ApiModelProperty(value = "인증번호", name = "smsAuthNumber", example = "1234", required = true)
    private String smsAuthNumber;

    @NonNull
    @ApiModelProperty(value = "이메일", name = "email", notes = "이곳에 이메일을 넣어주세요", example = "test@gmail.com", required = true)
    private String email;
}

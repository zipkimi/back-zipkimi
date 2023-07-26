package com.zipkimi.user.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true) // 기본 생성자 추가
public class PassResetSmsAuthNumberPostRequest {

    @NonNull
    @ApiModelProperty(value = "휴대폰번호", name = "phoneNumber", notes = "이곳에 휴대폰번호를 넣어주세요", example = "01000000000", required = true)
    private String phoneNumber;

    @NonNull
    @ApiModelProperty(value = "이메일", name = "email", notes = "이곳에 이메일을 넣어주세요", example = "test@gmail.com", required = true)
    private String email;

}

package com.zipkimi.global.jwt.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Builder
@Setter
public class TokenRequest {

    @NonNull
    @ApiModelProperty(value = "accessToken", name = "accessToken", example = "eyJ0eXAiOiJKV1QiLCJh", required = true)
    private String accessToken;

    @NonNull
    @ApiModelProperty(value = "refreshToken", name = "refreshToken", example = "eyJ0eXAiOiJK", required = true)
    private String refreshToken;

    private String key;

}

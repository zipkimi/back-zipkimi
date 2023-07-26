package com.zipkimi.user.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class SendFindSmsAuthNumberPostResponse {

    @ApiModelProperty(name = "결과", example = "인증번호를 전송하였습니다. / 등록되지 않은 회원입니다.", required = true)
    private String result;
}

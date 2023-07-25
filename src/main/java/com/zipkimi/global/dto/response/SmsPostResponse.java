package com.zipkimi.global.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class SmsPostResponse {

    String requestId;
    LocalDateTime requestTime;
    String statusCode;
    String statusName;
}

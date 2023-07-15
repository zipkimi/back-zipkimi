package com.zipkimi.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsPostResponse {

    String requestId;
    LocalDateTime requestTime;
    String statusCode;
    String statusName;
}

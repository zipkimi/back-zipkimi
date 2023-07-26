package com.zipkimi.global.dto.request;

import com.zipkimi.global.common.sms.SmsMessage;
import java.util.List;
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
public class SmsPostRequest {

    String type;
    String contentType;
    String countryCode;
    String from;
    String content;
    List<SmsMessage> messages;
}

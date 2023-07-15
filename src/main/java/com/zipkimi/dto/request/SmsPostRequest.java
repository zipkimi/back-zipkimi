package com.zipkimi.dto.request;

import com.zipkimi.common.sms.SmsMessage;
import java.util.List;
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
public class SmsPostRequest {

    String type;
    String contentType;
    String countryCode;
    String from;
    String content;
    List<SmsMessage> messages;
}

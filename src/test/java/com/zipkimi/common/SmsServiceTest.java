package com.zipkimi.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.zipkimi.common.sms.SmsMessage;
import com.zipkimi.dto.response.SmsPostResponse;
import com.zipkimi.service.SmsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SmsServiceTest {
    @Autowired
    SmsService smsService;

    @Test
    void sendSuccessTest() {
        String content = "Test 확인";
        String phoneNumber = "01097050821";
        SmsMessage message = SmsMessage.builder()
                .to(phoneNumber)
                .content(content)
                .build();
        SmsPostResponse response = null;
        try{
            response = smsService.send(message);
        }catch (Exception e){

        }
        assertEquals("202" , response.getStatusCode());
        assertEquals("success" , response.getStatusName());
    }
}
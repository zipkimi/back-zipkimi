package com.zipkimi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.zipkimi.dto.response.SmsPostResponse;
import com.zipkimi.entity.SmsAuthEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SmsServiceTest {
    @Autowired
    SmsService smsService;

    @Test
    @DisplayName("SMS 전송 성공 테스트")
    void pushSMSMessageSuccessTest() {
        // given
        String content = "Test 확인";
        String phoneNumber = "01097050821";
        String randomNumber = "1234";

        SmsAuthEntity smsAuth = new SmsAuthEntity();
        smsAuth.setPhoneNumber(phoneNumber);
        smsAuth.setSmsAuthNumber(randomNumber);
        smsAuth.setIsAuthenticate(false);
        smsAuth.setContent(content);

        // when
        SmsPostResponse response = smsService.pushSMSMessage(smsAuth);

        // then
        assertEquals("202" , response.getStatusCode());
        assertEquals("success" , response.getStatusName());
    }
}
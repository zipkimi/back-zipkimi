package com.zipkimi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.zipkimi.dto.request.SmsAuthNumberPostRequest;
import com.zipkimi.entity.SmsAuthEntity;
import com.zipkimi.entity.UserEntity;
import com.zipkimi.repository.SmsAuthRepository;
import com.zipkimi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SmsAuthRepository smsAuthRepository;

    @Test
    public void userTest() {
        SmsAuthEntity smsAuth = new SmsAuthEntity();
        smsAuth.setSmsAuthNumber("1234");
        smsAuth.setPhoneNumber("01012345678");
        smsAuth.setIsAuthenticate(false);
        smsAuthRepository.save(smsAuth);

        UserEntity user = new UserEntity();
        user.setName("유푸름");
        user.setEmail("ypr821@gmail.com");
        user.setPassword("test123");
        user.setPhoneNumber("01012345678");
        user.setBuilderId("general");
        userRepository.save(user);
    }

    @Test
    void sendSmsAuthNumberSuccessTest() {
        // given
        SmsAuthNumberPostRequest request = SmsAuthNumberPostRequest.builder()
                .phoneNumber("").build();
        // then                               // when
        assertEquals("인증번호 전송 완료", userService.sendSmsAuthNumber(request).getResult());
    }

    @Test
    void sendSmsAuthNumberFailTest() {
        // 이미 등록한 전화번호
    }

    @Test
    void sendSmsAuthNumberWithoutPhoneNumberFailTest() {
        // 요청시 입력받은 전화번호가 null 인 경우
    }
}
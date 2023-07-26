package com.zipkimi.user.service;

import static org.junit.jupiter.api.Assertions.*;

import com.zipkimi.entity.UserEntity;
import com.zipkimi.global.service.SmsService;
import com.zipkimi.repository.SmsAuthRepository;
import com.zipkimi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserLoginServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SmsAuthRepository smsAuthRepository;

    @Mock
    private SmsService smsService;

    @InjectMocks
    private UserLoginService loginService;

    @Test
    void getEmailByPhoneNumber_ExistingUser_ReturnEmail(){

        //given
        //가상의 휴대폰 번호, 유저 객체 생성
        String phoneNumber = "01012345678";
        UserEntity user = new UserEntity();
        user.setUserId(1L);
        user.setPhoneNumber(phoneNumber);
        user.setEmail("abc@gmail.com");

        //when
        Mockito.when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(user);

        //then
        String result = loginService.getEmailByPhoneNumber(phoneNumber);
        assertEquals("abc@gmail.com", result);

    }

    @Test
    void getEmailByPhoneNumber_NonExistingUser_ReturnNull(){

        //given
        String phoneNumber = "01012345678";

        //when
        //가상의 휴대폰 번호에 해당하는 사용자가 없다고 설정 (null을 반환)
        Mockito.when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(null);

        //then
        String result = loginService.getEmailByPhoneNumber(phoneNumber);
        assertNull(result);


    }

}
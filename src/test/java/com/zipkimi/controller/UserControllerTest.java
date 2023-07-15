package com.zipkimi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zipkimi.dto.request.SmsAuthNumberPostRequest;
import com.zipkimi.entity.UserEntity;
import com.zipkimi.repository.UserRepository;
import javax.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    UserRepository userRepository;

    @Test
    void sendSmsAuthNumberSuccessTest() throws Exception {
        // given
        SmsAuthNumberPostRequest smsAuthNumberPostRequest = SmsAuthNumberPostRequest.builder()
                .phoneNumber("01097050821")
                .build();
        String json = objectMapper.writeValueAsString(smsAuthNumberPostRequest);
        // when
        mockMvc.perform(post("/api/user/auth/sms/number")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json))
                .andDo(print())
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("인증번호를 전송하였습니다."));
    }

    @Test
    void sendSmsAuthNumberFailTest() throws Exception {
        // given
        SmsAuthNumberPostRequest smsAuthNumberPostRequest = SmsAuthNumberPostRequest.builder()
                .phoneNumber("01097050821")
                .build();
        UserEntity user = new UserEntity();
        user.setPhoneNumber("01097050821");
        user.setName("testName");
        user.setEmail("testEmail");
        user.setUse(true);
        userRepository.save(user);
        String json = objectMapper.writeValueAsString(smsAuthNumberPostRequest);
        // when
        mockMvc.perform(post("/api/user/auth/sms/number")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json))
                .andDo(print())
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("이미 등록된 휴대폰 번호입니다."));
    }
}
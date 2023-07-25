package com.zipkimi.user.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zipkimi.entity.UserEntity;
import com.zipkimi.repository.UserRepository;
import com.zipkimi.user.dto.request.SmsAuthNumberPostRequest;
import javax.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class UserManagementControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName(value = "인증번호 전송 성공 테스트")
    void sendSmsAuthNumberSuccessTest() throws Exception {
        // given
        SmsAuthNumberPostRequest smsAuthNumberPostRequest = SmsAuthNumberPostRequest.builder()
                .phoneNumber("01097050821")
                .build();
        String json = objectMapper.writeValueAsString(smsAuthNumberPostRequest);
        // when
        mockMvc.perform(post("/api/v1/user/auth/sms/number")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json))
                .andDo(print())
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("인증번호를 전송하였습니다."));
    }

    @Test
    @DisplayName(value = "인증번호 전송 실패 테스트")
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
        mockMvc.perform(post("/api/v1/user/auth/sms/number")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json))
                .andDo(print())
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("이미 등록된 휴대폰 번호입니다."));
    }

    @Test
    void checkSmsAuthNumber() {
        // TODO test 작성
    }
}

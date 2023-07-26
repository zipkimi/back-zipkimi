package com.zipkimi.user.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zipkimi.entity.SmsAuthEntity;
import com.zipkimi.entity.UserEntity;
import com.zipkimi.repository.SmsAuthRepository;
import com.zipkimi.repository.UserRepository;
import com.zipkimi.user.dto.request.SmsAuthNumberGetRequest;
import com.zipkimi.user.dto.request.SmsAuthNumberPostRequest;
import java.time.LocalDateTime;
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
    @Autowired
    SmsAuthRepository smsAuthRepository;

    @Test
    @DisplayName(value = "인증번호 전송 성공 테스트")
    void sendSmsAuthNumberSuccessTest() throws Exception {
        // given
        SmsAuthNumberPostRequest smsAuthNumberPostRequest = SmsAuthNumberPostRequest.builder()
                .phoneNumber("01097050821")
                .build();
        String json = objectMapper.writeValueAsString(smsAuthNumberPostRequest);
        // when
        mockMvc.perform(post("/api/v1/userMgmt/users/sms")
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
        mockMvc.perform(post("/api/v1/userMgmt/users/sms")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json))
                .andDo(print())
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("이미 등록된 휴대폰 번호입니다."));
    }

    @Test
    void checkSmsAuthNumberSuccessTest() throws Exception {
        // given
        SmsAuthEntity smsAuth = new SmsAuthEntity();
        smsAuth.setSmsAuthNumber("0000");
        smsAuth.setPhoneNumber("01097050821");
        smsAuth.setExpirationTime(LocalDateTime.now().plusMinutes(3L));
        //TODO Type값 설정 필요
        smsAuth.setSmsAuthType("In");
        smsAuth.setIsUse(true);
        smsAuth.setIsAuthenticate(false);
        smsAuth.setContent("본인확인 인증번호 (" + smsAuth.getSmsAuthNumber() + ")입력시 \n"
                + "정상처리 됩니다.");
        SmsAuthEntity savedSmsAuth = smsAuthRepository.save(smsAuth);

        SmsAuthNumberGetRequest request = SmsAuthNumberGetRequest.builder()
                .phoneNumber("01097050821")
                .smsAuthNumber("0000")
                .smsAuthId(savedSmsAuth.getSmsAuthId())
                .build();
        String json = objectMapper.writeValueAsString(request);
        // when
        mockMvc.perform(get("/api/v1/userMgmt/users/sms/" + savedSmsAuth.getSmsAuthId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json))
                .andDo(print())
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("본인 인증에 성공했습니다."));
    }
}

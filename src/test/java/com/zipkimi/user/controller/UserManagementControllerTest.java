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
import com.zipkimi.user.dto.request.JoinUserPostRequest;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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
    @DisplayName(value = "인증번호 전송 실패 테스트")
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
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("phoneNumber", "01097050821");
        params.add("smsAuthNumber", "0000");
        params.add("smsAuthId", String.valueOf(savedSmsAuth.getSmsAuthId()));
        // when
        mockMvc.perform(get("/api/v1/userMgmt/users/sms/" + savedSmsAuth.getSmsAuthId())
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("본인 인증에 성공했습니다."));
    }

    @Test
    @DisplayName(value = "회원가입 성공 테스트")
    void joinUserSuccessTest() throws Exception {
        //given
        SmsAuthEntity smsAuth = new SmsAuthEntity();
        smsAuth.setSmsAuthNumber("0000");
        smsAuth.setPhoneNumber("01000000000");
        smsAuth.setExpirationTime(LocalDateTime.now().plusMinutes(3L));
        smsAuth.setSmsAuthType("JOIN");
        smsAuth.setIsUse(true);
        smsAuth.setIsAuthenticate(true);
        smsAuthRepository.save(smsAuth);

        JoinUserPostRequest requestDto = JoinUserPostRequest.builder()
                .email("test@gmail.com")
                .name("test name")
                .pw("test123@")
                .smsAuthId(smsAuth.getSmsAuthId())
                .build();

        String json = objectMapper.writeValueAsString(requestDto);
        // when
        mockMvc.perform(post("/api/v1/userMgmt/users")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("회원가입 완료"));

    }
}

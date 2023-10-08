package com.zipkimi.builder.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zipkimi.builder.service.BuilderManagementService;
import com.zipkimi.user.dto.request.SmsAuthNumberPostRequest;
import com.zipkimi.user.dto.response.SmsAuthNumberPostResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class BuilderManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BuilderManagementService builderManagementService;

    @Test
    @DisplayName(value = "시공사 회원 가입 - SMS 인증번호 전송 성공 테스트")
    void sendBuilderUserJoinSmsAuthNumberSuccessTest() throws Exception {

        //given
        SmsAuthNumberPostRequest requestDto = new SmsAuthNumberPostRequest("01094342762");
        SmsAuthNumberPostResponse response = SmsAuthNumberPostResponse.builder().message("인증번호를 전송하였습니다.").build();

        when(builderManagementService.sendBuilderUserJoinSmsAuthNumber(
                any(SmsAuthNumberPostRequest.class))).thenReturn(response);

        String requestBody = objectMapper.writeValueAsString(requestDto);

        //when
        mockMvc.perform(post("/api/v1/builderMgmt/builders/sms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                //then
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.message").value("인증번호를 전송하였습니다."));

    }

    @Test
    @DisplayName(value = "시공사 회원 가입 - SMS 인증번호 전송 실패 테스트")
    void sendBuilderUserJoinSmsAuthNumberFailureTest() throws Exception {

        //given
        SmsAuthNumberPostRequest requestDto = new SmsAuthNumberPostRequest("01094342762");
        SmsAuthNumberPostResponse response = SmsAuthNumberPostResponse.builder().message("이미 등록된 휴대폰 번호입니다.").build();

        when(builderManagementService.sendBuilderUserJoinSmsAuthNumber(
                any(SmsAuthNumberPostRequest.class))).thenReturn(response);

        String requestBody = objectMapper.writeValueAsString(requestDto);

        //when
        mockMvc.perform(post("/api/v1/builderMgmt/builders/sms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                //then
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.message").value("이미 등록된 휴대폰 번호입니다."));

    }

}
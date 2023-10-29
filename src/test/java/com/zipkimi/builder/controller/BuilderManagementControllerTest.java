package com.zipkimi.builder.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zipkimi.builder.dto.request.JoinBuilderUserPostRequest;
import com.zipkimi.builder.dto.response.JoinBuilderUserPostResponse;
import com.zipkimi.builder.service.BuilderManagementService;
import com.zipkimi.user.dto.request.SmsAuthNumberGetRequest;
import com.zipkimi.user.dto.request.SmsAuthNumberPostRequest;
import com.zipkimi.user.dto.response.SmsAuthNumberGetResponse;
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

    // ************* 시공사 회원 가입 *************
    @Test
    @DisplayName(value = "시공사 회원 가입 성공 테스트")
    void builderUserJoinSuccessTest() throws Exception {

        //given
        JoinBuilderUserPostRequest requestDto = new JoinBuilderUserPostRequest("testBuilder@gmail.com", "test1234!@", "testBuilder",
                1L);
        JoinBuilderUserPostResponse response = JoinBuilderUserPostResponse.builder().message("시공사 회원가입 완료").build();

        when(builderManagementService.joinBuilderUser(any(JoinBuilderUserPostRequest.class))).thenReturn(response);

        String requestBody = objectMapper.writeValueAsString(requestDto);

        //when
        mockMvc.perform(post("/api/v1/builderMgmt/builders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                //then
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(jsonPath("$.message").value("시공사 회원가입 완료"));


    }

    @Test
    @DisplayName(value = "시공사 회원 가입 실패 테스트")
    void builderUserJoinFailureTest() throws Exception {

        //given
        JoinBuilderUserPostRequest requestDto = new JoinBuilderUserPostRequest("testBuilder@gmail.com", "password123!", "testBuilder",
                1L);
        JoinBuilderUserPostResponse response = JoinBuilderUserPostResponse.builder().message("이미 사용 중인 이메일입니다.").build();

        when(builderManagementService.joinBuilderUser(any(JoinBuilderUserPostRequest.class))).thenReturn(response);

        String requestBody = objectMapper.writeValueAsString(requestDto);

        //when
        mockMvc.perform(post("/api/v1/builderMgmt/builders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                //then
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(jsonPath("$.message").value("이미 사용 중인 이메일입니다."));


    }

    // ************* 시공사 회원 가입 - SMS 인증번호 전송 *************

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

    // ************* 시공사 회원 가입 - SMS 인증번호 확인 *************

    @Test
    @DisplayName(value = "시공사 회원 가입 - SMS 인증번호 확인 성공 테스트")
    void checkBuilderUserJoinSmsAuthNumberSuccessTest() throws Exception {

        //given
        SmsAuthNumberGetRequest requestDto = new SmsAuthNumberGetRequest(1, "01094342762", "1234");
        SmsAuthNumberGetResponse response = SmsAuthNumberGetResponse.builder().message("본인 인증에 성공했습니다.").build();

        when(builderManagementService.checkBuilderUserJoinSmsAuthNumber(
                any(SmsAuthNumberGetRequest.class))).thenReturn(response);

        String requestBody = objectMapper.writeValueAsString(requestDto);

        //when
        mockMvc.perform(get("/api/v1/builderMgmt/builders/sms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                //then
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.message").value("본인 인증에 성공했습니다."));

    }

    @Test
    @DisplayName(value = "시공사 회원 가입 - SMS 인증번호 확인 실패 테스트")
    void checkBuilderUserJoinSmsAuthNumberFailureTest() throws Exception {

        //given
        SmsAuthNumberGetRequest requestDto = new SmsAuthNumberGetRequest(1, "01094342762", "1234");
        SmsAuthNumberGetResponse response = SmsAuthNumberGetResponse.builder().message("SMS 인증에 실패하였습니다. \n인증번호를 정상적으로 입력해주세요.").build();

        when(builderManagementService.checkBuilderUserJoinSmsAuthNumber(
                any(SmsAuthNumberGetRequest.class))).thenReturn(response);

        String requestBody = objectMapper.writeValueAsString(requestDto);

        //when
        mockMvc.perform(get("/api/v1/builderMgmt/builders/sms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                //then
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.message").value("SMS 인증에 실패하였습니다. \n인증번호를 정상적으로 입력해주세요."));

    }

}
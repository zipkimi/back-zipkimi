package com.zipkimi.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zipkimi.global.dto.response.BaseResponse;
import com.zipkimi.global.jwt.dto.request.TokenRequest;
import com.zipkimi.global.jwt.dto.response.TokenResponse;
import com.zipkimi.user.dto.request.FindIdCheckSmsGetRequest;
import com.zipkimi.user.dto.request.FindPwCheckSmsGetRequest;
import com.zipkimi.user.dto.request.PassResetSmsAuthNumberPostRequest;
import com.zipkimi.user.dto.request.SmsAuthNumberPostRequest;
import com.zipkimi.user.dto.request.UserLoginRequest;
import com.zipkimi.user.dto.response.FindSmsAuthNumberGetResponse;
import com.zipkimi.user.dto.response.FindSmsAuthNumberPostResponse;
import com.zipkimi.user.service.UserLoginService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class UserLoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserLoginService userLoginService;
    //실제 Controller에 userLoginService 의존성이 주입되어 있기 때문에 @MockBean 가짜 객체 주입

    // ************* 로그인 테스트를 위한 간단 일반 회원 가입 *************

    @Test
    @DisplayName(value = "로그인 테스트를 위한 간단 일반 회원 가입 성공 테스트")
    @WithMockUser
        //시큐리티 인증된 사용자 (302 에러 방지)
    void signSuccessTest() throws Exception {

        //given
        // 테스트에 사용할 UserLoginRequest 객체 생성
        UserLoginRequest requestDto = new UserLoginRequest();
        requestDto.setEmail("test@gmail.com");
        requestDto.setPassword("testPassword!@");

        BaseResponse successResponse = BaseResponse.builder().message("일반 회원 가입 테스트에 성공했습니다.")
                .build();
        when(userLoginService.simpleJoinTest(any(UserLoginRequest.class))).thenReturn(
                successResponse);

        String requestBody = objectMapper.writeValueAsString(requestDto);

        //when
        mockMvc.perform(post("/api/v1/users/auth/sign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // 403 에러 방지
                        .content(requestBody))
                .andDo(print())
                //then
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.message").value("일반 회원 가입 테스트에 성공했습니다."));
    }

    @Test
    @DisplayName(value = "로그인 테스트를 위한 간단 일반 회원 가입 실패 테스트")
    @WithMockUser
        //시큐리티 인증된 사용자 (302 에러 방지)
    void signFailureTest() throws Exception {

        //given
        // 테스트에 사용할 UserLoginRequest 객체 생성
        UserLoginRequest requestDto = new UserLoginRequest();
        requestDto.setEmail("test@gmail.com");
        requestDto.setPassword("wrong_password");

        BaseResponse failureResponse = BaseResponse.builder().message("회원 가입에 실패하였습니다.").build();
        when(userLoginService.simpleJoinTest(any(UserLoginRequest.class))).thenReturn(
                failureResponse);

        String requestBody = objectMapper.writeValueAsString(requestDto);

        //when
        mockMvc.perform(post("/api/v1/users/auth/sign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // 403 에러 방지
                        .content(requestBody))
                .andDo(print())
                //then
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.message").value("회원 가입에 실패하였습니다."));

    }

    // ************* 로그인 *************

    @Test
    @DisplayName(value = "로그인 성공 테스트")
    @WithMockUser
        //시큐리티 인증된 사용자 (302 에러 방지)
    void loginSuccessTest() throws Exception {

        //given
        // 테스트에 사용할 UserLoginRequest 객체 생성
        UserLoginRequest requestDto = new UserLoginRequest();
        requestDto.setEmail("test@gmail.com");
        requestDto.setPassword("testPassword!@");

        TokenResponse successResponse = TokenResponse.builder().message("로그인에 성공하였습니다.").build();
        when(userLoginService.login(any(UserLoginRequest.class))).thenReturn(successResponse);

        String requestBody = objectMapper.writeValueAsString(requestDto);

        //when
        mockMvc.perform(post("/api/v1/users/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // 403 에러 방지
                        .content(requestBody))
                .andDo(print())
                //then
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.message").value("로그인에 성공하였습니다."));

    }

    @Test
    @DisplayName(value = "로그인 실패 테스트")
    @WithMockUser
        //시큐리티 인증된 사용자 (302 에러 방지)
    void loginFailureTest() throws Exception {

        //given
        // 테스트에 사용할 UserLoginRequest 객체 생성
        UserLoginRequest requestDto = new UserLoginRequest();
        requestDto.setEmail("test@gmail.com");
        requestDto.setPassword("wrong_password");

        TokenResponse response = TokenResponse.builder().message("가입하지 않은 이메일이거나 잘못된 비밀번호입니다.")
                .build();
        when(userLoginService.login(any(UserLoginRequest.class))).thenReturn(response);

        String requestBody = objectMapper.writeValueAsString(requestDto);

        //when
        mockMvc.perform(post("/api/v1/users/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // 403 에러 방지
                        .content(requestBody))
                .andDo(print())
                //then
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.message").value("가입하지 않은 이메일이거나 잘못된 비밀번호입니다."));

    }

    // ************* 로그아웃 *************

    @Test
    @DisplayName(value = "로그아웃 성공 테스트")
    @WithMockUser
        //시큐리티 인증된 사용자 (302 에러 방지)
    void logoutSuccessTest() throws Exception {

        TokenRequest requestDto = new TokenRequest();
        requestDto.setAccessToken("logout-test-access-token");
        requestDto.setRefreshToken("logout-test-refresh-token");

        TokenResponse response = TokenResponse.builder().message("로그아웃 되었습니다.").build();
        when(userLoginService.logout(requestDto.getRefreshToken())).thenReturn(response);

        String requestBody = objectMapper.writeValueAsString(requestDto);

        //when
        mockMvc.perform(delete("/api/v1/users/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // 403 에러 방지
                        .content(requestBody))
                .andDo(print())
                //then
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.message").value("로그아웃 되었습니다."));
    }

    @Test
    @DisplayName(value = "로그아웃 실패 테스트")
    @WithMockUser
        //시큐리티 인증된 사용자 (302 에러 방지)
    void logoutFailureTest() throws Exception {

        //given
        TokenRequest requestDto = new TokenRequest();
        requestDto.setAccessToken("invalid-access-token");
        requestDto.setRefreshToken("invalid-refresh-token");

        TokenResponse response = TokenResponse.builder().message("잘못된 JWT 서명입니다.").build();
        when(userLoginService.logout(requestDto.getRefreshToken())).thenReturn(response);

        String requestBody = objectMapper.writeValueAsString(requestDto);

        //when
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/users/auth/logout")
                        .header("Authorization", "Bearer " + requestDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // 403 에러 방지
                        .content(requestBody))
                //then
                .andExpect(MockMvcResultMatchers.status().isOk()); // HTTP 상태 코드 401 예상
    }

    // ************* 아이디 찾기 *************

    @Test
    @DisplayName(value = "아이디 찾기 - SMS 인증번호 전송 성공 테스트")
    void sendFindIdSmsAuthNumberSuccessTest() throws Exception {

        //given
        SmsAuthNumberPostRequest requestDto = new SmsAuthNumberPostRequest("010-9434-2762");
        FindSmsAuthNumberPostResponse response = FindSmsAuthNumberPostResponse.builder()
                .message("인증번호를 전송하였습니다.").build();

        when(userLoginService.sendFindIdSmsAuthNumber(
                any(SmsAuthNumberPostRequest.class))).thenReturn(response);

        String requestBody = objectMapper.writeValueAsString(requestDto);

        //when
        mockMvc.perform(post("/api/v1/users/find-id/sms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                //then
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.message").value("인증번호를 전송하였습니다."));
    }

    @Test
    @DisplayName(value = "아이디 찾기 - SMS 인증번호 전송 실패 테스트")
    void sendFindIdSmsAuthNumberFailureTest() throws Exception {

        //given
        SmsAuthNumberPostRequest requestDto = new SmsAuthNumberPostRequest("010-1234-5678");
        FindSmsAuthNumberPostResponse response = FindSmsAuthNumberPostResponse.builder()
                .message("입력하신 휴대폰 번호와 일치하는 아이디 정보가 없습니다. \n(고객센터 문의 요망)").build();

        when(userLoginService.sendFindIdSmsAuthNumber(any(SmsAuthNumberPostRequest.class)))
                .thenReturn(response);

        String requestBody = objectMapper.writeValueAsString(requestDto);

        //when
        mockMvc.perform(post("/api/v1/users/find-id/sms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                //then
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.message").value(
                        "입력하신 휴대폰 번호와 일치하는 아이디 정보가 없습니다. \n(고객센터 문의 요망)"));
    }

    @Test
    @DisplayName(value = "아이디 찾기 - SMS 인증번호 확인 및 아이디 찾기 성공 테스트")
    void checkFindIdSmsAuthSuccessTest() throws Exception {

        //given
        FindIdCheckSmsGetRequest requestDto = new FindIdCheckSmsGetRequest(1, "010-1234-5678",
                "1234");
        FindSmsAuthNumberGetResponse response = FindSmsAuthNumberGetResponse.builder()
                .message("고객님의 집킴이 계정을 찾았습니다. 아이디 확인 후 로그인 해주세요.").build();

        when(userLoginService.checkFindIdSmsAuth(any(FindIdCheckSmsGetRequest.class))).thenReturn(
                response);

        String requestBody = objectMapper.writeValueAsString(requestDto);

        //when
        mockMvc.perform(get("/api/v1/users/find-id/sms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                //then
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.message").value("고객님의 집킴이 계정을 찾았습니다. 아이디 확인 후 로그인 해주세요."));
    }

    @Test
    @DisplayName(value = "아이디 찾기 - SMS 인증번호 확인 및 아이디 찾기 실패 테스트")
    void checkFindIdSmsAuthFailureTest() throws Exception {

        //given
        FindIdCheckSmsGetRequest requestDto = new FindIdCheckSmsGetRequest(1, "010-1234-5678",
                "1234");
        FindSmsAuthNumberGetResponse response = FindSmsAuthNumberGetResponse.builder()
                .message("입력하신 휴대폰 번호와 일치하는 아이디 정보가 없습니다. \n(고객센터 문의 요망)").build();

        when(userLoginService.checkFindIdSmsAuth(any(FindIdCheckSmsGetRequest.class))).thenReturn(
                response);

        String requestBody = objectMapper.writeValueAsString(requestDto);

        //when
        mockMvc.perform(get("/api/v1/users/find-id/sms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                //then
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.message").value(
                        "입력하신 휴대폰 번호와 일치하는 아이디 정보가 없습니다. \n(고객센터 문의 요망)"));
    }

    // ************* 비밀번호 찾기 *************

    @Test
    @DisplayName(value = "비밀번호 찾기 - SMS 인증번호 전송 성공 테스트")
    void sendFindPwSmsAuthNumberSuccessTest() throws Exception {

        //given
        PassResetSmsAuthNumberPostRequest requestDto = new PassResetSmsAuthNumberPostRequest(
                "010-9434-2762", "test@gmailc.om");
        FindSmsAuthNumberPostResponse response = FindSmsAuthNumberPostResponse.builder()
                .message("인증번호를 전송하였습니다.").build();

        when(userLoginService.sendFindPwSmsAuthNumber(
                any(PassResetSmsAuthNumberPostRequest.class))).thenReturn(response);

        String requestBody = objectMapper.writeValueAsString(requestDto);

        //when
        mockMvc.perform(post("/api/v1/users/find-pw/sms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                //then
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.message").value("인증번호를 전송하였습니다."));
    }

    @Test
    @DisplayName(value = "비밀번호 찾기 - SMS 인증번호 전송 실패 테스트")
    void sendFindPwSmsAuthNumberFailureTest() throws Exception {

        //given
        PassResetSmsAuthNumberPostRequest requestDto = new PassResetSmsAuthNumberPostRequest(
                "010-9434-2762", "test@gmailc.om");
        FindSmsAuthNumberPostResponse response = FindSmsAuthNumberPostResponse.builder()
                .message("입력하신 휴대폰 번호와 일치하는 정보가 없습니다. \n(고객센터 문의 요망)").build();

        when(userLoginService.sendFindPwSmsAuthNumber(
                any(PassResetSmsAuthNumberPostRequest.class))).thenReturn(response);

        String requestBody = objectMapper.writeValueAsString(requestDto);

        //when
        mockMvc.perform(post("/api/v1/users/find-pw/sms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                //then
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        jsonPath("$.message").value("입력하신 휴대폰 번호와 일치하는 정보가 없습니다. \n(고객센터 문의 요망)"));
    }

    @Test
    @DisplayName(value = "비밀번호 찾기 - SMS 인증번호 확인 및 비밀번호 초기화 성공 테스트")
    void checkFindPwSmsAuthAndResetSuccessTest() throws Exception {

        //given
        FindPwCheckSmsGetRequest requestDto = new FindPwCheckSmsGetRequest(1, "010-1234-5678",
                "1234", "test12@gmail.com");
        FindSmsAuthNumberGetResponse response = FindSmsAuthNumberGetResponse.builder()
                .message("고객님의 비밀번호가 초기화 되었습니다. \n비밀번호 확인 후 로그인해주세요.").build();

        when(userLoginService.checkFindPwSmsAuth(any(FindPwCheckSmsGetRequest.class))).thenReturn(response);

        String requestBody = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(get("/api/v1/users/find-pw/sms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.message").value("고객님의 비밀번호가 초기화 되었습니다. \n비밀번호 확인 후 로그인해주세요."));
    }

    @Test
    @DisplayName(value = "비밀번호 찾기 - SMS 인증번호 확인 및 비밀번호 초기화 실패 테스트")
    void checkFindPwSmsAuthAndResetFailureTest() throws Exception {

        //given
        FindPwCheckSmsGetRequest requestDto = new FindPwCheckSmsGetRequest(1, "010-1234-5678",
                "1234", "test12@gmail.com");
        FindSmsAuthNumberGetResponse response = FindSmsAuthNumberGetResponse.builder()
                .message("입력하신 휴대폰 번호와 이메일 정보가 일치하는 사용자가 없습니다. \n(고객센터 문의 요망)").build();

        when(userLoginService.checkFindPwSmsAuth(
                any(FindPwCheckSmsGetRequest.class))).thenReturn(response);

        String requestBody = objectMapper.writeValueAsString(requestDto);

        //when
        mockMvc.perform(get("/api/v1/users/find-pw/sms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                //then
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.message").value(
                        "입력하신 휴대폰 번호와 이메일 정보가 일치하는 사용자가 없습니다. \n(고객센터 문의 요망)"));
    }
}
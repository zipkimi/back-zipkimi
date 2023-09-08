package com.zipkimi.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zipkimi.global.dto.response.BaseResponse;
import com.zipkimi.global.jwt.dto.request.TokenRequest;
import com.zipkimi.global.jwt.dto.response.TokenResponse;
import com.zipkimi.user.dto.request.UserLoginRequest;
import com.zipkimi.user.service.UserLoginService;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class UserLoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean //실제 Controller에 의존성이 주입되어 있기 때문에 @MockBean으로 가짜 객체를 주입
    private UserLoginService userLoginService;

    @Autowired
    private WebApplicationContext wac;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    // ************* 로그인 테스트를 위한 간단 일반 회원 가입 *************

    @Test
    @DisplayName(value = "로그인 테스트를 위한 간단 일반 회원 가입 성공 테스트")
    @WithMockUser //시큐리티 인증된 사용자 (302 에러 방지)
    void signSuccessTest() throws Exception {

        //given
        // 테스트에 사용할 UserLoginRequest 객체 생성
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("test@gmail.com");
        request.setPassword("testPassword!@");

        BaseResponse successResponse = BaseResponse.builder().message("일반 회원 가입 테스트에 성공했습니다.").build();
        when(userLoginService.simpleJoinTest(any(UserLoginRequest.class))).thenReturn(successResponse);

        String requestBody = objectMapper.writeValueAsString(request);

        //when & then
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
    @WithMockUser //시큐리티 인증된 사용자 (302 에러 방지)
    void signFailureTest() throws Exception {

        //given
        // 테스트에 사용할 UserLoginRequest 객체 생성
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("test@gmail.com");
        request.setPassword("wrong_password");

        BaseResponse failureResponse = BaseResponse.builder().message("회원 가입에 실패하였습니다.").build();
        when(userLoginService.simpleJoinTest(any(UserLoginRequest.class))).thenReturn(failureResponse);

        String requestBody = objectMapper.writeValueAsString(request);

        //when & then
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
    @WithMockUser //시큐리티 인증된 사용자 (302 에러 방지)
    void loginSuccessTest() throws Exception {

        //given
        // 테스트에 사용할 UserLoginRequest 객체 생성
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("test@gmail.com");
        request.setPassword("testPassword!@");

        TokenResponse successResponse = TokenResponse.builder().message("로그인에 성공하였습니다.").build();
        when(userLoginService.login(any(UserLoginRequest.class))).thenReturn(successResponse);

        String requestBody = objectMapper.writeValueAsString(request);

        //when & then
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
    @WithMockUser //시큐리티 인증된 사용자 (302 에러 방지)
    void loginFailureTest() throws Exception {

        //given
        // 테스트에 사용할 UserLoginRequest 객체 생성
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("test@gmail.com");
        request.setPassword("wrong_password");

        TokenResponse response = TokenResponse.builder().message("가입하지 않은 이메일이거나 잘못된 비밀번호입니다.").build();
        when(userLoginService.login(any(UserLoginRequest.class))).thenReturn(response);

        String requestBody = objectMapper.writeValueAsString(request);

        //when & then
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
    @WithMockUser //시큐리티 인증된 사용자 (302 에러 방지)
    void logoutSuccessTest() throws Exception {

        TokenRequest request = new TokenRequest();
        request.setAccessToken("logout-test-access-token");
        request.setRefreshToken("logout-test-refresh-token");

        TokenResponse response = TokenResponse.builder().message("로그아웃 되었습니다.").build();
        when(userLoginService.logout(request.getRefreshToken())).thenReturn(response);

        String requestBody = objectMapper.writeValueAsString(request);

        //when & then
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
    @WithMockUser //시큐리티 인증된 사용자 (302 에러 방지)
    void logoutFailureTest() throws Exception {

        TokenRequest request = new TokenRequest();
        request.setAccessToken("invalid-access-token");
        request.setRefreshToken("invalid-refresh-token");

        TokenResponse response = TokenResponse.builder().message("잘못된 JWT 서명입니다.").build();
        when(userLoginService.logout(request.getRefreshToken())).thenReturn(response);

        String requestBody = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/users/auth/logout")
                        .header("Authorization", "Bearer " + request.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // 403 에러 방지
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized()); // HTTP 상태 코드 401 예상
    }

}

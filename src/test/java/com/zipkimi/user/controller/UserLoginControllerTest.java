package com.zipkimi.user.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zipkimi.entity.SmsAuthEntity;
import com.zipkimi.entity.UserEntity;
import com.zipkimi.entity.UserRole;
import com.zipkimi.global.dto.response.BaseResponse;
import com.zipkimi.global.jwt.dto.request.TokenRequest;
import com.zipkimi.global.jwt.dto.response.TokenResponse;
import com.zipkimi.repository.SmsAuthRepository;
import com.zipkimi.repository.UserRepository;
import com.zipkimi.user.dto.request.FindIdCheckSmsGetRequest;
import com.zipkimi.user.dto.request.FindPwCheckSmsGetRequest;
import com.zipkimi.user.dto.request.PassResetSmsAuthNumberPostRequest;
import com.zipkimi.user.dto.request.SmsAuthNumberPostRequest;
import com.zipkimi.user.dto.request.UserLoginRequest;
import com.zipkimi.user.dto.response.FindSmsAuthNumberGetResponse;
import com.zipkimi.user.dto.response.FindSmsAuthNumberPostResponse;
import com.zipkimi.user.service.UserLoginService;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class UserLoginControllerTest {

    @Mock
    private UserLoginService userLoginService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SmsAuthRepository smsAuthRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private UserLoginController userLoginController;

    private static final String CHAR_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom secureRandom = new SecureRandom();

    // ************* 로그인 테스트를 위한 간단 일반 회원 가입 *************

    @Test
    @DisplayName(value = "로그인 테스트를 위한 간단 일반 회원 가입 성공 테스트")
    void singSuccessTest() throws Exception {

        //given
        // User 객체 생성 & 비밀번호 암호화 적용
        UserEntity user = new UserEntity();
        String rawPw = "test1234!";
        String encodePw = passwordEncoder.encode(rawPw);
        user.setEmail("test@gmail.com");
        user.setPassword(encodePw);
        user.setRole(UserRole.ROLE_USER);
        user.setPhoneNumber("01094342762");

        UserLoginRequest requestDto = new UserLoginRequest(user.getEmail(), user.getPassword());

        when(userLoginService.simpleJoinTest(requestDto)).thenReturn(
                BaseResponse.builder()
                        .message("일반 회원 가입 테스트에 성공했습니다.")
                        .build()
        );

        //when
        ResponseEntity<BaseResponse> response = userLoginController.sign(requestDto);

        log.info("******************");
        log.info("singSuccessTest response.getBody().getResult() ={}", response.getBody().getMessage());
        log.info("******************");

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("일반 회원 가입 테스트에 성공했습니다.", response.getBody().getMessage());

    }

    @Test
    @DisplayName(value = "로그인 테스트를 위한 간단 일반 회원 가입 실패 테스트")
    void signFailureTest() throws Exception{

        //given
        UserEntity user = new UserEntity();
        String rawPw = "test1234!";
        String encodePw = passwordEncoder.encode(rawPw);
        user.setEmail("test@gmail.com");
        user.setPassword(encodePw);
        user.setRole(UserRole.ROLE_USER);
        user.setPhoneNumber("01094342762");

        //when
        UserLoginRequest requestDto = new UserLoginRequest(user.getEmail(), user.getPassword());

        when(userLoginService.simpleJoinTest(requestDto)).thenReturn(
                BaseResponse.builder()
                        .message("이미 가입한 회원입니다.")
                        .build()
        );

        ResponseEntity<BaseResponse> response = userLoginController.sign(requestDto);

        log.info("******************");
        log.info("signFailureTest response.getBody().getResult() ={}", response.getBody().getMessage());
        log.info("******************");

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("이미 가입한 회원입니다.", response.getBody().getMessage());


    }

    // ************* 로그인 *************

    @Test
    @DisplayName(value = "로그인 성공 테스트")
    void loginSuccessTest() throws Exception{

        //given
        // 비밀번호 암호화 적용
        String rawPw = "password123!";
        String encodePw = passwordEncoder.encode(rawPw);

        UserLoginRequest requestDto = new UserLoginRequest("test@example.com", encodePw);

        when(userLoginService.login(requestDto)).thenReturn(
                TokenResponse.builder()
                        .message("로그인에 성공하였습니다.")
                        .build()
        );

        //when
        ResponseEntity<TokenResponse> response = userLoginController.login(requestDto);

        log.info("******************");
        log.info("loginSuccessTest response.getBody().getResult() ={}", response.getBody().getMessage());
        log.info("******************");

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("로그인에 성공하였습니다.", response.getBody().getMessage());
    }

    @Test
    @DisplayName(value = "로그인 실패 테스트")
    void loginFailureTest() throws Exception{

        //given
        // 비밀번호 암호화 적용
        String rawPw = "password123!";
        String encodePw = passwordEncoder.encode(rawPw);

        UserLoginRequest requestDto = new UserLoginRequest("test@example.com", encodePw);

        when(userLoginService.login(requestDto)).thenReturn(
                TokenResponse.builder()
                        .message("가입하지 않은 이메일이거나 잘못된 비밀번호입니다.")
                        .build()
        );

        //when
        ResponseEntity<TokenResponse> response = userLoginController.login(requestDto);

        log.info("******************");
        log.info("loginFailureTest response.getBody().getResult() ={}", response.getBody().getMessage());
        log.info("******************");

        //then
        //TODO HTTP 상태 코드 - 401 - Unauthorized
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("가입하지 않은 이메일이거나 잘못된 비밀번호입니다.", response.getBody().getMessage());

    }

    // ************* 토큰 재발급 *************
    @Test
    @DisplayName(value = "토근 재발급 성공 테스트")
    void reissueSuccessTest() throws Exception{

        // given
        TokenRequest requestDto = new TokenRequest();
        requestDto.setAccessToken("valid-access-token");
        requestDto.setRefreshToken("valid-refresh-token");

        when(userLoginService.reissue(requestDto)).thenReturn(
                TokenResponse.builder()
                        .message("토큰 재발급에 성공하였습니다.")
                        .build()
        );

        // when
        ResponseEntity<TokenResponse> response = userLoginController.reissue(requestDto);

        log.info("******************");
        log.info("reissueSuccessTest response.getBody().getResult() ={}", response.getBody().getMessage());
        log.info("******************");

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("토큰 재발급에 성공하였습니다.", response.getBody().getMessage());

    }

    @Test
    @DisplayName(value = "토큰 재발급 실패 테스트")
    void reissueFailureTest(){

            //given
            TokenRequest requestDto = new TokenRequest();
            requestDto.setAccessToken("invalid-access-token");
            requestDto.setRefreshToken("invalid-refresh-token");

            when(userLoginService.reissue(requestDto)).thenReturn(
                    TokenResponse.builder()
                            .message("토큰의 유저 정보가 일치하지 않습니다.")
                            .build()
            );

           //when
            ResponseEntity<TokenResponse> response = userLoginController.reissue(requestDto);

            log.info("******************");
            log.info("reissueFailureTest response.getBody().getResult() ={}", response.getBody().getMessage());
            log.info("******************");

            //then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("토큰의 유저 정보가 일치하지 않습니다.", response.getBody().getMessage());


    }

    // ************* 로그아웃 *************
    @Test
    @DisplayName(value = "로그아웃 성공 테스트")
    void logoutSuccessTest() throws Exception {

        //given
        TokenRequest requestDto = new TokenRequest();
        requestDto.setRefreshToken("valid-refresh-token");

        when(userLoginService.logout(requestDto.getRefreshToken())).thenReturn(
                TokenResponse.builder()
                        .message("로그아웃 되었습니다.")
                        .build()
        );

        //when
        ResponseEntity<TokenResponse> response = userLoginController.logout(requestDto);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("로그아웃 되었습니다.", response.getBody().getMessage());
    }

    // ************* 아이디 찾기 *************

    @Test
    @DisplayName(value = "아이디 찾기 - SMS 인증번호 전송 성공 테스트")
    void sendFindIdSmsAuthNumberSuccessTest() throws Exception {

        //given
        UserEntity user = new UserEntity();
        user.setPhoneNumber("01094342762");
        user.setEmail("test@gmail.com");
        userRepository.save(user);

        SmsAuthNumberPostRequest requestDto = new SmsAuthNumberPostRequest(user.getPhoneNumber());

        when(userLoginService.sendFindIdSmsAuthNumber(requestDto)).thenReturn(
                FindSmsAuthNumberPostResponse
                        .builder()
                        .message("인증번호를 전송하였습니다.")
                        .build()
        );

        //when
        ResponseEntity<FindSmsAuthNumberPostResponse> response = userLoginController.sendFindIdSmsAuthNumber(requestDto);

        log.info("******************");
        log.info("sendFindIdSmsAuthNumberSuccessTest response.getBody().getResult() ={}", response.getBody().getMessage());
        log.info("******************");

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("인증번호를 전송하였습니다.", response.getBody().getMessage());

    }

    @Test
    @DisplayName(value = "아이디 찾기 - SMS 인증번호 전송 실패 테스트")
    void sendFindIdSmsAuthNumberFailureTest() {

        //given
        UserEntity user = new UserEntity();
        user.setPhoneNumber("01094342762");
        user.setEmail("test@gmail.com");
        userRepository.save(user);

        SmsAuthNumberPostRequest requestDto = new SmsAuthNumberPostRequest("01012341234");

        when(userLoginService.sendFindIdSmsAuthNumber(requestDto)).thenReturn(
                FindSmsAuthNumberPostResponse
                        .builder()
                        .message("입력하신 휴대폰 번호와 일치하는 정보가 없습니다. \n(고객센터 문의 요망)")
                        .build()
        );

        //when
        ResponseEntity<FindSmsAuthNumberPostResponse> response = userLoginController.sendFindIdSmsAuthNumber(requestDto);

        log.info("******************");
        log.info("sendFindIdSmsAuthNumberFailureTest response.getBody().getResult() ={}", response.getBody().getMessage());
        log.info("******************");

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("입력하신 휴대폰 번호와 일치하는 정보가 없습니다. \n(고객센터 문의 요망)", response.getBody().getMessage());

    }

    @Test
    @DisplayName(value = "아이디 찾기 - SMS 인증번호 확인 및 아이디 찾기 성공 테스트")
    void checkFindIdSmsAuthSuccessTest() {

        // given
        UserEntity user = new UserEntity();
        user.setPhoneNumber("01094342762");
        user.setEmail("test@gmail.com");
        userRepository.save(user);

        SmsAuthEntity smsAuth = new SmsAuthEntity();
        smsAuth.setSmsAuthNumber("1234");
        smsAuth.setExpirationTime(LocalDateTime.now().plusMinutes(30));
        smsAuthRepository.save(smsAuth);

        FindIdCheckSmsGetRequest requestDto = new FindIdCheckSmsGetRequest(1L, user.getPhoneNumber(), "1234");

        when(userLoginService.checkFindIdSmsAuth(requestDto)).thenReturn(
                FindSmsAuthNumberGetResponse
                        .builder()
                        .message("회원님의 아이디는 'test@gmail.com' 입니다.")
                        .build());

        when(userRepository.findByPhoneNumber("01094342762")).thenReturn(Optional.of(user));
        when(smsAuthRepository.findById(1L)).thenReturn(Optional.of(smsAuth));

        // when
        ResponseEntity<FindSmsAuthNumberGetResponse> response = userLoginController.checkFindIdSmsAuth(requestDto);

        log.info("******************");
        log.info("checkFindIdSmsAuthSuccessTest response.getBody().getResult() ={}", response.getBody().getMessage());
        log.info("******************");

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("회원님의 아이디는 'test@gmail.com' 입니다.", response.getBody().getMessage());
    }

    @Test
    @DisplayName(value = "아이디 찾기 - SMS 인증번호 확인 및 아이디 찾기 실패 테스트")
    void checkFindIdSmsAuthFailureTest() {

        // given
        FindIdCheckSmsGetRequest requestDto = new FindIdCheckSmsGetRequest(1L, "01094342762", "1234");

        SmsAuthEntity smsAuth = new SmsAuthEntity();
        smsAuth.setSmsAuthNumber("5678");
        smsAuth.setExpirationTime(LocalDateTime.now().minusMinutes(30));
        smsAuthRepository.save(smsAuth);

        when(userLoginService.checkFindIdSmsAuth(requestDto)).thenReturn(
                FindSmsAuthNumberGetResponse
                        .builder()
                        .message("입력하신 휴대폰 번호와 일치하는 아이디 정보가 없습니다. \n(고객센터 문의 요망)")
                        .build());
        when(smsAuthRepository.findById(1L)).thenReturn(Optional.of(smsAuth));

        // when
        ResponseEntity<FindSmsAuthNumberGetResponse> response = userLoginController.checkFindIdSmsAuth(requestDto);

        log.info("******************");
        log.info("checkFindIdSmsAuthFailureTest response.getBody().getResult() ={}", response.getBody().getMessage());
        log.info("******************");

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("입력하신 휴대폰 번호와 일치하는 아이디 정보가 없습니다. \n(고객센터 문의 요망)", response.getBody().getMessage());
    }

    // ************* 비밀번호 찾기 *************

    @Test
    @DisplayName(value = "비밀번호 찾기 - SMS 인증번호 전송 성공 테스트")
    void sendFindPwSmsAuthNumberSuccessTest(){

        //given
        UserEntity user = new UserEntity();
        user.setPhoneNumber("01094342762");
        user.setEmail("test@gmail.com");
        userRepository.save(user);

        PassResetSmsAuthNumberPostRequest requestDto = new PassResetSmsAuthNumberPostRequest(user.getPhoneNumber(), user.getEmail());

        log.info("******************");
        log.info("requestDto.getPhoneNumber() ={}", requestDto.getPhoneNumber());
        log.info("requestDto.getEmail() ={}", requestDto.getEmail());
        log.info("******************");

        when(userLoginService.sendFindPwSmsAuthNumber(requestDto)).thenReturn(
                FindSmsAuthNumberPostResponse
                        .builder()
                        .message("인증번호를 전송하였습니다.")
                        .build()
        );

        //when
        ResponseEntity<FindSmsAuthNumberPostResponse> response = userLoginController.sendFindPwSmsAuthNumber(requestDto);

        log.info("******************");
        log.info("sendFindPwSmsAuthNumberSuccessTest response.getBody().getResult() ={}", response.getBody().getMessage());
        log.info("******************");

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("인증번호를 전송하였습니다.", response.getBody().getMessage());


    }

    @Test
    @DisplayName(value = "비밀번호 찾기 - SMS 인증번호 전송 실패 테스트")
    void sendFindPwSmsAuthNumberFailureTest(){

        //given
        UserEntity user = new UserEntity();
        user.setPhoneNumber("01094342762");
        user.setEmail("test@gmail.com");
        userRepository.save(user);

        PassResetSmsAuthNumberPostRequest requestDto = new PassResetSmsAuthNumberPostRequest("01012341234", "dkdkdk@gmail.com");

        log.info("******************");
        log.info("requestDto.getPhoneNumber() ={}", requestDto.getPhoneNumber());
        log.info("requestDto.getEmail() ={}", requestDto.getEmail());
        log.info("******************");

        when(userLoginService.sendFindPwSmsAuthNumber(requestDto)).thenReturn(
                FindSmsAuthNumberPostResponse
                        .builder()
                        .message("입력하신 휴대폰 번호와 일치하는 정보가 없습니다. \n(고객센터 문의 요망)")
                        .build()
        );

        //when
        ResponseEntity<FindSmsAuthNumberPostResponse> response = userLoginController.sendFindPwSmsAuthNumber(requestDto);

        log.info("******************");
        log.info("sendFindPwSmsAuthNumberFailureTest response.getBody().getResult() ={}", response.getBody().getMessage());
        log.info("******************");

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("입력하신 휴대폰 번호와 일치하는 정보가 없습니다. \n(고객센터 문의 요망)", response.getBody().getMessage());

    }

    @Test
    @DisplayName(value = "비밀번호 찾기 - SMS 인증번호 확인 및 비밀번호 초기화 성공 테스트")
    void checkFindPwSmsAuthAndResetSuccessTest() {

        // given
        UserEntity user = new UserEntity();
        user.setPhoneNumber("01094342762");
        user.setEmail("test@gmail.com");
        userRepository.save(user);

        SmsAuthEntity smsAuth = new SmsAuthEntity();
        smsAuth.setSmsAuthNumber("1234");
        smsAuth.setExpirationTime(LocalDateTime.now().plusMinutes(30));
        smsAuthRepository.save(smsAuth);

        FindPwCheckSmsGetRequest requestDto = new FindPwCheckSmsGetRequest(1L, user.getPhoneNumber(), "1234", user.getEmail());

        String newPassword = tempPassword(10);

        log.info("******************");
        log.info("newPassword = {}", newPassword);
        log.info("******************");

        when(userLoginService.checkFindPwSmsAuth(requestDto)).thenReturn(
                FindSmsAuthNumberGetResponse
                        .builder()
                        .message("비밀번호가 '" + newPassword + "'로 초기화 되었습니다.")
                        .build());

        when(userRepository.findByPhoneNumber("01094342762")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));
        when(smsAuthRepository.findById(1L)).thenReturn(Optional.of(smsAuth));

        // when
        ResponseEntity<FindSmsAuthNumberGetResponse> response = userLoginController.checkFindPwSmsAuthAndReset(requestDto);

        log.info("******************");
        log.info("checkFindPwSmsAuthAndResetSuccessTest response.getBody().getResult() ={}", response.getBody().getMessage());
        log.info("******************");

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("비밀번호가 '" + newPassword + "'로 초기화 되었습니다.", response.getBody().getMessage());
    }

    @Test
    @DisplayName(value = "비밀번호 찾기 - SMS 인증번호 확인 및 비밀번호 초기화 실패 테스트")
    void checkFindPwSmsAuthAndResetFailureTest() {

        // given
        UserEntity user = new UserEntity();
        user.setPhoneNumber("01094342762");
        user.setEmail("test@gmail.com");
        userRepository.save(user);

        SmsAuthEntity smsAuth = new SmsAuthEntity();
        smsAuth.setSmsAuthNumber("5678");
        smsAuth.setExpirationTime(LocalDateTime.now().minusMinutes(30));
        smsAuthRepository.save(smsAuth);

        FindPwCheckSmsGetRequest requestDto = new FindPwCheckSmsGetRequest(1L, "01012543658", "1234", user.getEmail());

        when(userLoginService.checkFindPwSmsAuth(requestDto)).thenReturn(
                FindSmsAuthNumberGetResponse
                        .builder()
                        .message("입력하신 휴대폰 번호와 이메일 정보가 일치하는 사용자가 없습니다.")
                        .build());

        when(userRepository.findByPhoneNumber("01012543658")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));
        when(smsAuthRepository.findById(1L)).thenReturn(Optional.of(smsAuth));

        // when
        ResponseEntity<FindSmsAuthNumberGetResponse> response = userLoginController.checkFindPwSmsAuthAndReset(requestDto);

        log.info("******************");
        log.info("checkFindPwSmsAuthAndResetFailureTest response.getBody().getResult() ={}", response.getBody().getMessage());
        log.info("******************");

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("입력하신 휴대폰 번호와 이메일 정보가 일치하는 사용자가 없습니다.", response.getBody().getMessage());
    }

    //임시 비밀번호 생성
    public String tempPassword(int len) {
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < len; i++) {
            int index = secureRandom.nextInt(CHAR_SET.length());
            char randomChar = CHAR_SET.charAt(index);
            password.append(randomChar);
        }

        return password.toString();
    }

}

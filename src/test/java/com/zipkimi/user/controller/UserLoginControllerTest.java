package com.zipkimi.user.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.zipkimi.entity.SmsAuthEntity;
import com.zipkimi.entity.UserEntity;
import com.zipkimi.repository.SmsAuthRepository;
import com.zipkimi.repository.UserRepository;
import com.zipkimi.user.dto.request.FindIdCheckSmsGetRequest;
import com.zipkimi.user.dto.request.FindPwCheckSmsGetRequest;
import com.zipkimi.user.dto.request.PassResetSmsAuthNumberPostRequest;
import com.zipkimi.user.dto.request.SmsAuthNumberPostRequest;
import com.zipkimi.user.dto.response.FindSmsAuthNumberGetResponse;
import com.zipkimi.user.dto.response.FindSmsAuthNumberPostResponse;
import com.zipkimi.user.service.UserLoginService;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import javax.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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

    @InjectMocks
    private UserLoginController userLoginController;

    private static final String CHAR_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom secureRandom = new SecureRandom();

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
                        .result("인증번호를 전송하였습니다.")
                        .build()
        );

        //when
        ResponseEntity<FindSmsAuthNumberPostResponse> response = userLoginController.sendFindIdSmsAuthNumber(requestDto);
        System.out.println("sendFindIdSmsAuthNumberFailureTest response.getBody().getResult() = " + response.getBody().getResult());

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("인증번호를 전송하였습니다.", response.getBody().getResult());

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
                        .result("입력하신 휴대폰 번호와 일치하는 정보가 없습니다. \n(고객센터 문의 요망)")
                        .build()
        );

        //when
        ResponseEntity<FindSmsAuthNumberPostResponse> response = userLoginController.sendFindIdSmsAuthNumber(requestDto);
        System.out.println("sendFindIdSmsAuthNumberFailureTest response.getBody().getResult() = " + response.getBody().getResult());

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("입력하신 휴대폰 번호와 일치하는 정보가 없습니다. \n(고객센터 문의 요망)", response.getBody().getResult());

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
                        .result("회원님의 아이디는 'test@gmail.com' 입니다.")
                        .build());

        when(userRepository.findByPhoneNumber("01094342762")).thenReturn(Optional.of(user));
        when(smsAuthRepository.findById(1L)).thenReturn(Optional.of(smsAuth));

        // when
        ResponseEntity<FindSmsAuthNumberGetResponse> response = userLoginController.checkFindIdSmsAuth(requestDto);
        System.out.println("checkFindIdSmsAuthSuccessTest response.getBody().getResult() = " + response.getBody().getResult());

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("회원님의 아이디는 'test@gmail.com' 입니다.", response.getBody().getResult());
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
                        .result("입력하신 휴대폰 번호와 일치하는 아이디 정보가 없습니다. \n(고객센터 문의 요망)")
                        .build());
        when(smsAuthRepository.findById(1L)).thenReturn(Optional.of(smsAuth));

        // when
        ResponseEntity<FindSmsAuthNumberGetResponse> response = userLoginController.checkFindIdSmsAuth(requestDto);
        System.out.println("checkFindIdSmsAuthFailureTest response.getBody().getResult() = " + response.getBody().getResult());

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("입력하신 휴대폰 번호와 일치하는 아이디 정보가 없습니다. \n(고객센터 문의 요망)", response.getBody().getResult());
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
        System.out.println("requestDto.getPhoneNumber() = " + requestDto.getPhoneNumber());
        System.out.println("requestDto.getEmail() = " + requestDto.getEmail());

        when(userLoginService.sendFindPwSmsAuthNumber(requestDto)).thenReturn(
                FindSmsAuthNumberPostResponse
                        .builder()
                        .result("인증번호를 전송하였습니다.")
                        .build()
        );

        //when
        ResponseEntity<FindSmsAuthNumberPostResponse> response = userLoginController.sendFindPwSmsAuthNumber(requestDto);
        System.out.println("sendFindPwSmsAuthNumberSuccessTest response.getBody().getResult() = " + response.getBody().getResult());

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("인증번호를 전송하였습니다.", response.getBody().getResult());


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
        System.out.println("requestDto.getPhoneNumber() = " + requestDto.getPhoneNumber());
        System.out.println("requestDto.getEmail() = " + requestDto.getEmail());

        when(userLoginService.sendFindPwSmsAuthNumber(requestDto)).thenReturn(
                FindSmsAuthNumberPostResponse
                        .builder()
                        .result("입력하신 휴대폰 번호와 일치하는 정보가 없습니다. \n(고객센터 문의 요망)")
                        .build()
        );

        //when
        ResponseEntity<FindSmsAuthNumberPostResponse> response = userLoginController.sendFindPwSmsAuthNumber(requestDto);
        System.out.println("sendFindPwSmsAuthNumberSuccessTest response.getBody().getResult() = " + response.getBody().getResult());

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("입력하신 휴대폰 번호와 일치하는 정보가 없습니다. \n(고객센터 문의 요망)", response.getBody().getResult());

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
        System.out.println("newPassword = " + newPassword);

        when(userLoginService.checkFindPwSmsAuth(requestDto)).thenReturn(
                FindSmsAuthNumberGetResponse
                        .builder()
                        .result("비밀번호가 '" + newPassword + "'로 초기화 되었습니다.")
                        .build());

        when(userRepository.findByPhoneNumber("01094342762")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));
        when(smsAuthRepository.findById(1L)).thenReturn(Optional.of(smsAuth));

        // when
        ResponseEntity<FindSmsAuthNumberGetResponse> response = userLoginController.checkFindPwSmsAuthAndReset(requestDto);
        System.out.println("checkFindPwSmsAuthAndResetSuccessTest response.getBody().getResult() = " + response.getBody().getResult());

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("비밀번호가 '" + newPassword + "'로 초기화 되었습니다.", response.getBody().getResult());
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
                        .result("입력하신 휴대폰 번호와 이메일 정보가 일치하는 사용자가 없습니다.")
                        .build());

        when(userRepository.findByPhoneNumber("01012543658")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));
        when(smsAuthRepository.findById(1L)).thenReturn(Optional.of(smsAuth));

        // when
        ResponseEntity<FindSmsAuthNumberGetResponse> response = userLoginController.checkFindPwSmsAuthAndReset(requestDto);
        System.out.println("checkFindPwSmsAuthAndResetFailureTest response.getBody().getResult() = " + response.getBody().getResult());

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("입력하신 휴대폰 번호와 이메일 정보가 일치하는 사용자가 없습니다.", response.getBody().getResult());
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
package com.zipkimi.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.zipkimi.entity.SmsAuthEntity;
import com.zipkimi.entity.UserEntity;
import com.zipkimi.global.exception.BadRequestException;
import com.zipkimi.global.service.SmsService;
import com.zipkimi.repository.SmsAuthRepository;
import com.zipkimi.repository.UserRepository;
import com.zipkimi.user.dto.request.FindIdCheckSmsGetRequest;
import com.zipkimi.user.dto.request.SmsAuthNumberPostRequest;
import com.zipkimi.user.dto.response.FindSmsAuthNumberGetResponse;
import com.zipkimi.user.dto.response.FindSmsAuthNumberPostResponse;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
@Transactional
@AutoConfigureMockMvc
class UserLoginServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SmsAuthRepository smsAuthRepository;

    @Mock
    private SmsService smsService;

    @InjectMocks
    private UserLoginService userLoginService;

    private static final String CHAR_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom secureRandom = new SecureRandom();
    private final Random random = new Random();

    // ************* 아이디 찾기 *************

    @Test
    @DisplayName(value = "아이디 찾기 - SMS 인증번호 전송 성공 테스트")
    void sendFindIdSmsAuthNumberSuccessTest() throws Exception {

        // given
        UserEntity user = new UserEntity();
        user.setPhoneNumber("01094342762");
        user.setEmail("test@gmail.com");

        when(userRepository.findByPhoneNumber(any())).thenReturn(Optional.of(user));
        when(smsAuthRepository.findValidSmsAuthByPhoneNumberAndType(any(), any(), any())).thenReturn(null);

        SmsAuthNumberPostRequest requestDto = new SmsAuthNumberPostRequest(user.getPhoneNumber());

        // when
        FindSmsAuthNumberPostResponse response = userLoginService.sendFindIdSmsAuthNumber(requestDto);

        // then
        assertEquals("인증번호를 전송하였습니다.", response.getResult());
    }

    @Test
    @DisplayName("아이디 찾기 - SMS 인증번호 전송 실패 테스트 (휴대폰 번호로 가입된 회원 없음)")
    void sendFindIdSmsAuthNumberFailureTestUserNotFound() throws Exception {

        // given
        SmsAuthNumberPostRequest requestDto = new SmsAuthNumberPostRequest("01094342762");
        when(userRepository.findByPhoneNumber(requestDto.getPhoneNumber())).thenReturn(Optional.empty());

        // when
        FindSmsAuthNumberPostResponse response = userLoginService.sendFindIdSmsAuthNumber(requestDto);

        // then
        assertEquals("입력하신 휴대폰 번호와 일치하는 정보가 없습니다. \n(고객센터 문의 요망)", response.getResult());
    }

    @Test
    @DisplayName("아이디 찾기 - SMS 인증번호 전송 실패 테스트 (유효한 SMS 인증번호 이미 존재)")
    void sendFindIdSmsAuthNumberFailureTestExistingSmsAuth() throws Exception {

        // given
        SmsAuthNumberPostRequest requestDto = new SmsAuthNumberPostRequest("01094342762");

        when(userRepository.findByPhoneNumber(requestDto.getPhoneNumber())).thenReturn(Optional.of(new UserEntity()));
        when(smsAuthRepository.findValidSmsAuthByPhoneNumberAndType(any(), any(), any())).thenReturn(new SmsAuthEntity());

        // when
        FindSmsAuthNumberPostResponse response = userLoginService.sendFindIdSmsAuthNumber(requestDto);

        // then
        assertEquals("유효한 SMS 인증번호가 있습니다. 인증번호를 입력해주세요.", response.getResult());
    }

    @Test
    @DisplayName("아이디 찾기 - SMS 인증번호 전송 실패 테스트 (SMS 인증번호 생성 중 오류 발생)")
    void sendFindIdSmsAuthNumberFailureTestSmsAuthCreationError() throws Exception {

        // given
        SmsAuthNumberPostRequest requestDto = new SmsAuthNumberPostRequest("01094342762");

        when(userRepository.findByPhoneNumber(requestDto.getPhoneNumber())).thenReturn(Optional.of(new UserEntity()));
        when(smsAuthRepository.findValidSmsAuthByPhoneNumberAndType(any(), any(), any())).thenReturn(null);

        doThrow(new RuntimeException("Error saving SMS authentication")).when(smsAuthRepository).save(any());

        // when / then
        Exception exception = org.junit.jupiter.api.Assertions.assertThrows(BadRequestException.class, () -> {
            userLoginService.sendFindIdSmsAuthNumber(requestDto);
        });

        assertEquals("SMS 인증번호를 생성하던 중 오류가 발생하였습니다.", exception.getMessage());
    }

    @Test
    @DisplayName(value = "아이디 찾기 - SMS 인증번호 검증 & 휴대폰 번호로 가입된 회원 이메일 조회 성공 테스트")
    void checkFindIdSmsAuthSuccessTest() throws Exception {

        // given
        // SMS 인증
        SmsAuthEntity smsAuth = new SmsAuthEntity();
        smsAuth.setSmsAuthId(1L);
        smsAuth.setSmsAuthNumber("1234");
        smsAuth.setExpirationTime(LocalDateTime.now().plusMinutes(5));
        smsAuth.setIsUse(false);

        // 회원
        UserEntity user = new UserEntity();
        user.setPhoneNumber("01094342762");
        user.setEmail("test@gmail.com");
        userRepository.save(user);

        FindIdCheckSmsGetRequest requestDto = new FindIdCheckSmsGetRequest();
        requestDto.setSmsAuthId(1L);
        requestDto.setSmsAuthNumber("1234");
        requestDto.setPhoneNumber("01094342762");

        when(smsAuthRepository.findById(1L)).thenReturn(Optional.of(smsAuth));
        when(userRepository.findByPhoneNumber("01094342762")).thenReturn(Optional.of(user));

        // when
        FindSmsAuthNumberGetResponse response = userLoginService.checkFindIdSmsAuth(requestDto);

        // then
        assertEquals("회원님의 아이디는 'test@gmail.com' 입니다.", response.getResult());

    }

    @Test
    @DisplayName(value = "아이디 찾기 - SMS 인증번호 검증 - 실패 테스트 (인증번호 불일치)")
    void checkFindIdSmsAuthFailureTestInvalidAuthNumber() throws Exception {

        // given
        SmsAuthEntity smsAuth = new SmsAuthEntity();
        smsAuth.setSmsAuthId(1L);
        smsAuth.setSmsAuthNumber("1234");
        smsAuth.setExpirationTime(LocalDateTime.now().plusMinutes(5));
        smsAuth.setIsUse(false);

        FindIdCheckSmsGetRequest requestDto = new FindIdCheckSmsGetRequest();
        requestDto.setSmsAuthId(1L);
        requestDto.setSmsAuthNumber("5678");
        requestDto.setPhoneNumber("01094342762");

        when(smsAuthRepository.findById(1L)).thenReturn(Optional.of(smsAuth));

        // when
        FindSmsAuthNumberGetResponse response = userLoginService.checkFindIdSmsAuth(requestDto);

        // then
        assertEquals("SMS 인증에 실패하였습니다. \n인증번호를 정상적으로 입력해주세요.", response.getResult());
    }

    @Test
    @DisplayName(value = "아이디 찾기 - SMS 인증번호 검증 - 실패 테스트 (인증번호 만료)")
    void checkFindIdSmsAuthFailureTestExpiredAuthNumber() throws Exception {

        //given
        SmsAuthEntity smsAuth = new SmsAuthEntity();
        smsAuth.setSmsAuthId(1L);
        smsAuth.setSmsAuthNumber("1234");
        smsAuth.setExpirationTime(LocalDateTime.now().minusMinutes(1)); // Set expired time
        smsAuth.setIsUse(false);

        FindIdCheckSmsGetRequest requestDto = new FindIdCheckSmsGetRequest();
        requestDto.setSmsAuthId(1L);
        requestDto.setSmsAuthNumber("1234");
        requestDto.setPhoneNumber("01094342762");

        when(smsAuthRepository.findById(1L)).thenReturn(Optional.of(smsAuth));

        // when
        FindSmsAuthNumberGetResponse response = userLoginService.checkFindIdSmsAuth(requestDto);

        // then
        assertEquals("인증번호가 만료되었습니다. \n다시 인증번호를 발급받아주세요.", response.getResult());
    }

    @Test
    @DisplayName(value = "아이디 찾기 - SMS 인증번호 검증 - 실패 테스트 (이미 사용된 인증번호)")
    void checkFindIdSmsAuthFailureTestUsedAuthNumber() throws Exception {

        //given
        SmsAuthEntity smsAuth = new SmsAuthEntity();
        smsAuth.setSmsAuthId(1L);
        smsAuth.setSmsAuthNumber("1234");
        smsAuth.setExpirationTime(LocalDateTime.now().plusMinutes(5));
        smsAuth.setIsUse(true); // Set to used

        FindIdCheckSmsGetRequest requestDto = new FindIdCheckSmsGetRequest();
        requestDto.setSmsAuthId(1L);
        requestDto.setSmsAuthNumber("1234");
        requestDto.setPhoneNumber("01094342762");

        when(smsAuthRepository.findById(1L)).thenReturn(Optional.of(smsAuth));

        // when
        FindSmsAuthNumberGetResponse response = userLoginService.checkFindIdSmsAuth(requestDto);

        // then
        assertEquals("이미 사용된 인증번호입니다. \n다시 인증번호를 발급받아주세요.", response.getResult());
    }

    @Test
    @DisplayName(value = "아이디 찾기 - 휴대폰 번호로 가입된 회원 없음 테스트")
    void checkFindIdSmsAuthFailureTestNoUser() throws Exception {

        //given
        SmsAuthEntity smsAuth = new SmsAuthEntity();
        smsAuth.setSmsAuthId(1L);
        smsAuth.setSmsAuthNumber("1234");
        smsAuth.setExpirationTime(LocalDateTime.now().plusMinutes(5));
        smsAuth.setIsUse(false);

        FindIdCheckSmsGetRequest requestDto = new FindIdCheckSmsGetRequest();
        requestDto.setSmsAuthId(1L);
        requestDto.setSmsAuthNumber("1234");
        requestDto.setPhoneNumber("01094342762");

        when(smsAuthRepository.findById(1L)).thenReturn(Optional.of(smsAuth));
        when(userRepository.findByPhoneNumber("01094342762")).thenReturn(Optional.empty());

        // when
        FindSmsAuthNumberGetResponse response = userLoginService.checkFindIdSmsAuth(requestDto);

        // then
        assertEquals("입력하신 휴대폰 번호와 일치하는 아이디 정보가 없습니다. \n(고객센터 문의 요망)", response.getResult());
    }

    // ************* 비밀번호 찾기 *************

    @Test
    @DisplayName(value = "비밀번호 찾기 - SMS 인증번호 전송 성공 테스트")
    void sendFindPwSmsAuthNumberSuccessTest() throws Exception {
    }

    @Test
    @DisplayName(value = "비밀번호 찾기 - SMS 인증번호 전송 실패 테스트")
    void sendFindPwSmsAuthNumberFailureTest() throws Exception {
    }

    @Test
    @DisplayName(value = "비밀번호 찾기 - SMS 인증번호 검증 & 이메일과 휴대폰 번호로 가입 회원 조회 성공 테스트")
    void checkFindPwSmsAuthSuccessTest() throws Exception {
    }

    @Test
    @DisplayName(value = "비밀번호 찾기 - SMS 인증번호 검증 & 이메일과 휴대폰 번호로 가입 회원 조회 실패 테스트")
    void checkFindPwSmsAuthFailureTest() throws Exception {
    }


}
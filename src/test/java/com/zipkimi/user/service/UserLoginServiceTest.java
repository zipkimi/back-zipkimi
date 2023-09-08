package com.zipkimi.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.zipkimi.entity.SmsAuthEntity;
import com.zipkimi.entity.UserEntity;
import com.zipkimi.global.exception.BadRequestException;
import com.zipkimi.global.service.SmsService;
import com.zipkimi.repository.SmsAuthRepository;
import com.zipkimi.repository.UserRepository;
import com.zipkimi.user.dto.request.FindIdCheckSmsGetRequest;
import com.zipkimi.user.dto.request.FindPwCheckSmsGetRequest;
import com.zipkimi.user.dto.request.PassResetSmsAuthNumberPostRequest;
import com.zipkimi.user.dto.request.SmsAuthNumberPostRequest;
import com.zipkimi.user.dto.response.FindSmsAuthNumberGetResponse;
import com.zipkimi.user.dto.response.FindSmsAuthNumberPostResponse;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@ExtendWith(MockitoExtension.class)
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

    @Mock
    private PasswordEncoder passwordEncoder;

    private static final String PHONE_NUMBER = "01094342762";
    private static final String EMAIL = "test@gmail.com";

    private static final String CHAR_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom secureRandom = new SecureRandom();
    private final Random random = new Random();




    @BeforeAll
    static void beforeAll() {
        log.info("## BeforeAll 호출 ##");
    }

    @AfterAll
    static void afterAll() {
        log.info("## AfterAll 호출 ##");
    }



    // ************* 로그인 테스트를 위한 간단 일반 회원 가입 *************
    // ************* 로그인 *************
    // ************* 토큰 재발급 *************
    // ************* 로그아웃 *************







    // ************* 아이디 찾기 *************

    // ************* 아이디 찾기 - SMS 인증번호 전송 *************

    // User 생성
    private UserEntity createUser() {
        UserEntity user = new UserEntity();
        user.setPhoneNumber(PHONE_NUMBER);
        user.setEmail(EMAIL);

        // 2-1. 임시 비밀번호 생성
        String newPassword = tempPassword(10);
        // 비밀번호 암호화 적용
        String encodeNewPw = passwordEncoder.encode(newPassword);
        user.setPassword(encodeNewPw);
        return user;
    }

    // SMS 인증 객체 생성
    private SmsAuthEntity createSmsAuthEntity() {
        SmsAuthEntity smsAuth = new SmsAuthEntity();
        smsAuth.setSmsAuthId(1L);
        smsAuth.setSmsAuthNumber("1234");
        smsAuth.setExpirationTime(LocalDateTime.now().plusMinutes(5));
        smsAuth.setIsUse(false);
        return smsAuth;
    }

    // ID 찾기 확인 요청 객체 생성
    private FindIdCheckSmsGetRequest createFindIdCheckSmsGetRequest(Long smsAuthId, String smsAuthNumber, String phoneNumber) {
        FindIdCheckSmsGetRequest requestDto = new FindIdCheckSmsGetRequest();
        requestDto.setSmsAuthId(smsAuthId);
        requestDto.setSmsAuthNumber(smsAuthNumber);
        requestDto.setPhoneNumber(phoneNumber);
        return requestDto;
    }

    // Pw 찾기 확인 요청 객체 생성
    private FindPwCheckSmsGetRequest createFindPwCheckSmsGetRequest(Long smsAuthId, String smsAuthNumber, String phoneNumber, String email) {
        FindPwCheckSmsGetRequest requestDto = new FindPwCheckSmsGetRequest();
        requestDto.setSmsAuthId(smsAuthId);
        requestDto.setSmsAuthNumber(smsAuthNumber);
        requestDto.setPhoneNumber(phoneNumber);
        requestDto.setEmail(email);
        return requestDto;
    }

    @Test
    @DisplayName(value = "아이디 찾기 - SMS 인증번호 전송 성공 테스트")
    @Transactional
    void sendFindIdSmsAuthNumberSuccessTest() throws Exception {

        log.info("## test1 시작 ##");

        // given
        UserEntity user = createUser();
        when(userRepository.findByPhoneNumberAndIsUseIsTrue(any())).thenReturn(Optional.of(user));
        when(smsAuthRepository.findValidSmsAuthByPhoneNumberAndType(any(), any(), any())).thenReturn(null);

        SmsAuthNumberPostRequest requestDto = new SmsAuthNumberPostRequest(user.getPhoneNumber());

        // when
        FindSmsAuthNumberPostResponse response = userLoginService.sendFindIdSmsAuthNumber(requestDto);

        // then
        assertEquals("인증번호를 전송하였습니다.", response.getMessage());
    }

    @Test
    @DisplayName("아이디 찾기 - SMS 인증번호 전송 실패 테스트 (#1. 휴대폰 번호로 가입된 회원 없음)")
    void sendFindIdSmsAuthNumberFailureTestUserNotFound() throws Exception {

        log.info("## test2 시작 ##");

        // given
        SmsAuthNumberPostRequest requestDto = new SmsAuthNumberPostRequest("01094342762");
        lenient().when(userRepository.findByPhoneNumber(requestDto.getPhoneNumber())).thenReturn(Optional.empty());

        // when
        FindSmsAuthNumberPostResponse response = userLoginService.sendFindIdSmsAuthNumber(requestDto);

        // then
        assertEquals("입력하신 휴대폰 번호와 일치하는 정보가 없습니다. \n(고객센터 문의 요망)", response.getMessage());
    }

    @Test
    @DisplayName("아이디 찾기 - SMS 인증번호 전송 실패 테스트 (#2. 유효한 SMS 인증번호 이미 존재)")
    void sendFindIdSmsAuthNumberFailureTestExistingSmsAuth() throws Exception {

        log.info("## test3 시작 ##");

        // given
        SmsAuthNumberPostRequest requestDto = new SmsAuthNumberPostRequest("01094342762");

        when(userRepository.findByPhoneNumberAndIsUseIsTrue(requestDto.getPhoneNumber())).thenReturn(Optional.of(new UserEntity()));
        when(smsAuthRepository.findValidSmsAuthByPhoneNumberAndType(any(), any(), any())).thenReturn(new SmsAuthEntity());

        // when
        FindSmsAuthNumberPostResponse response = userLoginService.sendFindIdSmsAuthNumber(requestDto);

        // then
        assertEquals("유효시간이 만료되지 않은 인증번호가 존재합니다. \n인증번호를 확인하거나, 유효시간이 지난 후 다시 시도해주세요.", response.getMessage());
    }

    @Test
    @DisplayName("아이디 찾기 - SMS 인증번호 전송 실패 테스트 (#3. SMS 인증번호 생성 중 오류 발생)")
    void sendFindIdSmsAuthNumberFailureTestSmsAuthCreationError() throws Exception {

        log.info("## test4 시작 ##");

        // given
        SmsAuthNumberPostRequest requestDto = new SmsAuthNumberPostRequest("01094342762");

        when(userRepository.findByPhoneNumberAndIsUseIsTrue(requestDto.getPhoneNumber())).thenReturn(Optional.of(new UserEntity()));
        when(smsAuthRepository.findValidSmsAuthByPhoneNumberAndType(any(), any(), any())).thenReturn(null);

        doThrow(new RuntimeException("SMS 인증번호를 생성하던 중 오류가 발생하였습니다."))
                .when(smsAuthRepository).save(any());

        // when / then
        Exception exception = org.junit.jupiter.api.Assertions.assertThrows(BadRequestException.class, () -> {
            userLoginService.sendFindIdSmsAuthNumber(requestDto);
        });

        assertEquals("SMS 인증번호를 생성하던 중 오류가 발생하였습니다.", exception.getMessage());
    }

    // ************* 아이디 찾기 - SMS 인증번호 확인 및 아이디 찾기 *************

    @Test
    @DisplayName(value = "아이디 찾기 - SMS 인증번호 검증 & 휴대폰 번호로 가입된 회원 이메일 조회 성공 테스트")
    void checkFindIdSmsAuthSuccessTest() throws Exception {

        log.info("## test5 시작 ##");
        // given
        SmsAuthEntity smsAuth = createSmsAuthEntity();
        UserEntity user = createUser();
        FindIdCheckSmsGetRequest requestDto = createFindIdCheckSmsGetRequest(smsAuth.getSmsAuthId(), smsAuth.getSmsAuthNumber(), user.getPhoneNumber());

        when(smsAuthRepository.findById(smsAuth.getSmsAuthId())).thenReturn(Optional.of(smsAuth));
        when(userRepository.findByPhoneNumberAndIsUseIsTrue(user.getPhoneNumber())).thenReturn(Optional.of(user));

        // when
        FindSmsAuthNumberGetResponse response = userLoginService.checkFindIdSmsAuth(requestDto);

        // then
        assertEquals("고객님의 집킴이 계정을 찾았습니다. 아이디 확인 후 로그인 해주세요.", response.getMessage());
        assertEquals(user.getEmail(), response.getEmail());
    }


    @Test
    @DisplayName(value = "아이디 찾기 - SMS 인증번호 검증 - 실패 테스트 (#1. 인증번호 불일치)")
    void checkFindIdSmsAuthFailureTestInvalidAuthNumber() throws Exception {

        log.info("## test6 시작 ##");

        // given
        SmsAuthEntity smsAuth = createSmsAuthEntity();
        FindIdCheckSmsGetRequest requestDto = createFindIdCheckSmsGetRequest(smsAuth.getSmsAuthId(), "5678", "01094342762");

        when(smsAuthRepository.findById(1L)).thenReturn(Optional.of(smsAuth));

        // when
        FindSmsAuthNumberGetResponse response = userLoginService.checkFindIdSmsAuth(requestDto);

        // then
        assertEquals("SMS 인증에 실패하였습니다. \n인증번호를 정상적으로 입력해주세요.", response.getMessage());
    }

    @Test
    @DisplayName(value = "아이디 찾기 - SMS 인증번호 검증 - 실패 테스트 (#2. 인증번호 만료)")
    void checkFindIdSmsAuthFailureTestExpiredAuthNumber() throws Exception {

        log.info("## test7 시작 ##");

        //given
        SmsAuthEntity smsAuth = createSmsAuthEntity();
        smsAuth.setExpirationTime(LocalDateTime.now().minusMinutes(1)); // 만료 시간을 현재 날짜와 시간 - 1분으로 세팅 (이미 만료된 인증번호)

        FindIdCheckSmsGetRequest requestDto = createFindIdCheckSmsGetRequest(smsAuth.getSmsAuthId(), "1234", "01094342762");

        when(smsAuthRepository.findById(1L)).thenReturn(Optional.of(smsAuth));

        // when
        FindSmsAuthNumberGetResponse response = userLoginService.checkFindIdSmsAuth(requestDto);

        // then
        assertEquals("인증번호가 만료되었습니다. \n다시 인증번호를 발급받아주세요.", response.getMessage());
    }

    @Test
    @DisplayName(value = "아이디 찾기 - SMS 인증번호 검증 - 실패 테스트 (#3. 이미 사용된 인증번호)")
    void checkFindIdSmsAuthFailureTestUsedAuthNumber() throws Exception {

        log.info("## test8 시작 ##");

        //given
        SmsAuthEntity smsAuth = createSmsAuthEntity();
        smsAuth.setIsUse(true);

        FindIdCheckSmsGetRequest requestDto = createFindIdCheckSmsGetRequest(smsAuth.getSmsAuthId(), "1234", "01094342762");

        when(smsAuthRepository.findById(1L)).thenReturn(Optional.of(smsAuth));

        // when
        FindSmsAuthNumberGetResponse response = userLoginService.checkFindIdSmsAuth(requestDto);

        // then
        assertEquals("이미 사용된 인증번호입니다. \n다시 인증번호를 발급받아주세요.", response.getMessage());
    }

    @Test
    @DisplayName(value = "아이디 찾기 - 휴대폰 번호로 가입된 회원 없음 테스트")
    void checkFindIdSmsAuthFailureTestNoUser() throws Exception {

        log.info("## test9 시작 ##");

        //given
        SmsAuthEntity smsAuth = createSmsAuthEntity();
        FindIdCheckSmsGetRequest requestDto = createFindIdCheckSmsGetRequest(smsAuth.getSmsAuthId(), "1234", "01094342762");

        when(smsAuthRepository.findById(anyLong())).thenReturn(Optional.of(smsAuth));
        when(userRepository.findByPhoneNumberAndIsUseIsTrue("01094342762")).thenReturn(Optional.empty());

        // when
        FindSmsAuthNumberGetResponse response = userLoginService.checkFindIdSmsAuth(requestDto);

        // then
        assertEquals("입력하신 휴대폰 번호와 일치하는 아이디 정보가 없습니다. \n(고객센터 문의 요망)", response.getMessage());
        assertNull(response.getEmail());

    }

    // ************* 비밀번호 찾기 *************

    // ************* 비밀번호 찾기 - SMS 인증번호 전송 *************

    @Test
    @DisplayName(value = "비밀번호 찾기 - SMS 인증번호 전송 성공 테스트")
    void sendFindPwSmsAuthNumberSuccessTest() throws Exception {

        log.info("## test10 시작 ##");

        // given
        UserEntity user = createUser();

        when(userRepository.findByPhoneNumberAndIsUseIsTrue(any())).thenReturn(Optional.of(user));
        when(smsAuthRepository.findValidSmsAuthByPhoneNumberAndType(any(), any(), any())).thenReturn(null);

        PassResetSmsAuthNumberPostRequest requestDto = new PassResetSmsAuthNumberPostRequest(user.getPhoneNumber(), user.getEmail());

        // when
        FindSmsAuthNumberPostResponse response = userLoginService.sendFindPwSmsAuthNumber(requestDto);

        // then
        assertEquals("인증번호를 전송하였습니다.", response.getMessage());

    }

    @Test
    @DisplayName("비밀번호 찾기 - SMS 인증번호 전송 실패 테스트 (#1. 휴대폰 번호와 이메일로 가입된 회원 없음)")
    void sendFindPwSmsAuthNumberFailureTestUserNotFound() throws Exception {

        log.info("## test11 시작 ##");

        // given
        PassResetSmsAuthNumberPostRequest requestDto = new PassResetSmsAuthNumberPostRequest("01094342762", "test@gmail.com");
        when(userRepository.findByPhoneNumberAndIsUseIsTrue(requestDto.getPhoneNumber())).thenReturn(Optional.empty());

        // when
        FindSmsAuthNumberPostResponse response = userLoginService.sendFindPwSmsAuthNumber(requestDto);

        // then
        assertEquals("입력하신 휴대폰 번호와 일치하는 정보가 없습니다. \n(고객센터 문의 요망)", response.getMessage());
    }

    @Test
    @DisplayName("비밀번호 찾기 - SMS 인증번호 전송 실패 테스트 (#2. 유효한 SMS 인증번호 이미 존재)")
    void sendFindPwSmsAuthNumberFailureTestExistingSmsAuth() throws Exception {

        log.info("## test12 시작 ##");

        // given
        PassResetSmsAuthNumberPostRequest requestDto = new PassResetSmsAuthNumberPostRequest("01094342762", "test@gmail.com");

        when(userRepository.findByPhoneNumberAndIsUseIsTrue(requestDto.getPhoneNumber())).thenReturn(Optional.of(new UserEntity()));
        when(smsAuthRepository.findValidSmsAuthByPhoneNumberAndType(any(), any(), any())).thenReturn(new SmsAuthEntity());

        // when
        FindSmsAuthNumberPostResponse response = userLoginService.sendFindPwSmsAuthNumber(requestDto);

        // then
        assertEquals("유효시간이 만료되지 않은 인증번호가 존재합니다. \n인증번호를 확인하거나, 유효시간이 지난 후 다시 시도해주세요.", response.getMessage());
    }

    @Test
    @DisplayName("비밀번호 찾기 - SMS 인증번호 전송 실패 테스트 (#3. SMS 인증번호 생성 중 오류 발생)")
    void sendFindPwSmsAuthNumberFailureTestSmsAuthCreationError() throws Exception {

        log.info("## test13 시작 ##");

        // given
        PassResetSmsAuthNumberPostRequest requestDto = new PassResetSmsAuthNumberPostRequest("01094342762", "test@gmail.com");

        when(userRepository.findByPhoneNumberAndIsUseIsTrue(requestDto.getPhoneNumber())).thenReturn(Optional.of(new UserEntity()));
        when(smsAuthRepository.findValidSmsAuthByPhoneNumberAndType(any(), any(), any())).thenReturn(null);

        doThrow(new RuntimeException("SMS 인증번호를 생성하던 중 오류가 발생하였습니다."))
                .when(smsAuthRepository).save(any());

        // when / then
        Exception exception = org.junit.jupiter.api.Assertions.assertThrows(BadRequestException.class, () -> {
            userLoginService.sendFindPwSmsAuthNumber(requestDto);
        });

        assertEquals("SMS 인증번호를 생성하던 중 오류가 발생하였습니다.", exception.getMessage());
    }

    // ************* 비밀번호 찾기 - SMS 인증번호 확인 및 비밀번호 초기화 *************

    @Test
    @DisplayName(value = "비밀번호 찾기 - SMS 인증번호 검증 & 이메일과 휴대폰 번호로 가입 회원 조회 성공 테스트")
    void checkFindPwSmsAuthSuccessTest() throws Exception {

        log.info("## test14 시작 ##");

        // given
        // SMS 인증 객체 생성
        SmsAuthEntity smsAuth = createSmsAuthEntity();
        when(smsAuthRepository.findById(1L)).thenReturn(Optional.of(smsAuth));

        // USER 회원 객체 생성
        UserEntity user = createUser();
        when(userRepository.findByPhoneNumberAndEmailAndIsUseIsTrue("01094342762","test@gmail.com")).thenReturn(Optional.of(user));

        FindPwCheckSmsGetRequest requestDto = createFindPwCheckSmsGetRequest(smsAuth.getSmsAuthId(), "1234", "01094342762", "test@gmail.com");

        // when
        FindSmsAuthNumberGetResponse response = userLoginService.checkFindPwSmsAuth(requestDto);

        assertNotNull(response);
        assertTrue(response.getMessage().contains("고객님의 비밀번호가 초기화 되었습니다. \n비밀번호 확인 후 로그인해주세요."));
    }

    @Test
    @DisplayName(value = "비밀번호 찾기 - SMS 인증번호 검증 - 실패 테스트 (#1. 인증번호 불일치)")
    void checkFindPwSmsAuthFailureTestInvalidAuthNumber() throws Exception {

        log.info("## test15 시작 ##");

        // given
        SmsAuthEntity smsAuth = createSmsAuthEntity();
        FindPwCheckSmsGetRequest requestDto = createFindPwCheckSmsGetRequest(smsAuth.getSmsAuthId(), "5678", "01094342762", "test@gmail.com");

        when(smsAuthRepository.findById(1L)).thenReturn(Optional.of(smsAuth));

        // when
        FindSmsAuthNumberGetResponse response = userLoginService.checkFindPwSmsAuth(requestDto);

        // then
        assertEquals("SMS 인증에 실패하였습니다. \n인증번호를 정상적으로 입력해주세요.", response.getMessage());
    }

    @Test
    @DisplayName(value = "비밀번호 찾기 - SMS 인증번호 검증 - 실패 테스트 (#2. 인증번호 만료)")
    void checkFindPwSmsAuthFailureTestExpiredAuthNumber() throws Exception {

        log.info("## test16 시작 ##");

        //given
        SmsAuthEntity smsAuth = createSmsAuthEntity();
        smsAuth.setExpirationTime(LocalDateTime.now().minusMinutes(1)); // 만료 시간을 현재 날짜와 시간 - 1분으로 세팅 (이미 만료된 인증번호)

        FindPwCheckSmsGetRequest requestDto = createFindPwCheckSmsGetRequest(smsAuth.getSmsAuthId(), "1234", "01094342762", "test@gmail.com");

        when(smsAuthRepository.findById(1L)).thenReturn(Optional.of(smsAuth));

        // when
        FindSmsAuthNumberGetResponse response = userLoginService.checkFindPwSmsAuth(requestDto);

        // then
        assertEquals("인증번호가 만료되었습니다. \n다시 인증번호를 발급받아주세요.", response.getMessage());
    }

    @Test
    @DisplayName(value = "비밀번호 찾기 - SMS 인증번호 검증 - 실패 테스트 (#3. 이미 사용된 인증번호)")
    void checkFindPwSmsAuthFailureTestUsedAuthNumber() throws Exception {

        log.info("## test17 시작 ##");

        //given
        SmsAuthEntity smsAuth = createSmsAuthEntity();
        smsAuth.setIsUse(true); // 이미 사용된 인증번호 = true 로 세팅

        FindPwCheckSmsGetRequest requestDto = createFindPwCheckSmsGetRequest(smsAuth.getSmsAuthId(), "1234", "01094342762", "test@gmail.com");

        when(smsAuthRepository.findById(1L)).thenReturn(Optional.of(smsAuth));

        // when
        FindSmsAuthNumberGetResponse response = userLoginService.checkFindPwSmsAuth(requestDto);

        // then
        assertEquals("이미 사용된 인증번호입니다. \n다시 인증번호를 발급받아주세요.", response.getMessage());
    }

    @Test
    @DisplayName(value = "비밀번호 찾기 - 휴대폰 번호와 이메일로 가입된 회원 없음 테스트")
    void checkFindPwSmsAuthFailureTestNoUser() throws Exception {

        log.info("## test18 시작 ##");

        //given
        SmsAuthEntity smsAuth = createSmsAuthEntity();

        FindPwCheckSmsGetRequest requestDto = createFindPwCheckSmsGetRequest(smsAuth.getSmsAuthId(), "1234", "01094342762", "test@gmail.com");

        when(smsAuthRepository.findById(1L)).thenReturn(Optional.of(smsAuth));
        when(userRepository.findByPhoneNumberAndEmailAndIsUseIsTrue("01094342762", "test@gmail.com")).thenReturn(Optional.empty());

        // when
        FindSmsAuthNumberGetResponse response = userLoginService.checkFindPwSmsAuth(requestDto);

        // then
        assertEquals("입력하신 휴대폰 번호와 이메일 정보가 일치하는 사용자가 없습니다. \n(고객센터 문의 요망)", response.getMessage());
    }

    @Test
    @DisplayName(value = "비밀번호 찾기 - 임시 비밀번호 생성 테스트")
    void testTempPasswordGeneration() {

        log.info("## testTempPasswordGeneration 시작 ##");

        String generatedPassword = userLoginService.tempPassword(10);
        assertEquals(10, generatedPassword.length());
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

package com.zipkimi.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.zipkimi.entity.SmsAuthEntity;
import com.zipkimi.entity.UserEntity;
import com.zipkimi.global.exception.BadRequestException;
import com.zipkimi.repository.SmsAuthRepository;
import com.zipkimi.repository.UserRepository;
import com.zipkimi.user.dto.request.JoinUserPostRequest;
import com.zipkimi.user.dto.request.SmsAuthNumberGetRequest;
import com.zipkimi.user.dto.request.SmsAuthNumberPostRequest;
import com.zipkimi.user.dto.response.JoinUserPostResponse;
import com.zipkimi.user.dto.response.SmsAuthNumberGetResponse;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

//@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Transactional
class UserManagementServiceTest {

    @InjectMocks
    UserManagementService userManagementService;
    @Mock
    UserRepository userRepository;
    @Mock
    SmsAuthRepository smsAuthRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Test
    @DisplayName(value = "인증번호 전송 성공 테스트")
    void sendSmsAuthNumberSuccessTest() {
        // given
        SmsAuthNumberPostRequest request = SmsAuthNumberPostRequest.builder()
                .phoneNumber("01097050821").build();
        SmsAuthEntity smsAuth = new SmsAuthEntity();
        smsAuth.setSmsAuthId(1);
        smsAuth.setSmsAuthNumber("0000");
        smsAuth.setPhoneNumber("01097050821");
        smsAuth.setExpirationTime(LocalDateTime.now().minusMinutes(3L));
        smsAuth.setSmsAuthType("In");
        smsAuth.setIsUse(true);
        //TODO Type값 설정 필요
        smsAuth.setIsAuthenticate(false);
        smsAuth.setContent("본인확인 인증번호 (" + smsAuth.getSmsAuthNumber() + ")입력시 \n"
                + "정상처리 됩니다.");

        // mocking
        given(userRepository.findByPhoneNumberAndIsUseIsTrue(request.getPhoneNumber()))
                .willReturn(Optional.ofNullable(null));
        given(smsAuthRepository.save(any())).willReturn(smsAuth);

        // then                               // when
        assertEquals("인증번호를 전송하였습니다.", userManagementService.sendSmsAuthNumber(request).getMessage());
    }

    @Test
    @DisplayName(value = "인증번호 전송 실패 테스트 - 이미 등록한 전화번호 케이스")
    void sendSmsAuthNumberWithAlreadyJoinedPhoneNumberFailTest() {
        // 휴대폰 번호 유효성 검사 실패 - 이미 등록한 전화번호
        // given
        UserEntity user = new UserEntity();
        user.setPhoneNumber("01000000000");
        user.setName("테스트이름");
        user.setEmail("test@gmail.com");
        //TODO is_use default값 true로 설정하
        user.setUse(true);
        SmsAuthNumberPostRequest request = SmsAuthNumberPostRequest.builder()
                .phoneNumber(user.getPhoneNumber()).build();

        // mocking
        given(userRepository.findByPhoneNumberAndIsUseIsTrue(user.getPhoneNumber()))
                .willReturn(Optional.ofNullable(user));

        // then                                          // when
        assertEquals("이미 등록된 휴대폰 번호입니다.",
                userManagementService.sendSmsAuthNumber(request).getMessage());
    }

    @Test
    @DisplayName(value = "인증번호 확인 실패 테스트 - 유효시간 지난 인증번호 케이스")
    void checkSmsAuthNumberWithExpiredAuthNumberFailTest() {
        //given
        SmsAuthNumberGetRequest requestDto = SmsAuthNumberGetRequest.builder()
                .smsAuthId(1L)
                .smsAuthNumber("0000")
                .phoneNumber("01000000000")
                .build();
        SmsAuthEntity smsAuth = new SmsAuthEntity();
        smsAuth.setSmsAuthId(1);
        smsAuth.setSmsAuthNumber("0000");
        smsAuth.setPhoneNumber("01000000000");
        smsAuth.setExpirationTime(LocalDateTime.now().minusMinutes(3L));
        //TODO Type값 설정 필요
        smsAuth.setSmsAuthType("In");
        smsAuth.setIsUse(true);
        smsAuth.setIsAuthenticate(false);

        Long fakeSmsAuthId = 1l;
        ReflectionTestUtils.setField(smsAuth, "smsAuthId", fakeSmsAuthId);

        // mocking
        given(smsAuthRepository.findById(fakeSmsAuthId))
                .willReturn(Optional.ofNullable(smsAuth));
        //when
        SmsAuthNumberGetResponse response = userManagementService.checkSmsAuthNumber(requestDto);
        //then
        assertEquals("인증번호 유효시간이 만료됐습니다. 재전송해주세요.", response.getMessage());
    }

    @Test
    @DisplayName(value = "인증번호 확인 실패 테스트 - 일치하지 않는 인증번호 케이스")
    void checkSmsAuthNumberWithWrongAuthNumberFailTest() {
        //given
        SmsAuthNumberGetRequest requestDto = SmsAuthNumberGetRequest.builder()
                .smsAuthId(1L)
                .smsAuthNumber("1234")
                .phoneNumber("01000000000")
                .build();
        SmsAuthEntity smsAuth = new SmsAuthEntity();
        smsAuth.setSmsAuthId(1);
        smsAuth.setSmsAuthNumber("0000");
        smsAuth.setPhoneNumber("01000000000");
        smsAuth.setExpirationTime(LocalDateTime.now().plusMinutes(3L));
        //TODO Type값 설정 필요
        smsAuth.setSmsAuthType("In");
        smsAuth.setIsUse(true);
        smsAuth.setIsAuthenticate(false);

        Long fakeSmsAuthId = 1l;
        ReflectionTestUtils.setField(smsAuth, "smsAuthId", fakeSmsAuthId);

        // mocking
        given(smsAuthRepository.findById(fakeSmsAuthId))
                .willReturn(Optional.ofNullable(smsAuth));
        //when
        SmsAuthNumberGetResponse response = userManagementService.checkSmsAuthNumber(requestDto);
        //then
        assertEquals("인증번호를 확인해주세요.", response.getMessage());
    }

    @Test
    @DisplayName(value = "인증번호 확인 실패 테스트 - 전송이력이 없는 인증 아이디 케이스")
    void checkSmsAuthNumberWithWrongSmsAuthIdFailTest() {
        //given
        SmsAuthNumberGetRequest requestDto = SmsAuthNumberGetRequest.builder()
                .smsAuthId(1L)
                .smsAuthNumber("1234")
                .phoneNumber("01000000000")
                .build();

        Long fakeSmsAuthId = 1l;

        // mocking
        given(smsAuthRepository.findById(fakeSmsAuthId)).willReturn(Optional.empty());

        //then                                                                    // when
        BadRequestException exception = assertThrows(BadRequestException.class,()-> userManagementService.checkSmsAuthNumber(requestDto));
        String message = exception.getMessage();
        assertEquals("인증번호를 확인해주세요.", message);
    }

    @Test
    @DisplayName(value = "인증번호 확인 성공 테스트")
    void checkSmsAuthNumberSuccessTest() {
        //given
        SmsAuthNumberGetRequest requestDto = SmsAuthNumberGetRequest.builder()
                .smsAuthId(1L)
                .smsAuthNumber("0000")
                .phoneNumber("01000000000")
                .build();
        SmsAuthEntity smsAuth = new SmsAuthEntity();
        smsAuth.setSmsAuthId(1);
        smsAuth.setSmsAuthNumber("0000");
        smsAuth.setPhoneNumber("01000000000");
        smsAuth.setExpirationTime(LocalDateTime.now().plusMinutes(3L));
        //TODO Type값 설정 필요
        smsAuth.setSmsAuthType("In");
        smsAuth.setIsUse(true);
        smsAuth.setIsAuthenticate(false);

        Long fakeSmsAuthId = 1l;
        ReflectionTestUtils.setField(smsAuth, "smsAuthId", fakeSmsAuthId);

        // mocking
        given(smsAuthRepository.findById(fakeSmsAuthId))
                .willReturn(Optional.ofNullable(smsAuth));
        //when
        SmsAuthNumberGetResponse response = userManagementService.checkSmsAuthNumber(requestDto);
        //then
        assertEquals("본인 인증에 성공했습니다.", response.getMessage());
    }

    @Test
    @DisplayName(value = "일반회원 가입 실패 테스트 -  이미 가입한 Email 을 사용한 케이스")
    void joinUserWithAlreadyJoinedEmailFailTest() {
        //given
        UserEntity user = new UserEntity();
        user.setPhoneNumber("01000000000");
        user.setName("테스트이름");
        user.setEmail("test@gmail.com");
        //TODO is_use default값 true로 설정하
        user.setUse(true);
        JoinUserPostRequest request = JoinUserPostRequest.builder()
                .email(user.getEmail())
                .pw("")
                .name(user.getName())
                .smsAuthId(1L)
                .build();

        // mocking
        given(userRepository.findByEmailAndIsUseIsTrue(user.getEmail()))
                .willReturn(Optional.ofNullable(user));

        //then                                                                    // when
        BadRequestException exception = assertThrows(BadRequestException.class,()-> userManagementService.joinUser(request));
        String message = exception.getMessage();
        assertEquals("이미 사용중인 email 입니다.", message);
    }

    @Test
    @DisplayName(value = "일반회원 가입 실패 테스트 - 유효하지않는 형식의 이메일 사용한 케이스")
    void joinUserWithWrongEmailFailTest() {
        //given
        JoinUserPostRequest request = JoinUserPostRequest.builder()
                .email("tesgmail.com")
                .pw("")
                .name("")
                .smsAuthId(1L)
                .build();

        //then                                                                     //when
        BadRequestException exception = assertThrows(BadRequestException.class,()-> userManagementService.joinUser(request));
        String message = exception.getMessage();
        assertEquals("이메일 주소를 확인해주세요.", message);
    }

    @Test
    @DisplayName(value = "일반회원 가입 실패 테스트 - 유효하지않는 형식의 패스워드를을 사용한 케이스")
    void joinUserWithWrongPasswordFailTest() {
        //given
        JoinUserPostRequest request = JoinUserPostRequest.builder()
                .email("tes@gmail.com")
                .name("test name")
                .pw("12345678")
                .smsAuthId(1L)
                .build();

        //then                                                                     //when
        BadRequestException exception = assertThrows(BadRequestException.class,()-> userManagementService.joinUser(request));
        String message = exception.getMessage();
        assertEquals("패스워드 형식을 확인해주세요.", message);

    }

    @Test
    @DisplayName(value = "일반회원 가입 실패 테스트 - 유효하지않는 형식의 이름을 사용한 케이스")
    void joinUserWithWrongNameFailTest() {
        //given
        JoinUserPostRequest request = JoinUserPostRequest.builder()
                .email("tes@gmail.com")
                .name("&*^$name")
                .pw("")
                .smsAuthId(1L)
                .build();

        //then                                                                     //when
        BadRequestException exception = assertThrows(BadRequestException.class,()-> userManagementService.joinUser(request));
        String message = exception.getMessage();
        assertEquals("이름을 확인해주세요.", message);

    }

    @Test
    @DisplayName(value = "일반회원 가입 실패 테스트 - 인증번호 아이디가 없는 케이스")
    void joinUserWithWrongSmsAuthFailTest() {
        //given
        JoinUserPostRequest request = JoinUserPostRequest.builder()
                .email("tes@gmail.com")
                .name("test name")
                .pw("")
                .smsAuthId(-1L)
                .build();

        //then                                                                     //when
        BadRequestException exception = assertThrows(BadRequestException.class,()-> userManagementService.joinUser(request));
        String message = exception.getMessage();
        assertEquals("인증번호 전송부터 다시 진행해주세요.", message);
    }

    @Test
    @DisplayName(value = "일반회원 가입 실패 테스트 - 인증번호 확인을 받지 않은 케이스")
    void joinUserWithSmsAuthIsAuthenticateFalseFailTest() {
        //given
        SmsAuthEntity smsAuth = new SmsAuthEntity();
        smsAuth.setSmsAuthNumber("0000");
        smsAuth.setPhoneNumber("01000000000");
        smsAuth.setExpirationTime(LocalDateTime.now().plusMinutes(3L));
        smsAuth.setSmsAuthType("JOIN");
        smsAuth.setIsUse(true);
        smsAuth.setIsAuthenticate(false);

        Long fakeSmsAuthId = 1l;
        ReflectionTestUtils.setField(smsAuth, "smsAuthId", fakeSmsAuthId);

        JoinUserPostRequest request = JoinUserPostRequest.builder()
                .email("tes@gmail.com")
                .name("test name")
                .pw("")
                .smsAuthId(smsAuth.getSmsAuthId())
                .build();

        // mocking
        given(smsAuthRepository.findById(fakeSmsAuthId))
                .willReturn(Optional.ofNullable(smsAuth));

        //then                                                                     //when
        BadRequestException exception = assertThrows(BadRequestException.class,()-> userManagementService.joinUser(request));
        String message = exception.getMessage();
        assertEquals("인증번호 전송부터 다시 진행해주세요.", message);
    }

    @Test
    @DisplayName(value = "일반회원 가입 성공 테스트")
    void joinUserSuccessTest() {
        //given
        SmsAuthEntity smsAuth = new SmsAuthEntity();
        smsAuth.setSmsAuthNumber("0000");
        smsAuth.setPhoneNumber("01000000000");
        smsAuth.setExpirationTime(LocalDateTime.now().plusMinutes(3L));
        smsAuth.setSmsAuthType("JOIN");
        smsAuth.setIsUse(true);
        smsAuth.setIsAuthenticate(true);

        Long fakeSmsAuthId = 1l;
        ReflectionTestUtils.setField(smsAuth, "smsAuthId", fakeSmsAuthId);

        UserEntity userEntity = new UserEntity();
        userEntity.setPhoneNumber("01000000000");
        userEntity.setName("테스트이름");
        userEntity.setEmail("test@gmail.com");

        Long fakeId = 1l;
        ReflectionTestUtils.setField(userEntity, "userId", fakeId);
        ReflectionTestUtils.setField(smsAuth, "smsAuthId", fakeId);

        JoinUserPostRequest request = JoinUserPostRequest.builder()
                .email("test@gmail.com")
                .name("test name")
                .pw("test123")
                .smsAuthId(smsAuth.getSmsAuthId())
                .build();

        // mocking
        given(smsAuthRepository.findById(fakeId))
                .willReturn(Optional.ofNullable(smsAuth));
        given(passwordEncoder.encode(request.getPw()))
                .willReturn("$2a$10$rAwrc73Z3Srh6JmwqjwtaOCqYAxr5TYt0sLZmFGcupoVPvP1QJhKa");

        //when
        JoinUserPostResponse response = userManagementService.joinUser(request);
        //then                                                                     //when
        assertEquals("회원가입 완료", response.getMessage());
        assertEquals("test name", userEntity.getName());
        assertEquals("test@gmail.com", userEntity.getEmail());
        assertEquals("test@gmail.com", (userEntity.getPassword()));
    }
}
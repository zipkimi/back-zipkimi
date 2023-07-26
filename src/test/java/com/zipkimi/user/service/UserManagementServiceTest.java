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
import com.zipkimi.user.dto.request.SmsAuthNumberGetRequest;
import com.zipkimi.user.dto.request.SmsAuthNumberPostRequest;
import com.zipkimi.user.dto.response.SmsAuthNumberGetResponse;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
    //TODO test code수정 필요 - @InjectMocks사용하여 데이터 변화 영향없이 일관적인 테스트하도록 수정

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

        //then
        assertThrows(BadRequestException.class
                , () -> {
                    //when
                    userManagementService.checkSmsAuthNumber(requestDto);
                }
                , "인증번호를 확인해주세요.");
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
}
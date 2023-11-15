package com.zipkimi.builder.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.zipkimi.entity.SmsAuthEntity;
import com.zipkimi.entity.UserEntity;
import com.zipkimi.global.exception.BadRequestException;
import com.zipkimi.global.service.SmsService;
import com.zipkimi.global.utils.CodeConstant.SMS_AUTH_CODE;
import com.zipkimi.repository.BuilderUserRepository;
import com.zipkimi.repository.SmsAuthRepository;
import com.zipkimi.repository.UserRepository;
import com.zipkimi.user.dto.request.SmsAuthNumberPostRequest;
import com.zipkimi.user.dto.response.SmsAuthNumberPostResponse;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
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
class BuilderManagementServiceTest {

    @Mock
    private BuilderUserRepository builderUserRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SmsAuthRepository smsAuthRepository;

    @Mock
    private SmsService smsService;

    @InjectMocks
    private BuilderManagementService builderManagementService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName(value = "시공사 회원가입 - SMS 인증번호 전송 성공 테스트")
    @Transactional
    void sendBuilderUserJoinSmsAuthNumberSuccessTest() throws Exception {

        // given
        SmsAuthNumberPostRequest requestDto = new SmsAuthNumberPostRequest("01094342762");

        when(userRepository.findByPhoneNumberAndIsUseIsTrue(requestDto.getPhoneNumber())).thenReturn(Optional.empty());
        when(builderUserRepository.findByPhoneNumberAndIsUseIsTrue(requestDto.getPhoneNumber())).thenReturn(Optional.empty());

        given(smsAuthRepository.findValidSmsAuthByPhoneNumberAndType(any(), any(), any())).willReturn(null);

        SmsAuthEntity savedSmsAuthEntity = new SmsAuthEntity();
        savedSmsAuthEntity.setSmsAuthId(1);
        savedSmsAuthEntity.setSmsAuthNumber("1234");
        savedSmsAuthEntity.setPhoneNumber(requestDto.getPhoneNumber());
        savedSmsAuthEntity.setExpirationTime(LocalDateTime.now().plusMinutes(5L));
        savedSmsAuthEntity.setSmsAuthType(SMS_AUTH_CODE.BUILDER_JOIN.getValue());
        savedSmsAuthEntity.setIsUse(false);
        savedSmsAuthEntity.setIsAuthenticate(false);
        savedSmsAuthEntity.setContent("[집킴이] 본인확인 인증번호는 [" + savedSmsAuthEntity.getSmsAuthNumber() + "] 입니다. 인증번호를 정확히 입력해주세요.");

        when(smsAuthRepository.save(any())).thenReturn(savedSmsAuthEntity);

        // when
        SmsAuthNumberPostResponse response = builderManagementService.sendBuilderUserJoinSmsAuthNumber(requestDto);

        // then
        assertEquals("인증번호를 전송하였습니다.", response.getMessage());
    }

    @Test
    @DisplayName(value = "시공사 회원가입 - SMS 인증번호 전송 실패 테스트 (#1. 유효하지 않은 형식의 휴대폰 번호)")
    @Transactional
    void sendBuilderUserJoinSmsAuthNumberFailureTestInvalidPhoneNumber() {

        // given
        SmsAuthNumberPostRequest requestDto = new SmsAuthNumberPostRequest("0101234");

        // when
        SmsAuthNumberPostResponse response = builderManagementService.sendBuilderUserJoinSmsAuthNumber(requestDto);

        // then
        assertEquals("휴대폰 번호를 정확히 입력해주세요.", response.getMessage());
    }

    @Test
    @DisplayName(value = "시공사 회원가입 - SMS 인증번호 전송 실패 테스트 (#2. 이미 등록된 휴대폰 번호)")
    @Transactional
    void sendBuilderUserJoinSmsAuthNumberFailureTestPhoneNumberAlreadyRegistered() {

        // given
        String existingPhoneNumber = "01094342762";
        SmsAuthNumberPostRequest requestDto = new SmsAuthNumberPostRequest(existingPhoneNumber);

        when(userRepository.findByPhoneNumberAndIsUseIsTrue(existingPhoneNumber)).thenReturn(Optional.of(new UserEntity()));

        // when
        SmsAuthNumberPostResponse response = builderManagementService.sendBuilderUserJoinSmsAuthNumber(requestDto);

        // then
        assertEquals("이미 등록된 휴대폰 번호입니다.", response.getMessage());
    }

    @Test
    @DisplayName(value = "시공사 회원가입 - SMS 인증번호 전송 실패 테스트 (#3. 유효한 SMS 인증번호 이미 존재)")
    @Transactional
    void sendBuilderUserJoinSmsAuthNumberFailureTestExistingAuth() {

        // given
        SmsAuthNumberPostRequest requestDto = new SmsAuthNumberPostRequest("01094342762");

        when(userRepository.findByPhoneNumberAndIsUseIsTrue(requestDto.getPhoneNumber())).thenReturn(Optional.empty());
        when(builderUserRepository.findByPhoneNumberAndIsUseIsTrue(requestDto.getPhoneNumber())).thenReturn(Optional.empty());

        SmsAuthEntity existingSmsAuth = new SmsAuthEntity();
        existingSmsAuth.setSmsAuthNumber("1234");
        existingSmsAuth.setPhoneNumber(requestDto.getPhoneNumber());
        existingSmsAuth.setExpirationTime(LocalDateTime.now().plusMinutes(5L));
        existingSmsAuth.setSmsAuthType(SMS_AUTH_CODE.BUILDER_JOIN.getValue());
        given(smsAuthRepository.findValidSmsAuthByPhoneNumberAndType(any(), any(), any())).willReturn(existingSmsAuth);

        // when
        SmsAuthNumberPostResponse response = builderManagementService.sendBuilderUserJoinSmsAuthNumber(requestDto);

        // then
        assertEquals("유효시간이 만료되지 않은 인증번호가 존재합니다. \n인증번호를 확인하거나, 유효시간이 지난 후 다시 시도해주세요.", response.getMessage());
    }

    @Test
    @DisplayName(value = "시공사 회원가입 - SMS 인증번호 전송 실패 테스트 (#4. SMS 인증번호 생성 중 오류 발생)")
    @Transactional
    void sendBuilderUserJoinSmsAuthNumberFailureTestSmsAuthCreationError() throws Exception {

        log.info("## test4 시작 ##");

        // given
        SmsAuthNumberPostRequest requestDto = new SmsAuthNumberPostRequest("01094342762");

        when(userRepository.findByPhoneNumberAndIsUseIsTrue(requestDto.getPhoneNumber())).thenReturn(Optional.empty());
        when(builderUserRepository.findByPhoneNumberAndIsUseIsTrue(requestDto.getPhoneNumber())).thenReturn(Optional.empty());
        when(smsAuthRepository.findValidSmsAuthByPhoneNumberAndType(any(), any(), any())).thenReturn(null);

        doThrow(new RuntimeException("SMS 인증번호를 생성하던 중 오류가 발생하였습니다."))
                .when(smsAuthRepository).save(any());

        // when
        Exception exception = assertThrows(BadRequestException.class, () -> {
            builderManagementService.sendBuilderUserJoinSmsAuthNumber(requestDto);
        });

        // then
        assertEquals("SMS 인증번호를 생성하던 중 오류가 발생하였습니다.", exception.getMessage());
    }

}
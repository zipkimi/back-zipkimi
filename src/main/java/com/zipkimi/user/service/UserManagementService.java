package com.zipkimi.user.service;

import com.zipkimi.entity.SmsAuthEntity;
import com.zipkimi.entity.UserEntity;
import com.zipkimi.global.service.SmsService;
import com.zipkimi.repository.SmsAuthRepository;
import com.zipkimi.repository.UserRepository;
import com.zipkimi.user.dto.request.SmsAuthNumberPostRequest;
import com.zipkimi.user.dto.response.SmsAuthNumberPostResponse;
import java.util.Optional;
import java.util.Random;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class UserManagementService {
    private final UserRepository userRepository;
    private final SmsAuthRepository smsAuthRepository;
    private final SmsService smsService;
    private final Random random = new Random();

    public SmsAuthNumberPostResponse sendSmsAuthNumber(SmsAuthNumberPostRequest requestDto) {
        // 휴대폰 번호 유효성 검사 - 타입, 글자수, 이미 등록된 번호인지 체크
        Optional<UserEntity> userEntityOptional = userRepository.findByPhoneNumberAndIsUseIsTrue(
                requestDto.getPhoneNumber());
        if (userEntityOptional.isPresent()) {
            return SmsAuthNumberPostResponse.builder()
                    .result("이미 등록된 휴대폰 번호입니다.")
                    .build();
        }
        // 난수 발생
        String randomNumber = String.valueOf(random.nextInt(9999));
        // DB table 에 insert
        SmsAuthEntity smsAuth = new SmsAuthEntity();
        smsAuth.setPhoneNumber(requestDto.getPhoneNumber());
        smsAuth.setSmsAuthNumber(randomNumber);
        smsAuth.setIsAuthenticate(false);
        smsAuth.setContent("본인확인 인증번호 (" + smsAuth.getSmsAuthNumber() + ")입력시 \n"
                + "정상처리 됩니다.");
        SmsAuthEntity smsAuthEntitySaved = smsAuthRepository.save(smsAuth);
        // SMS 전송 로직
        smsService.pushSMSMessage(smsAuthEntitySaved);
        return SmsAuthNumberPostResponse.builder()
                .result("인증번호를 전송하였습니다.")
                .build();
    }
}

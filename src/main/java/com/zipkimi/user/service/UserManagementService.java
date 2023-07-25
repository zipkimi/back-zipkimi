package com.zipkimi.user.service;

import com.zipkimi.dto.request.SmsAuthNumberGetRequest;
import com.zipkimi.dto.response.SmsAuthNumberGetResponse;
import com.zipkimi.entity.SmsAuthEntity;
import com.zipkimi.entity.UserEntity;
import com.zipkimi.global.exception.BadRequestException;
import com.zipkimi.global.service.SmsService;
import com.zipkimi.repository.SmsAuthRepository;
import com.zipkimi.repository.UserRepository;
import com.zipkimi.user.dto.request.SmsAuthNumberPostRequest;
import com.zipkimi.user.dto.response.SmsAuthNumberPostResponse;
import java.time.LocalDateTime;
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
        String phoneNumber = requestDto.getPhoneNumber().replaceAll("\\D", "");
        if (phoneNumber.length() != 11) {
            return SmsAuthNumberPostResponse.builder()
                    .message("입력한 휴대전화 번호를 확인해주세요.")
                    .build();
        }
        Optional<UserEntity> userEntityOptional = userRepository.findByPhoneNumberAndIsUseIsTrue(phoneNumber);
        if (userEntityOptional.isPresent()) {
            return SmsAuthNumberPostResponse.builder()
                    .message("이미 등록된 휴대폰 번호입니다.")
                    .build();
        }
        // 난수 발생
        String randomNumber = String.valueOf(random.nextInt(9999));
        // DB table 에 insert
        SmsAuthEntity smsAuth = new SmsAuthEntity();
        smsAuth.setPhoneNumber(requestDto.getPhoneNumber());
        smsAuth.setSmsAuthNumber(randomNumber);
        smsAuth.setIsAuthenticate(false);
        // TODO Type 값 설정 필요
        smsAuth.setExpirationTime(LocalDateTime.now().plusMinutes(5L));
        smsAuth.setContent("본인확인 인증번호 (" + smsAuth.getSmsAuthNumber() + ")입력시 \n"
                + "정상처리 됩니다.");
        SmsAuthEntity smsAuthEntitySaved = smsAuthRepository.save(smsAuth);
        // SMS 전송 로직
        smsService.pushSMSMessage(smsAuthEntitySaved);
        return SmsAuthNumberPostResponse.builder()
                .message("인증번호를 전송하였습니다.")
                .smsAuthId(smsAuthEntitySaved.getSmsAuthId())
                .build();
    }

    public SmsAuthNumberGetResponse checkSmsAuthNumber(SmsAuthNumberGetRequest requestDto) {
        String message = "본인 인증에 성공했습니다.";
        Optional<SmsAuthEntity> optionalSmsAuth = smsAuthRepository.findById(
                requestDto.getSmsAuthId());
        if (optionalSmsAuth.isEmpty()) {
            throw new BadRequestException("인증번호를 확인해주세요.");
        }
        SmsAuthEntity smsAuth = optionalSmsAuth.get();
        // 이미 만료된 인증번호
        if (smsAuth.getExpirationTime().isBefore(LocalDateTime.now())) {
            message = "인증번호 유효시간이 만료됐습니다. 재전송해주세요.";
        }
        // 일치하지 않는 인증번호
        if(!requestDto.getSmsAuthNumber().equals(smsAuth.getSmsAuthNumber())){
            message = "인증번호를 확인해주세요.";
        }
        return SmsAuthNumberGetResponse.builder().message(message).build();
    }
}

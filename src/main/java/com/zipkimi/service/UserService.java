package com.zipkimi.service;

import com.zipkimi.common.sms.SmsMessage;
import com.zipkimi.dto.request.SmsAuthNumberPostRequest;
import com.zipkimi.dto.response.SmsAuthNumberPostResponse;
import com.zipkimi.dto.response.SmsPostResponse;
import com.zipkimi.entity.SmsAuthEntity;
import com.zipkimi.entity.UserEntity;
import com.zipkimi.repository.SmsAuthRepository;
import com.zipkimi.repository.UserRepository;
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
public class UserService {
    private final UserRepository userRepository;
    private final SmsAuthRepository smsAuthRepository;
    private final  SmsService smsService;

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
        String randomNumber = String.valueOf(new Random().nextInt(9999));
        // DB table 에 insert
        SmsAuthEntity smsAuth = new SmsAuthEntity();
        smsAuth.setPhoneNumber(requestDto.getPhoneNumber());
        smsAuth.setSmsAuthNumber(randomNumber);
        smsAuth.setIsAuthenticate(false);
        SmsAuthEntity smsAuthEntitySaved = smsAuthRepository.save(smsAuth);
        // SMS 전송 로직
        pushSMSMessage(smsAuthEntitySaved);
        return SmsAuthNumberPostResponse.builder()
                .result("인증번호를 전송하였습니다.")
                .build();
    }

    private SmsPostResponse pushSMSMessage(SmsAuthEntity smsAuth){
        SmsMessage message = SmsMessage.builder()
                .to(smsAuth.getPhoneNumber())
                .content("본인확인 인증번호 (" + smsAuth.getSmsAuthNumber() + ")입력시 \n"
                        + "정상처리 됩니다.")
                .build();
        SmsPostResponse response = null;
        try{
            response = smsService.send(message);
        }catch (Exception e){
//          TODO Exception 수정  throw new BadRequestException("인증번호 전송을 실패했습니다.");
        }
        return response;
    }
}

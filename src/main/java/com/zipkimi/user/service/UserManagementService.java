package com.zipkimi.user.service;

import static com.zipkimi.global.utils.RegexUtils.getFormatName;
import static com.zipkimi.global.utils.RegexUtils.isValidEmail;
import static com.zipkimi.global.utils.RegexUtils.isValidName;
import static com.zipkimi.global.utils.RegexUtils.isValidPassword;
import static com.zipkimi.global.utils.RegexUtils.isValidPhoneNumber;

import com.zipkimi.entity.SmsAuthEntity;
import com.zipkimi.entity.UserEntity;
import com.zipkimi.global.exception.BadRequestException;
import com.zipkimi.global.service.SmsService;
import com.zipkimi.repository.SmsAuthRepository;
import com.zipkimi.repository.UserRepository;
import com.zipkimi.user.dto.request.JoinUserPostRequest;
import com.zipkimi.user.dto.request.SmsAuthNumberGetRequest;
import com.zipkimi.user.dto.request.SmsAuthNumberPostRequest;
import com.zipkimi.user.dto.response.JoinUserPostResponse;
import com.zipkimi.user.dto.response.SmsAuthNumberGetResponse;
import com.zipkimi.user.dto.response.SmsAuthNumberPostResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class UserManagementService {
    private final UserRepository userRepository;
    private final SmsAuthRepository smsAuthRepository;
    private final SmsService smsService;
    private final PasswordEncoder passwordEncoder;
    private final Random random = new Random();

    public JoinUserPostResponse joinUser(JoinUserPostRequest requestDto) {
        Optional<UserEntity> optionalUser = userRepository.findByEmailAndIsUseIsTrue(requestDto.getEmail());
        if(optionalUser.isPresent()){
            throw new BadRequestException("이미 사용중인 email 입니다.");
        }
        if(!isValidEmail(requestDto.getEmail())){
            throw new BadRequestException("이메일 주소를 확인해주세요.");
        }
        String name = getFormatName(requestDto.getName());
        if(!isValidName(name)){
            throw new BadRequestException("이름을 확인해주세요.");
        }
        if(!isValidPassword(requestDto.getPw())){
            throw new BadRequestException("패스워드 형식을 확인해주세요.");
        }
        Optional<SmsAuthEntity> optionalSmsAuth = smsAuthRepository.findById(requestDto.getSmsAuthId());
        if(optionalSmsAuth.isEmpty() || !optionalSmsAuth.get().getIsAuthenticate()){
            throw new BadRequestException("인증번호 전송부터 다시 진행해주세요.");
        }

        UserEntity user = UserEntity.builder()
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPw()))
                .name(name)
                .phoneNumber(optionalSmsAuth.get().getPhoneNumber())
                .build();
        userRepository.save(user);
        return JoinUserPostResponse.builder()
                .userId(user.getUserId())
                .name(name)
                .message("회원가입 완료")
                .build();
    }

    public SmsAuthNumberPostResponse sendSmsAuthNumber(SmsAuthNumberPostRequest requestDto) {
        // 휴대폰 번호 유효성 검사 - 타입, 글자수, 이미 등록된 번호인지 체크
        String phoneNumber = requestDto.getPhoneNumber().replaceAll("\\D", ""); // "\\D" 정규식을 사용하여 숫자이외의 문자는 모두 ""으로 변경
        if (phoneNumber == null || !isValidPhoneNumber(phoneNumber)) {
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
        // 이전 전송 이력이 있는 경우 -> 이전이력 isUse false 처리후 새로 insert
        List<SmsAuthEntity> smsAuthEntities = smsAuthRepository.findByPhoneNumberAndIsAuthenticateFalseAndIsUseTrue(
                phoneNumber);
        for (SmsAuthEntity entity : smsAuthEntities) {
            entity.setIsUse(false);
            smsAuthRepository.save(entity);
        }
        // 난수 발생
        String randomNumber = String.valueOf(random.nextInt(9999));
        // DB table 에 insert
        SmsAuthEntity smsAuth = new SmsAuthEntity();
        smsAuth.setPhoneNumber(requestDto.getPhoneNumber());
        smsAuth.setSmsAuthNumber(randomNumber);
        smsAuth.setIsAuthenticate(false);
        smsAuth.setIsUse(true);
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
            throw new BadRequestException("인증번호 유효시간이 만료됐습니다. 재전송해주세요.");
        }
        // 일치하지 않는 인증번호
        if(!requestDto.getSmsAuthNumber().equals(smsAuth.getSmsAuthNumber())){
            throw new BadRequestException("인증번호를 확인해주세요.");
        }
        return SmsAuthNumberGetResponse.builder().message(message).build();
    }
}

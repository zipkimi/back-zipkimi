package com.zipkimi.user.service;

import com.zipkimi.global.exception.BadRequestException;
import com.zipkimi.global.service.SmsService;
import com.zipkimi.global.utils.CodeConstant.SMS_AUTH_CODE;
import com.zipkimi.user.dto.request.FindIdCheckSmsGetRequest;
import com.zipkimi.user.dto.request.FindPwCheckSmsGetRequest;
import com.zipkimi.user.dto.request.PassResetSmsAuthNumberPostRequest;
import com.zipkimi.user.dto.request.SmsAuthNumberPostRequest;
import com.zipkimi.user.dto.response.FindSmsAuthNumberGetResponse;
import com.zipkimi.user.dto.response.FindSmsAuthNumberPostResponse;
import com.zipkimi.entity.SmsAuthEntity;
import com.zipkimi.entity.UserEntity;
import com.zipkimi.repository.SmsAuthRepository;
import com.zipkimi.repository.UserRepository;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class UserLoginService {

    //임시 비밀번호 발급
    private static final String CHAR_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom secureRandom = new SecureRandom();
    private final UserRepository userRepository;
    private final SmsAuthRepository smsAuthRepository;
    private final SmsService smsService;
    private final Random random = new Random();

    // ************* 아이디 찾기 *************

    // 아이디 찾기 - SMS 인증번호 검증 & 휴대폰 번호로 가입된 회원 이메일 조회
    public FindSmsAuthNumberGetResponse checkFindIdSmsAuth(FindIdCheckSmsGetRequest requestDto) {

        // #1. SMS 인증번호를 ID를 통해 DB 데이터 검증 후
        Optional<SmsAuthEntity> smsAuth = smsAuthRepository.findById(requestDto.getSmsAuthId());

        if (smsAuth.isEmpty() || !smsAuth.get().getSmsAuthNumber()
                .equals(requestDto.getSmsAuthNumber())) {
            return FindSmsAuthNumberGetResponse
                    .builder()
                    .message("SMS 인증에 실패하였습니다. \n인증번호를 정상적으로 입력해주세요.")
                    .build();
        }

        // 2. SMS 인증번호 만료시간 검증
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime expirationTime = smsAuth.get().getExpirationTime();

        if (currentTime.isAfter(expirationTime)) {
            return FindSmsAuthNumberGetResponse
                    .builder()
                    .message("인증번호가 만료되었습니다. \n다시 인증번호를 발급받아주세요.")
                    .build();
        }

        if (Boolean.TRUE.equals(smsAuth.get().getIsUse())) {
            return FindSmsAuthNumberGetResponse
                    .builder()
                    .message("이미 사용된 인증번호입니다. \n다시 인증번호를 발급받아주세요.")
                    .build();
        }

        // SMS 인증번호를 여러 번 사용하는 것 방지
        smsAuth.get().setIsUse(true);
        smsAuthRepository.save(smsAuth.get());

        //3. 휴대폰 번호로 일치하는 회원 조회
        Optional<UserEntity> user = userRepository.findByPhoneNumber(requestDto.getPhoneNumber());
        String email = user.map(UserEntity::getEmail).orElse(null);

        if (email != null) {
            // 휴대폰 번호로 가입된 회원이 존재할 경우 : Email 반환
            return FindSmsAuthNumberGetResponse
                    .builder()
                    .message("회원님의 아이디는 '" + email + "' 입니다.")
                    .build();
        } else {
            // 휴대폰 번호로 가입된 회원이 존재하지 않을 경우 : 고객센터 문의 요망 반환 안내
            return FindSmsAuthNumberGetResponse
                    .builder()
                    .message("입력하신 휴대폰 번호와 일치하는 아이디 정보가 없습니다. \n(고객센터 문의 요망)")
                    .build();
        }
    }

    // 아이디 찾기 - SMS 인증번호 전송
    public FindSmsAuthNumberPostResponse sendFindIdSmsAuthNumber(
            SmsAuthNumberPostRequest requestDto) {

        // 휴대폰 번호로 일치하는 회원 조회
        Optional<UserEntity> user = userRepository.findByPhoneNumber(requestDto.getPhoneNumber());

        if (user.isEmpty()) {
            return FindSmsAuthNumberPostResponse.builder()
                    .message("입력하신 휴대폰 번호와 일치하는 정보가 없습니다. \n(고객센터 문의 요망)")
                    .build();
        }

        // 만료되지 않은 인증번호 있는지 조회
        SmsAuthEntity existingSmsAuth =
                smsAuthRepository.findValidSmsAuthByPhoneNumberAndType(requestDto.getPhoneNumber(),
                    LocalDateTime.now(), SMS_AUTH_CODE.FIND_ID.getValue());

        String randomNumber;
        SmsAuthEntity smsAuthEntitySaved = null;

        if (existingSmsAuth != null) {
            // SMS 인증번호가 만료되지 않았을 경우
            existingSmsAuth.setExpirationTime(LocalDateTime.now().plusMinutes(5L));
            smsAuthRepository.save(existingSmsAuth);
        } else {
            // 인증번호 생성 : 4자리(중복 x)
            randomNumber = generateNumber(4, 2);

            SmsAuthEntity smsAuth = new SmsAuthEntity();
            smsAuth.setPhoneNumber(requestDto.getPhoneNumber());
            smsAuth.setSmsAuthNumber(randomNumber);
            smsAuth.setIsAuthenticate(false);
            smsAuth.setExpirationTime(LocalDateTime.now().plusMinutes(5L));
            smsAuth.setSmsAuthType(SMS_AUTH_CODE.FIND_ID.getValue());

            // SMS 내용 설정
            smsAuth.setContent("[집킴이] 아이디 찾기 인증번호는 [" + randomNumber + "] 입니다. 인증번호를 정확히 입력해주세요.");

            try {
                // DB 테이블에 insert
                smsAuthEntitySaved = smsAuthRepository.save(smsAuth);

                // SMS 전송 로직
                smsService.pushSMSMessage(smsAuthEntitySaved);
            } catch (Exception e) {
                throw new BadRequestException("SMS 인증번호를 생성하던 중 오류가 발생하였습니다.");
            }
        }

        return FindSmsAuthNumberPostResponse.builder()
                .message("인증번호를 전송하였습니다.")
                .build();
    }

    // ************* 비밀번호 찾기 *************

    // 비밀번호 찾기 - SMS 인증번호 전송
    public FindSmsAuthNumberPostResponse sendFindPwSmsAuthNumber(
            PassResetSmsAuthNumberPostRequest requestDto) {

        // 휴대폰 번호로 가입된 회원 확인
        Optional<UserEntity> user = userRepository.findByPhoneNumber(requestDto.getPhoneNumber());

        if (user.isEmpty()) {
            return FindSmsAuthNumberPostResponse.builder()
                    .message("입력하신 휴대폰 번호와 일치하는 정보가 없습니다. \n(고객센터 문의 요망)")
                    .build();
        }

        // 만료되지 않은 SMS 인증번호 있는지 조회
        SmsAuthEntity existingSmsAuth =
                smsAuthRepository.findValidSmsAuthByPhoneNumberAndType(requestDto.getPhoneNumber(),
                LocalDateTime.now(), SMS_AUTH_CODE.FIND_PW.getValue());

        String randomNumber;
        SmsAuthEntity smsAuthEntitySaved = null;

        if (existingSmsAuth != null) {
            // SMS 인증번호가 만료되지 않았을 경우
            existingSmsAuth.setExpirationTime(LocalDateTime.now().plusMinutes(5L));
            smsAuthRepository.save(existingSmsAuth);
        } else {
            // 인증번호 생성 : 4자리(중복 x)
            randomNumber = generateNumber(4, 2);

            // DB 테이블에 insert
            SmsAuthEntity smsAuth = new SmsAuthEntity();
            smsAuth.setPhoneNumber(requestDto.getPhoneNumber());
            smsAuth.setSmsAuthNumber(randomNumber);
            smsAuth.setIsAuthenticate(false);
            smsAuth.setExpirationTime(LocalDateTime.now().plusMinutes(5L));
            smsAuth.setSmsAuthType(SMS_AUTH_CODE.FIND_PW.getValue());

            // SMS 내용 설정
            smsAuth.setContent(
                    "[집킴이] 비밀번호 찾기 인증번호는 [" + randomNumber + "] 입니다. 인증번호를 정확히 입력해주세요.");

            try {
                // DB 테이블에 insert
                smsAuthEntitySaved = smsAuthRepository.save(smsAuth);

                // SMS 전송 로직
                smsService.pushSMSMessage(smsAuthEntitySaved);
            } catch (Exception e) {
                throw new BadRequestException("SMS 인증번호를 생성하던 중 오류가 발생하였습니다.");
            }
        }

        return FindSmsAuthNumberPostResponse.builder()
                .message("인증번호를 전송하였습니다.")
                .build();
    }

    // 비밀번호 찾기 - SMS 인증번호 검증 & 이메일과 휴대폰 번호로 가입 회원 조회
    public FindSmsAuthNumberGetResponse checkFindPwSmsAuth(FindPwCheckSmsGetRequest requestDto) {

        // #1. SMS 인증번호를 ID를 통해 DB 데이터 검증 후
        Optional<SmsAuthEntity> smsAuth = smsAuthRepository.findById(requestDto.getSmsAuthId());

        if (smsAuth.isEmpty() || !smsAuth.get().getSmsAuthNumber()
                .equals(requestDto.getSmsAuthNumber())) {
            return FindSmsAuthNumberGetResponse
                    .builder()
                    .message("SMS 인증에 실패하였습니다. \n인증번호를 정상적으로 입력해주세요.")
                    .build();
        }

        // 2. SMS 인증번호 만료시간 검증
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime expirationTime = smsAuth.get().getExpirationTime();

        if (currentTime.isAfter(expirationTime)) {
            return FindSmsAuthNumberGetResponse
                    .builder()
                    .message("인증번호가 만료되었습니다. \n다시 인증번호를 발급받아주세요.")
                    .build();
        }

        if (Boolean.TRUE.equals(smsAuth.get().getIsUse())) {
            return FindSmsAuthNumberGetResponse
                    .builder()
                    .message("이미 사용된 인증번호입니다. \n다시 인증번호를 발급받아주세요.")
                    .build();
        }

        // SMS 인증번호를 여러 번 사용하는 것 방지
        smsAuth.get().setIsUse(true);
        smsAuthRepository.save(smsAuth.get());

        // 3. 이메일과 휴대폰 번호로 가입 회원 조회
        Optional<UserEntity> user = userRepository.findByPhoneNumberAndEmail(
                requestDto.getPhoneNumber(), requestDto.getEmail());

        // 2-1. 임시 비밀번호 생성
        String newPassword = tempPassword(10);

        // 2.2 회원이 존재한다면, 생성한 임시비밀번호로 비밀번호 초기화(업데이트)
        if (user.isPresent()) {
            UserEntity userEntity = user.get();
            userEntity.setPassword(newPassword);

            return FindSmsAuthNumberGetResponse
                    .builder()
                    .message("비밀번호가 '" + newPassword + "'로 초기화 되었습니다.")
                    .build();
        } else {
            return FindSmsAuthNumberGetResponse
                    .builder()
                    .message("입력하신 휴대폰 번호와 이메일 정보가 일치하는 사용자가 없습니다.")
                    .build();
        }

    }

    // ************* 공통 로직 *************

    //난수로 인증번호 생성
    public String generateNumber(int len, int dupCd) {

        //난수가 저장될 변수
        StringBuilder numStr = new StringBuilder();

        for (int i = 0; i < len; i++) {

            //0~9 까지 난수 생성
            String ran = Integer.toString(random.nextInt(10));

            if (dupCd == 1) {
                //중복 허용시 numStr 변수에 append
                numStr.append(ran);
            } else if (dupCd == 2) {
                //중복을 허용하지 않을시 중복된 값이 있는지 검사한다
                if (!numStr.toString().contains(ran)) {
                    //중복된 값이 없으면 numStr 변수에  append
                    numStr.append(ran);
                } else {
                    //생성된 난수가 중복되면 루틴을 다시 실행한다
                    i -= 1;
                }
            }
        }
        return numStr.toString();
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

package com.zipkimi.user.service;

import com.zipkimi.global.service.SmsService;
import com.zipkimi.user.dto.request.PassResetSmsAuthNumberPostRequest;
import com.zipkimi.user.dto.request.SmsAuthNumberPostRequest;
import com.zipkimi.user.dto.response.SendFindSmsAuthNumberPostResponse;
import com.zipkimi.entity.SmsAuthEntity;
import com.zipkimi.entity.UserEntity;
import com.zipkimi.repository.SmsAuthRepository;
import com.zipkimi.repository.UserRepository;
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

    private final UserRepository userRepository;
    private final SmsAuthRepository smsAuthRepository;
    private final SmsService smsService;
    private final Random random = new Random();

    // ************* 아이디 찾기 *************
    
    // 휴대폰 번호로 가입된 회원 이메일 조회
    public String getEmailByPhoneNumber(String phoneNumber) {

        // 휴대폰 번호로 일치하는 회원 조회
        UserEntity user = userRepository.findByPhoneNumber(phoneNumber);

        // 휴대폰 번호로 가입된 회원이 존재할 경우 : Email 반환
        // 휴대폰 번호로 가입된 회원이 존재하지 않을 경우 : null 반환
        return user != null ? user.getEmail() : null;
    }

    // SMS 인증번호 검증
    public boolean verifySmsAuth(String phoneNumber, String smsAuthNumber){
        SmsAuthEntity smsAuth = smsAuthRepository.findByPhoneNumberAndSmsAuthNumber(phoneNumber, smsAuthNumber);
        return smsAuth != null;
    }

    // SMS 인증번호 전송
    public SendFindSmsAuthNumberPostResponse sendFindIdSmsAuthNumber(
            SmsAuthNumberPostRequest requestDto) {

        // 휴대폰 번호로 일치하는 회원 조회
        UserEntity user = userRepository.findByPhoneNumber(requestDto.getPhoneNumber());

        if (user == null) {
            return SendFindSmsAuthNumberPostResponse.builder()
                    .result("입력하신 휴대폰 번호와 일치하는 정보가 없습니다. \n(고객센터 문의 요망)")
                    .build();
        }

        // 인증번호 생성 : 4자리(중복 x)
        String randomNumber = numberGen(4, 2);

        SmsAuthEntity smsAuth = new SmsAuthEntity();
        smsAuth.setPhoneNumber(requestDto.getPhoneNumber());
        smsAuth.setSmsAuthNumber(randomNumber);
        smsAuth.setIsAuthenticate(false);

        // SMS 내용 설정
        smsAuth.setContent("[집킴이] 아이디 찾기 인증번호는 [" + randomNumber + "] 입니다. 인증번호를 정확히 입력해주세요.");

        // DB 테이블에 insert
        SmsAuthEntity smsAuthEntitySaved = smsAuthRepository.save(smsAuth);

        // SMS 전송 로직
        smsService.pushSMSMessage(smsAuthEntitySaved);

        return SendFindSmsAuthNumberPostResponse.builder()
                .result("인증번호를 전송하였습니다.")
                .build();
    }

    // ************* 비밀번호 찾기 *************

    // 휴대폰 번호와 이메일로 가입된 회원 조회
    public String getUserByPhoneNumberAndEmail(String phoneNumber, String email) {

        // 휴대폰 번호와 이메일로 일치하는 user 조회
        Optional<UserEntity> user = userRepository.findByPhoneNumberAndEmail(phoneNumber, email);

        // 휴대폰 번호와 이메일로 가입된 회원이 존재할 경우 : Email 반환
        // 휴대폰 번호와 이메일로 가입된 회원이 존재하지 않을 경우 : null 반환
        //TODO 수정 필요
        return user.isPresent() ? user.get().getEmail() : null;
    }
    
    //임시 비밀번호로 비밀번호 업데이트
    public void updatePassword(String email, String newPassword){
        UserEntity user = userRepository.findByEmail(email);
        user.setPassword(newPassword);
    }

    // SMS 인증번호 전송
    public SendFindSmsAuthNumberPostResponse sendFindPwSmsAuthNumber(
            PassResetSmsAuthNumberPostRequest requestDto) {

            // 휴대폰 번호로 가입된 회원 확인
            UserEntity user = userRepository.findByPhoneNumber(requestDto.getPhoneNumber());

            if (user == null) {
                return SendFindSmsAuthNumberPostResponse.builder()
                        .result("등록되지 않은 회원입니다.")
                        .build();
            }

            // 인증번호 생성 : 4자리(중복 x)
            String randomNumber = numberGen(4, 2);

            // DB 테이블에 insert
            SmsAuthEntity smsAuth = new SmsAuthEntity();
            smsAuth.setPhoneNumber(requestDto.getPhoneNumber());
            smsAuth.setSmsAuthNumber(randomNumber);
            smsAuth.setIsAuthenticate(false);

            // SMS 내용 설정
            smsAuth.setContent("[집킴이] 비밀번호 찾기 인증번호는 [" + randomNumber + "] 입니다. 인증번호를 정확히 입력해주세요.");

            SmsAuthEntity smsAuthEntitySaved = smsAuthRepository.save(smsAuth);

            // SMS 전송 로직
            smsService.pushSMSMessage(smsAuthEntitySaved);

            return SendFindSmsAuthNumberPostResponse.builder()
                    .result("인증번호를 전송하였습니다.")
                    .build();
        }


    // ************* 공통 *************


    //난수로 인증번호 생성
    public String numberGen(int len, int dupCd ) {

        //난수가 저장될 변수
        String numStr = "";

        for(int i=0;i<len;i++) {

            //0~9 까지 난수 생성
            String ran = Integer.toString(random.nextInt(10));

            if(dupCd==1) {
                //중복 허용시 numStr에 append
                numStr += ran;
            }else if(dupCd==2) {
                //중복을 허용하지 않을시 중복된 값이 있는지 검사한다
                if(!numStr.contains(ran)) {
                    //중복된 값이 없으면 numStr에 append
                    numStr += ran;
                }else {
                    //생성된 난수가 중복되면 루틴을 다시 실행한다
                    i-=1;
                }
            }
        }
        return numStr;
    }

    //임시 비밀번호 생성
    public String tempPassword(int len){
        int index = 0;
        char[] charSet = new char[] {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
                'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
        };    //배열안의 문자 숫자는 원하는대로

        StringBuilder password = new StringBuilder();

        for (int i = 0; i < len ; i++) {
            double rd = random.nextDouble();
            index = (int) (charSet.length * rd);

            password.append(charSet[index]);

            System.out.println("index::" + index + " charSet::"+ charSet[index]);
        }

        //StringBuilder를 String으로 변환해서 return 하려면 toString()을 사용하면 된다.
        return password.toString();
    }
}

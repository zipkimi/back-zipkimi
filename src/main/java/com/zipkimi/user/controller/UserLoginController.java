package com.zipkimi.user.controller;

import com.zipkimi.user.dto.request.FindPwVerifySmsRequest;
import com.zipkimi.user.dto.request.PassResetSmsAuthNumberPostRequest;
import com.zipkimi.user.dto.request.FindIdVerifySmsRequest;
import com.zipkimi.user.dto.request.SmsAuthNumberPostRequest;
import com.zipkimi.user.dto.response.SendFindSmsAuthNumberPostResponse;
import com.zipkimi.user.service.UserLoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@Api(value = "로그인")
public class UserLoginController {

    private UserLoginService loginService;

    // ************* 아이디 찾기 *************

    @ApiOperation(value = "아이디 찾기 - SMS 인증번호 전송")
    @PostMapping(value = "/api/v1/users/find-id/auth/sms/send")
    public ResponseEntity<SendFindSmsAuthNumberPostResponse> sendFindIdSmsAuthNumber(@RequestBody @Validated SmsAuthNumberPostRequest requestDto) {
        SendFindSmsAuthNumberPostResponse response = loginService.sendFindIdSmsAuthNumber(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @ApiOperation(value = "아이디 찾기 - SMS 인증번호 확인 및 아이디 찾기")
    @PostMapping(value = "/api/v1/users/find-id/auth/sms/verify", produces = "application/json; charset=utf8")
    public ResponseEntity<String> verifySmsAuthAndFindId(@RequestBody FindIdVerifySmsRequest requestDto){
        
        // 0. 입력받은 휴대폰번호와 인증번호
        String phoneNumber = requestDto.getPhoneNumber();
        String smsAuthNumber = requestDto.getSmsAuthNumber();

        // 1. SMS 인증번호 DB 데이터 검증하기
        boolean isVerified = loginService.verifySmsAuth(phoneNumber, smsAuthNumber);

        if (!isVerified) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("SMS 인증에 실패하였습니다. \n인증번호를 정상적으로 입력해주세요.");
        }

        // 2. 휴대폰번호로 아이디 찾기
        String email = loginService.getEmailByPhoneNumber(phoneNumber);

        if (email != null) {
            return ResponseEntity.ok("회원님의 아이디는 '" + email + "' 입니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("입력하신 휴대폰 번호와 일치하는 정보가 없습니다. \n(고객센터 문의 요망)");
        }

    }

    // ************* 비밀번호 찾기 *************

    @ApiOperation(value = "비밀번호 찾기 - SMS 인증번호 전송")
    @PostMapping(value = "/api/v1/users/find-pw/auth/sms/send")
    public ResponseEntity<SendFindSmsAuthNumberPostResponse> sendFindPwSmsAuthNumber(@RequestBody @Validated PassResetSmsAuthNumberPostRequest requestDto) {
        SendFindSmsAuthNumberPostResponse response = loginService.sendFindPwSmsAuthNumber(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @ApiOperation(value = "비밀번호 찾기 - SMS 인증번호 확인 및 비밀번호 초기화")
    @PostMapping(value = "/api/v1/users/find-pw/auth/sms/verify", produces = "application/json; charset=utf8")
    public ResponseEntity<String> verifySmsAuthAndResetPw(@RequestBody FindPwVerifySmsRequest requestDto) {

        // 0. 입력받은 휴대폰번호와 인증번호, 이메일
        String phoneNumber = requestDto.getPhoneNumber();
        String smsAuthNumber = requestDto.getSmsAuthNumber();
        String email = requestDto.getEmail();

        // 1. SMS 인증번호 확인
        boolean isVerified = loginService.verifySmsAuth(phoneNumber, smsAuthNumber);

        if (!isVerified) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("SMS 인증에 실패하였습니다.");
        }

        // 2. 이메일과 휴대폰 번호로 가입 회원 조회
        String user = loginService.getUserByPhoneNumberAndEmail(phoneNumber, email);

        if (user != null) {
            // 3. 임시 비밀번호 생성
            String resetPassword = loginService.tempPassword(10);
            
            // 4. 임시 비밀번호로 비밀번호 업데이트
            loginService.updatePassword(email, resetPassword);
            return ResponseEntity.ok("비밀번호가 '" + resetPassword + "'로 초기화 되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("입력하신 휴대폰 번호와 이메일 정보가 일치하는 사용자가 없습니다.");
        }

    }

}

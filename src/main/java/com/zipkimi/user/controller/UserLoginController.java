package com.zipkimi.user.controller;

import com.zipkimi.global.dto.response.BaseResponse;
import com.zipkimi.global.jwt.dto.request.TokenRequest;
import com.zipkimi.global.jwt.dto.response.TokenResponse;
import com.zipkimi.user.dto.request.FindIdCheckSmsGetRequest;
import com.zipkimi.user.dto.request.FindPwCheckSmsGetRequest;
import com.zipkimi.user.dto.request.PassResetSmsAuthNumberPostRequest;
import com.zipkimi.user.dto.request.SmsAuthNumberPostRequest;
import com.zipkimi.user.dto.request.UserLoginRequest;
import com.zipkimi.user.dto.response.FindSmsAuthNumberGetResponse;
import com.zipkimi.user.dto.response.FindSmsAuthNumberPostResponse;
import com.zipkimi.user.service.UserLoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@Api(value = "로그인")
@RequestMapping("/api/v1/users")
public class UserLoginController {

    private UserLoginService loginService;

    //TODO 일반회원가입 로직 완성 후 삭제 예정
    // ************* 로그인 테스트를 위한 일반 회원가입 테스트 *************
    @ApiOperation(value = "일반 회원가입 테스트", notes = "일반 회원가입 테스트입니다.")
    @PostMapping(value = "/auth/sign")
    public ResponseEntity<BaseResponse> sign(@RequestBody UserLoginRequest userLoginRequest) {

        // 로그인 시 JWT 토큰이 잘 이루어지는지 테스트 하기 위한 간단한 일반 회원가입 테스트
        BaseResponse baseResponse = loginService.simpleJoinTest(userLoginRequest);

        return ResponseEntity.status(HttpStatus.OK).body(baseResponse);
    }
    
    // ************* 로그인 *************

    @ApiOperation(value = "로그인", notes = "이메일로 로그인합니다.")
    @PostMapping(value = "/auth/login")
    public ResponseEntity<TokenResponse> login(@RequestBody UserLoginRequest userLoginRequest) {

        TokenResponse tokenResponse = loginService.login(userLoginRequest);
        return ResponseEntity.status(HttpStatus.OK).body(tokenResponse);
    }

    @ApiOperation(value = "로그아웃", notes = "로그아웃 합니다.")
    @DeleteMapping(value = "/auth/logout")
    public ResponseEntity<TokenResponse> logout(@RequestBody TokenRequest tokenRequest){
        log.info("============================= Controller 로그아웃 메서드 실행 ");
        TokenResponse tokenResponse = loginService.logout(tokenRequest.getRefreshToken());
        return ResponseEntity.status(HttpStatus.OK).body(tokenResponse);
    }

    @ApiOperation(value = "토큰 재발급 (accessToken, refreshToken 재발급) ",
            notes = "accessToken 만료시 회원 검증 후 refreshToken을 검증해서 accessToken, refreshToken을 재발급합니다.")
    @PostMapping(value = "/auth/reissue")
    public ResponseEntity<TokenResponse> reissue(@RequestBody TokenRequest tokenRequest) {
        TokenResponse tokenResponse = loginService.reissue(tokenRequest);

        return ResponseEntity.status(HttpStatus.OK).body(tokenResponse);
    }

    // ************* 아이디 찾기 *************

    @ApiOperation(value = "아이디 찾기 - SMS 인증번호 전송", notes = "아이디 찾기 시 SMS 인증 번호를 전송합니다.")
    @PostMapping(value = "/find-id/sms")
    public ResponseEntity<FindSmsAuthNumberPostResponse> sendFindIdSmsAuthNumber(
            @RequestBody @Validated SmsAuthNumberPostRequest requestDto) {
        FindSmsAuthNumberPostResponse response = loginService.sendFindIdSmsAuthNumber(
                requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @ApiOperation(value = "아이디 찾기 - SMS 인증번호 확인 및 아이디 찾기", notes = "아이디 찾기 시 SMS 인증 번호를 확인한 후 아이디를 찾습니다.")
    @GetMapping(value = "/find-id/sms")
    public ResponseEntity<FindSmsAuthNumberGetResponse> checkFindIdSmsAuth(
            @ModelAttribute FindIdCheckSmsGetRequest requestDto) {

        // #1. SMS 인증번호 DB 데이터 검증 후
        // #2. 휴대폰번호로 아이디 찾기
        return ResponseEntity.status(HttpStatus.OK)
                .body(loginService.checkFindIdSmsAuth(requestDto));

    }

    // ***************************************

    // ************* 비밀번호 찾기 *************

    @ApiOperation(value = "비밀번호 찾기 - SMS 인증번호 전송", notes = "비밀번호 찾기 시 비밀번호 찾기 SMS 인증 번호를 전송합니다.")
    @PostMapping(value = "/find-pw/sms")
    public ResponseEntity<FindSmsAuthNumberPostResponse> sendFindPwSmsAuthNumber(
            @RequestBody @Validated PassResetSmsAuthNumberPostRequest requestDto) {
        FindSmsAuthNumberPostResponse response = loginService.sendFindPwSmsAuthNumber(
                requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @ApiOperation(value = "비밀번호 찾기 - SMS 인증번호 확인 및 비밀번호 초기화", notes = "비밀번호 찾기 시 SMS 인증 번호를 확인한 후 비밀번호를 초기화합니다.")
    @GetMapping(value = "/find-pw/sms")
    public ResponseEntity<FindSmsAuthNumberGetResponse> checkFindPwSmsAuthAndReset(
            @ModelAttribute FindPwCheckSmsGetRequest requestDto) {

        // #1. SMS 인증번호 DB 데이터 검증 후
        // #2. 이메일과 휴대폰 번호로 아이디 찾기
        return ResponseEntity.status(HttpStatus.OK)
                .body(loginService.checkFindPwSmsAuth(requestDto));

    }
}

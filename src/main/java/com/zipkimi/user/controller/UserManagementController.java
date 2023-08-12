package com.zipkimi.user.controller;

import com.zipkimi.user.dto.request.JoinUserPostRequest;
import com.zipkimi.user.dto.request.SmsAuthNumberGetRequest;
import com.zipkimi.user.dto.request.SmsAuthNumberPostRequest;
import com.zipkimi.user.dto.response.JoinUserPostResponse;
import com.zipkimi.user.dto.response.SmsAuthNumberGetResponse;
import com.zipkimi.user.dto.response.SmsAuthNumberPostResponse;
import com.zipkimi.user.service.UserManagementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@Api(value = "회원관리")
@RequestMapping (produces = "application/json; charset=utf8")
public class UserManagementController {

    private final UserManagementService userManagementService;

    @ApiOperation(value = "일반 회원 가입")
    @PostMapping(value = "/api/v1/userMgmt/users")
    public ResponseEntity<JoinUserPostResponse> joinUser(HttpServletRequest request, @RequestBody @Validated JoinUserPostRequest requestDto){
        return ResponseEntity.status(HttpStatus.OK).body(userManagementService.joinUser(requestDto));
    }

    @ApiOperation(value = "SMS 인증번호 전송")
    @PostMapping(value = "/api/v1/userMgmt/users/sms")
    public ResponseEntity<SmsAuthNumberPostResponse> sendSmsAuthNumber(HttpServletRequest request,
            @RequestBody @Validated SmsAuthNumberPostRequest requestDto) {
/*      차이점
        @Valid
            - JSR-303 자바 표준 스펙
            - 특정 ArgumentResolver 를 통해 진행되어 컨트롤러 메소드의 유효성 검증만 가능하다.
            - 유효성 검증에 실패할 경우 MethodArgumentNotValidException 이 발생한다.
            - gradle 에 dependency 를 추가해야한다.
        @Validated
            - 자바 표준 스펙이 아닌 스프링 프레임워크가 제공하는 기능
            - AOP 를 기반으로 스프링 빈의 유효성 검증을 위해 사용되며 클래스에는 @Validated 를, 메소드에는 @Valid 를 붙여주어야 한다.
            - 유효성 검증에 실패할 경우 ConstraintViolationException 이 발생한다.*/
        return ResponseEntity.status(HttpStatus.OK).body(userManagementService.sendSmsAuthNumber(requestDto));
        // TODO response 구성하기

    }

    @ApiOperation(value = "SMS 인증번호 확인")
    @GetMapping(value = "/api/v1/userMgmt/users/sms/{id}")
    public ResponseEntity<SmsAuthNumberGetResponse> checkSmsAuthNumber(HttpServletRequest request, @ModelAttribute SmsAuthNumberGetRequest requestDto){
        // SMS 인증번호 입력 -> 확인 결과 응답
        return ResponseEntity.status(HttpStatus.OK).body(userManagementService.checkSmsAuthNumber(requestDto));
    }


// TODO - 1. 네이버, 카카오 - 회원가입 후 API 키 발급 - 푸름
// TODO - 2. 일반 집킴이 회원가입 API 개발 - 푸름
// TODO - 3. Oauth 2.0 활용한 소셜 로그인 개발 - 페어프로그래밍
// TODO - 4. 로그인 개발 (Spring Security + JWT Token) - 페어프로그래밍

// TODO - 5. 아이디 / 비밀번호 찾기 개발 - 슬기
}

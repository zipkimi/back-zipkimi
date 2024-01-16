package com.zipkimi.builder.controller;

import com.zipkimi.builder.dto.request.BuilderLoginRequest;
import com.zipkimi.builder.service.BuilderLoginService;
import com.zipkimi.global.jwt.dto.response.TokenResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@Api(value = "시공사 회원 관리")
@RequestMapping("/api/v1/builders")
public class BuilderLoginController {

    private BuilderLoginService loginService;

    @ApiOperation(value = "시공사 로그인 ")
    public ResponseEntity<TokenResponse> login(@RequestBody BuilderLoginRequest request){
        TokenResponse tokenResponse = loginService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(tokenResponse);
    }

}

package com.zipkimi.builder.controller;

import com.zipkimi.builder.service.BuilderManagementService;
import com.zipkimi.user.dto.request.SmsAuthNumberGetRequest;
import com.zipkimi.user.dto.request.SmsAuthNumberPostRequest;
import com.zipkimi.user.dto.response.SmsAuthNumberGetResponse;
import com.zipkimi.user.dto.response.SmsAuthNumberPostResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@Api(value = "시공사 회원 관리")
@RequestMapping("/api/v1/builderMgmt/builders")
public class BuilderManagementController {

    private BuilderManagementService builderManagementService;

    @ApiOperation(value = "SMS 인증번호 전송")
    @PostMapping(value = "/sms")
    public ResponseEntity<SmsAuthNumberPostResponse> sendBuilderUserJoinSmsAuthNumber(@RequestBody
            SmsAuthNumberPostRequest requestDto){
        return ResponseEntity.status(HttpStatus.OK).body(builderManagementService.sendBuilderUserJoinSmsAuthNumber(requestDto));
    }

    @ApiOperation(value = "SMS 인증번호 확인")
    @GetMapping(value = "/sms")
    public ResponseEntity<SmsAuthNumberGetResponse> checkBuilderUserJoinSmsAuthNumber(
            @ModelAttribute
            SmsAuthNumberGetRequest requestDto) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(builderManagementService.checkBuilderUserJoinSmsAuthNumber(requestDto));

    }
}

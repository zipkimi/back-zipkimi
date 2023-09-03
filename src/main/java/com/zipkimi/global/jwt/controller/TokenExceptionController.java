package com.zipkimi.global.jwt.controller;

import com.zipkimi.global.dto.response.BaseResponse;
import com.zipkimi.global.security.exception.CustomAuthenticationEntryPointException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/exception")
public class TokenExceptionController {

    @GetMapping("/entrypoint")
    public BaseResponse entrypointException() {
        throw new CustomAuthenticationEntryPointException();
    }

    @GetMapping("/accessDenied")
    public BaseResponse accessDeniedException() {
        throw new AccessDeniedException("");
    }

}
package com.zipkimi.builder.service;

import com.zipkimi.builder.dto.request.BuilderLoginRequest;
import com.zipkimi.global.jwt.dto.response.TokenResponse;
import org.springframework.stereotype.Service;

@Service
public class BuilderLoginService {

    public TokenResponse login(BuilderLoginRequest request) {
        return TokenResponse.builder().build();
    }
}

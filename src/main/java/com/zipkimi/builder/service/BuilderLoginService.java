package com.zipkimi.builder.service;

import com.zipkimi.builder.dto.request.BuilderLoginRequest;
import com.zipkimi.entity.BuilderUserEntity;
import com.zipkimi.global.jwt.JwtTokenProvider;
import com.zipkimi.global.jwt.dto.response.TokenResponse;
import com.zipkimi.global.jwt.entity.RefreshTokenEntity;
import com.zipkimi.global.jwt.repository.RefreshTokenRepository;
import com.zipkimi.repository.BuilderUserRepository;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class BuilderLoginService {

    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private BuilderUserRepository builderUserRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public TokenResponse login(BuilderLoginRequest request) {
        Optional<BuilderUserEntity> builderUserOptional = builderUserRepository.findByEmailAndIsUseIsTrue(
                request.getEmail());
        if (builderUserOptional.isEmpty()
                || !passwordEncoder.matches(request.getPassword(), builderUserOptional.get().getPassword())
        ) {
            return TokenResponse.builder()
                    .message("가입하지 않은 이메일이거나 잘못된 비밀번호입니다.")
                    .build();
        }

        UsernamePasswordAuthenticationToken authenticationToken = request.toAuthentication();
        log.info("login authenticationToken = " + authenticationToken);
        log.info("login authenticationToken.getName() = " + authenticationToken.getName());

        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);
        log.info("login authentication = " + authentication);
        log.info("login authentication.getCredentials() = " + authentication.getCredentials());

        // 인증 정보를 기반으로 JWT 토큰 생성
        TokenResponse tokenResponse = jwtTokenProvider.createToken(authentication);
        log.info("login tokenResponse = " + tokenResponse);

        // 이전의 RefreshToken을 DB에서 모두 삭제
        refreshTokenRepository.deleteAll();

        // RefreshToken을 DB에 저장 (식별을 위해 userId도 함께 저장)
        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .userId(builderUserOptional.get().getBuilderId())
                .token(tokenResponse.getRefreshToken())
                .build();
        refreshTokenRepository.save(refreshToken);

        //  토큰 발급
        return TokenResponse.builder()
                .message("로그인에 성공하였습니다.")
                .grantType(tokenResponse.getGrantType())
                .accessToken(tokenResponse.getAccessToken())
                .refreshToken(tokenResponse.getRefreshToken())
                .accessTokenExpireDate(tokenResponse.getAccessTokenExpireDate())
                .build();
    }
}

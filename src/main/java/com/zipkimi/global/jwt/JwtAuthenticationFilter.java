package com.zipkimi.global.jwt;

import com.zipkimi.global.dto.response.TokenResponse;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT 유효한 토큰인지 인증하기 위한 Filter
 * Security 설정 시에 UsernamePasswordAuthentication 앞에 세팅해서 반환하기 전에 인증 여부를 JSON으로 반환시킴
 */

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    // request로 들어오는 Jwt의 유효성을 검증 - JwtProvider.validationToken() 을 필터로서 FilterChain에 추가
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException  {

        // WebSecurityConfig 에서 보았던 UsernamePasswordAuthenticationFilter 보다 먼저 동작을 하게 됩니다.
        // Access / Refresh 헤더에서 토큰을 가져옴.
        String accessToken = jwtTokenProvider.getHeaderToken(request, "Access");
        String refreshToken = jwtTokenProvider.getHeaderToken(request, "Refresh");

        // accessToken이 null이 아니고
        // accessToken이 유효한 토큰이라면 setAuthentication를 통해 security context에 인증 정보저장
        if (accessToken != null && jwtTokenProvider.validationToken(accessToken)) {
            setAuthentication(jwtTokenProvider.getEmailFromToken(accessToken));
        }
        // accessToken이 만료 & refreshToken 존재
        else if (refreshToken != null) {
            // 리프레시 토큰 검증 && 리프레시 토큰 DB에서  토큰 존재유무 확인
            boolean isRefreshToken = jwtTokenProvider.refreshTokenValidation(refreshToken);
            // 리프레시 토큰이 유효하고 리프레시 토큰이 DB와 비교했을때 똑같다면
            if (isRefreshToken) {
                // 리프레시 토큰으로 아이디 정보 가져오기
                String userPk = jwtTokenProvider.getEmailFromToken(refreshToken);
                // 새로운 어세스 토큰 발급
                TokenResponse newAccessToken = jwtTokenProvider.createTokenDto(userPk, "Access");
                // 헤더에 리프레시 토큰 추가
                //jwtTokenProvider.setHeaderRefreshToken(response, refreshToken);
                // 헤더에 어세스 토큰 추가
                jwtTokenProvider.setHeaderAccessToken(response, String.valueOf(newAccessToken));
                // Security context에 인증 정보 넣기
                setAuthentication(jwtTokenProvider.getEmailFromToken(String.valueOf(newAccessToken)));
            }
        }

        filterChain.doFilter(request, response);

    }

    // SecurityContext 에 Authentication 객체를 저장합니다.
    public void setAuthentication(String email) {
        Authentication authentication = jwtTokenProvider.createAuthentication(email);
        // security가 만들어주는 securityContextHolder 그 안에 authentication을 넣어줍니다.
        // security가 securitycontextholder에서 인증 객체를 확인하는데
        // jwtAuthfilter에서 authentication을 넣어주면 UsernamePasswordAuthenticationFilter 내부에서 인증이 된 것을 확인하고 추가적인 작업을 진행하지 않습니다.
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}

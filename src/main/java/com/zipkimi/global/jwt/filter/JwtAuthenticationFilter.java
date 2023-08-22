package com.zipkimi.global.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zipkimi.entity.UserEntity;
import com.zipkimi.entity.UserRole;
import com.zipkimi.global.dto.response.BaseResponse;
import com.zipkimi.global.jwt.JwtTokenProvider;
import com.zipkimi.global.jwt.dto.TokenResponse;
import com.zipkimi.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT 유효한 토큰인지 인증하기 위한 Filter
 * Security 설정 시에 UsernamePasswordAuthentication 앞에 세팅해서 반환하기 전에 인증 여부를 JSON으로 반환시킴
 * 클라이언트가 Header에 토큰 값을 실어서 보내면, doFilterInternal 메서드 안에서 토큰을 검증
 * 인증 객체 생성 후에 Security Context에 정보 저장
 */

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    // HTTP 요청이 오면 WAS(tomcat)가 HttpServletRequest, HttpServletResponse 객체를 만들어줌
    // 만든 인자 값을 받아옴
    // request(요청)이 들어오면 diFilterInternal 이 딱 한번 실행된다.
    // request(요청)이 Jwt의 유효성을 검증
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException  {

        log.info("============================= JwtAuthenticationFilter request = "+ request);
        log.info("============================= JwtAuthenticationFilter response = " + response);
        log.info("============================= JwtAuthenticationFilter filterChain = " + filterChain);

        // WebSecurityConfig 에서 보았던 UsernamePasswordAuthenticationFilter 보다 먼저 동작을 하게 됩니다.
        // Access / Refresh 헤더에서 토큰을 가져옴.
        String accessToken = jwtTokenProvider.getHeaderToken(request, "Access");
        String refreshToken = jwtTokenProvider.getHeaderToken(request, "Refresh");

        log.info("============================= JwtAuthenticationFilter accessToken1 = "+ accessToken);
        log.info("============================= JwtAuthenticationFilter refreshToken1 = " + refreshToken);

        // AccessToken이 null이 아니면서 유효한 토큰이라면
        if (accessToken != null && jwtTokenProvider.validationToken(accessToken)) {
            log.info("============================= JwtAuthenticationFilter accessToken2 = " + accessToken);
            log.info("=============================  JwtAuthenticationFilter refreshToken2 = " + refreshToken);

            // setAuthentication를 통해 security context에 인증 정보 저장
            // 이때 accessToken으로 회원 정보 email 추출
            setAuthentication(jwtTokenProvider.getEmailFromToken(accessToken));
            log.info("=============================  JwtAuthenticationFilter jwtTokenProvider.getEmailFromToken(accessToken) = "
                    + jwtTokenProvider.getEmailFromToken(accessToken));

        }

        // AccessToken이 만료 & RefreshToken 존재
        else if (refreshToken != null) {
            // RefreshToken 검증 && RefreshToken DB에서 토큰 존재유무 확인
            boolean isRefreshToken = jwtTokenProvider.refreshTokenValidation(refreshToken);
            log.info("=============================  JwtAuthenticationFilter accessToken3 = " + accessToken);
            log.info("=============================  JwtAuthenticationFilter refreshToken3 = " + refreshToken);
            log.info("=============================  JwtAuthenticationFilter isRefreshToken = " + refreshToken);

            // RefreshToken이 유효하고 RefreshToken이 DB와 비교했을 때 똑같다면
            if (isRefreshToken) {
                // RefreshToken으로 아이디 정보 가져오기
                String userPk = jwtTokenProvider.getEmailFromToken(refreshToken);

                log.info("=============================  JwtAuthenticationFilter userPk = " + userPk);

                TokenResponse newAccessToken = jwtTokenProvider.createTokenDto(userPk,
                        Collections.singletonList(UserRole.ROLE_USER.name()));
                log.info("=============================  JwtAuthenticationFilter newAccessToken = " + newAccessToken);

                // Header에 RefreshToken 추가
                jwtTokenProvider.setHeaderRefreshToken(response, refreshToken);
                // Header에 AccessToken 추가
                jwtTokenProvider.setHeaderAccessToken(response, String.valueOf(newAccessToken));
                // Security context에 인증 정보 넣기
                setAuthentication(jwtTokenProvider.getEmailFromToken(String.valueOf(newAccessToken)));

                log.info("============================= JwtAuthenticationFilter.getEmailFromToken(String.valueOf(newAccessToken) = "
                        + jwtTokenProvider.getEmailFromToken(String.valueOf(newAccessToken)));
                }
                // RefreshToken이 만료 || RefreshToken이 DB와 비교했을때 똑같지 않다면
                else {
                    jwtExceptionHandler(response, "RefreshToken Expired", HttpStatus.BAD_REQUEST);
                    return;
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

    // Jwt 예외처리
    public void jwtExceptionHandler(HttpServletResponse response, String msg, HttpStatus status) {
        response.setStatus(status.value());
        response.setContentType("application/json");
        try {
            String json = new ObjectMapper().writeValueAsString(new BaseResponse(msg));
            response.getWriter().write(json);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}

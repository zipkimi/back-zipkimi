package com.zipkimi.global.jwt;

import com.zipkimi.entity.RefreshTokenEntity;
import com.zipkimi.entity.UserRole;
import com.zipkimi.global.jwt.dto.TokenResponse;
import com.zipkimi.global.security.CustomUserDetailsService;
import com.zipkimi.global.jwt.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.token.key}") // application-xxx.properties의 secret key
    private String secretKey;
    private Key key;

    private final CustomUserDetailsService userDetailsService;

    //TODO 테스트를 위해 짧게 시간 설정 해볼 것
    // Access Token 유효시간 : 1시간
    private static final Long ACCESS_TIME =  60 * 60 * 1000L;

    //TODO 테스트를 위해 짧게 시간 설정 해볼 것
    // Refresh Token 유효시간 : 14일
    private static final Long REFRESH_TIME =  14 * 24 * 60 * 60 * 1000L;
    
    public static final String ACCESS_TOKEN = "Access_Token";
    public static final String REFRESH_TOKEN = "Refresh_Token";
    public static final String GRANT_TYPE = "Bearer";
    private String ROLES = "roles";

    @PostConstruct
    protected void init() {
        // key를 base64로 인코딩
        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        key= Keys.hmacShaKeyFor(encodedKey.getBytes());
    }

    // Jwt 토큰 생성
    public TokenResponse createTokenDto(String userPk, List<String> roles) {

        // Claims 에 user 구분을 위한 User pk 및 authorities 목록 삽입
        Claims claims = Jwts.claims().setSubject(String.valueOf(userPk));
        claims.put(UserRole.ROLE_USER.name(), roles);
        log.info("============================= JwtTokenProvider claims = " + claims);
        log.info("============================= JwtTokenProvider ROLES = " + ROLES);

        // 생성날짜, 만료날짜를 위한 Date
        Date now = new Date();
        // accessToken 만료 시간
        Date accessTokenExpiration = new Date(now.getTime() + ACCESS_TIME);
        log.info("============================= JwtTokenProvider accessTokenExpiration = " + accessTokenExpiration);

        // refreshToken 만료 시간
        Date refreshTokenExpiration = new Date(now.getTime() + REFRESH_TIME);

        log.info("============================= JwtTokenProvider refreshTokenExpiration = " + refreshTokenExpiration);

        String accessToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setClaims(claims)
                .setIssuedAt(now) //발행 시간
                .setExpiration(accessTokenExpiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        log.info("============================= JwtTokenProvider accessToken = " + accessToken);

        String refreshToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setExpiration(refreshTokenExpiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        log.info("============================= JwtTokenProvider refreshToken = " + refreshToken);

        return TokenResponse.builder()
                .grantType(GRANT_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpireDate(ACCESS_TIME)
                .build();
    }

    // JWT 토큰에서 인증 정보 조회
    public Authentication createAuthentication(String userPk) {
        log.info("============================= JwtTokenProvider createAuthentication userPk = " + userPk);
        UserDetails userDetails = userDetailsService.loadUserByUsername(userPk);
        // spring security 내에서 가지고 있는 객체입니다. (UsernamePasswordAuthenticationToken)
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 회원 정보 추출 (email)
    public String getEmailFromToken(String token) {
        log.info("============================= JwtTokenProvider getEmailFromToken token = " + token);
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();

    }

    // Jwt 토큰 복호화해서 가져오기
    public Claims parseClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    // 토큰 검증
    public Boolean tokenValidation(String token) {
        log.info("============================= JwtTokenProvider tokenValidation token = " + token);
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return false;
        }
    }

    // refreshToken 토큰 검증
    // DB에 저장되어 있는 token과 비교
    // DB에 저장한다는 것이 jwt token을 사용한다는 강점을 상쇄시킨다.
    // DB 보다는 redis를 사용하는 것이 더욱 좋다. (in-memory db기 때문에 조회속도가 빠르고 주기적으로 삭제하는 기능이 기본적으로 존재합니다.)
    public Boolean refreshTokenValidation(String token) {
        log.info("============================= JwtTokenProvider refreshTokenValidation token = " + token);

        // 1차 토큰 검증
        if(!tokenValidation(token)) return false;

        // DB에 저장한 토큰 비교
        Optional<RefreshTokenEntity> refreshToken = refreshTokenRepository.findByEmail(getEmailFromToken(token));

        return refreshToken.isPresent() && token.equals(refreshToken.get().getRefreshToken());
    }

    // header 토큰을 가져오는 기능
    public String getHeaderToken(HttpServletRequest request, String type) {
        return type.equals("Access") ? request.getHeader(ACCESS_TOKEN) :request.getHeader(REFRESH_TOKEN);
    }

    // Access 토큰 헤더 설정
    public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {
        response.setHeader("Access_Token", accessToken);
    }

    // Refresh 토큰 헤더 설정
    public void setHeaderRefreshToken(HttpServletResponse response, String refreshToken) {
        response.setHeader("Refresh_Token", refreshToken);
    }

    // jwt 의 유효성 및 만료일자 확인
    public boolean validationToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error(e.toString());
            return false;
        }
    }


}

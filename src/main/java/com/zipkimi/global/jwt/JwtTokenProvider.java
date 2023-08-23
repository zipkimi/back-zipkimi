package com.zipkimi.global.jwt;

import com.zipkimi.global.jwt.dto.response.TokenResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    // application-xxx.properties의 secret key
    @Value("${jwt.token.key}")
    private String secretKey;
    private Key key;

    private static final String AUTHORITIES_KEY = "auth";
    private static final String GRANT_TYPE = "Bearer";
    private final Long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30L;            // 30분 (1시간 : 60 * 60 * 1000L;)
    private final Long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7L;  // 7일 (14일 : 14 * 24 * 60 * 60 * 1000L;)

    // application-xxx.properties의 secret key
    @PostConstruct
    protected void init() {
        // key를 base64로 인코딩
        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        key= Keys.hmacShaKeyFor(encodedKey.getBytes());
    }

    // Jwt 토큰 생성
    public TokenResponse createToken(Authentication authentication) {

        // 권한들 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        log.info("JwtTokenProvider createToken authorities = " + authorities);

        // 생성날짜, 만료날짜를 위한 Date
        Date now = new Date();

        String accessToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .setExpiration((new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        log.info("============================= JwtTokenProvider accessToken = " + accessToken);
        log.info("============================= JwtTokenProvider new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME = "
                + new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME));

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setExpiration((new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        log.info("============================= JwtTokenProvider refreshToken = " + refreshToken);
        log.info("============================= JwtTokenProvider new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME = "
                + new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME));

        return TokenResponse.builder()
                .grantType(GRANT_TYPE)
                .accessToken(accessToken)
                .accessTokenExpireDate(ACCESS_TOKEN_EXPIRE_TIME)
                .refreshToken(refreshToken)
                .build();
    }

    public Authentication getAuthentication(String accessToken) {
        // 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        log.info("JwtTokenProvider getAuthentication claims = " + claims);

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        log.info("JwtTokenProvider authorities = " + authorities);

        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        log.info("JwtTokenProvider principal = " + principal);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public boolean validateToken(String token) {
        log.info("JwtTokenProvider validateToken = " + token);

        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public Long getExpiration(String accessToken) {
        Claims claims = Jwts.parser()
                .parseClaimsJws(accessToken)
                .getBody();
        return claims.getExpiration().getTime();
    }
}

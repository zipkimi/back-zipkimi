package com.zipkimi.global.config;

import com.zipkimi.global.jwt.JwtTokenProvider;
import com.zipkimi.global.jwt.config.CustomAccessDeniedHandler;
import com.zipkimi.global.jwt.config.CustomAuthenticationEntryPoint;
import com.zipkimi.global.jwt.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig  {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    // 회원가입 시 비밀번호 암호화를 위한 PasswordEncoder Bean 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // Swagger Page 표시를 위한 셋팅
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/v2/api-docs", "/swagger-resources/**",
                "/swagger-ui.html", "/swagger-ui/**", "/webjars/**", "/swagger/**");
    }

    // WebSecurityConfigurerAdapter가 Deprecated가 되어 대신에 SecurityFilterChain을 사용
    // 반환값 존재 / Bean으로 등록 : SecurityFilterChain을 반환하고 Bean으로 등록함으로써 컴포넌트 기반의 보안 설정 가능
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // REST API 관련 미사용 설정
                .httpBasic().disable() // REST API 이므로 기본 인증 로그인 미사용
                .csrf().disable() // REST API 이므로 csrf 보안 미사용 (서버에 인증정보를 저장하지 않기 때문에)
                .formLogin().disable() // REST API 이므로 formLogin 미사용
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // JWT를 통해 인증하므로 세션 미사용

                // HttpServletRequest를 사용하는 요청들에 대한 접근 제한 설정
                // 회원가입(userMgmt), 로그인 및 아이디/비밀번호 찾기(users)을 위한 요청은 검증 없이 요청을 허용함
                .and()
                .authorizeRequests()
                .antMatchers("/api/v1/userMgmt/users/**").permitAll()
                .antMatchers("/api/v1/users/auth/sign").permitAll()
                .antMatchers("/api/v1/users/auth/login").permitAll()
                .antMatchers("/api/v1/users/find-id/**").permitAll()
                .antMatchers("/api/v1/users/find-pw/**").permitAll()
                .antMatchers("/exception/**").permitAll()

                //시공사 회원 관련 접근 허용 추가
                .antMatchers("/api/v1/builderMgmt/builders/**").permitAll()

                // 만약, 권한별 접근 설정을 하고 싶으면 hasRole 사용
                // 특정 요청에 대한 권한 체크 (ROLE이 USER일 때만 이용 가능)
                //.antMatchers("/api/v1/users/**").hasRole("USER")

                // 테스트를 위해서 회원가입 로직의 권한을 USER로 줌
                .antMatchers("/api/v1/userMgmt/**").hasRole("USER")

                // 그 외 항목 전부 인증 적용
                .anyRequest().authenticated()

                // JWT Token을 위한 Jwt Filter 추가
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        http
                // JwtAuthentication exception handling
                // 토큰 인증과정에서 발생하는 예외를 처리하기 위한 EntryPoint 등록
                .exceptionHandling()
                .authenticationEntryPoint(customAuthenticationEntryPoint)

                // Jwt Access Denial handler (일반 유저가 시공사 관련 페이지에 접속할 경우 등)
                // 인가에 실패했을 때 예외를 발생시키는 handler 등록
                .and()
                .exceptionHandling().accessDeniedHandler(customAccessDeniedHandler);

        return http.build();
    }


}

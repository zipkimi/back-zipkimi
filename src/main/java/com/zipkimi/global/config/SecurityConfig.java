package com.zipkimi.global.config;

import com.zipkimi.global.exception.CustomAuthenticationEntryPoint;
import com.zipkimi.global.jwt.JwtAuthenticationFilter;
import com.zipkimi.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                /** REST API 관련 미사용 설정 */
                .httpBasic().disable() // REST API 이므로 기본 인증 로그인 미사용
                .csrf().disable() // REST API 이므로 csrf 보안 미사용 (서버에 인증정보를 저장하지 않기 때문에)
                .formLogin().disable() // REST API 이므로 formLogin 미사용
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // JWT를 통해 인증하므로 세션 미사용

                /** HttpServletRequest를 사용하는 요청들에 대한 접근 제한 설정*/
                .and()
                .authorizeRequests()
                .antMatchers("/api/v1/userMgmt/**").permitAll()
                .antMatchers("/api/v1/users/**").permitAll()
                .antMatchers("/exception/**").permitAll()

                // 그 외 항목 전부 인증 적용
                .anyRequest().authenticated()

                .and()
                .exceptionHandling()
                .authenticationEntryPoint(customAuthenticationEntryPoint)

                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


}

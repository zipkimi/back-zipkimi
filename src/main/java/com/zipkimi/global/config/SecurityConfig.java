package com.zipkimi.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable() // REST API 이므로 기본설정 미사용
                .csrf().disable() // REST API 이므로 csrf 보안 미사용
                .formLogin().disable() // REST API 이므로 formLogin 미사용
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // JWT를 통해 인증하므로 세션 미사용
                .and()
                .authorizeRequests()
                .antMatchers("/api/v1/userMgmt/**").permitAll()
                .antMatchers("/api/v1/users/**").permitAll()
                .antMatchers("/exception/**").permitAll()
                .anyRequest().authenticated();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // 예외 처리
        //web.ignoring().antMatchers("/static/js/**","/static/css/**","/static/img/**","/static/frontend/**");
    }


}

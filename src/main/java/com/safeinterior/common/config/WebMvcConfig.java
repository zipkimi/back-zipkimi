package com.safeinterior.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

	/**
	 * React, Spring 은 각각 로컬에서 실행하면 3000, 8080 포트로 실행하게 된다.
	 * 그러면 React 에서 Spring API 를 호출하면
	 * 두 도메인은 Port 가 다르기 때문에 SOP 문제가 발생한다.
	 * http://localhost:3000에 대해서 접근할 수 있는 권한을 준다는 매핑정보를 저장해줌으로써
	 * CORS 문제 해결하였다.
	 *
	 * 참고) CORS(Cross-Origin Resource Sharing)는 교차(다른) 출처 리소스 공유
	 * */
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins("localhost:3000")
			.allowedMethods("OPTIONS", "GET", "POST", "PUT", "DELETE");
		WebMvcConfigurer.super.addCorsMappings(registry);
	}
}

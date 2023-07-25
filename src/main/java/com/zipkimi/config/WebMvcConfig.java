package com.zipkimi.config;

import com.zipkimi.common.LogInterceptor;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
// @RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * React, Spring 은 각각 로컬에서 실행하면 3000, 8080 포트로 실행하게 된다. 그러면 React 에서 Spring API 를 호출하면 두 도메인은
     * Port 가 다르기 때문에 SOP 문제가 발생한다. http://localhost:3000에 대해서 접근할 수 있는 권한을 준다는 매핑정보를 저장해줌으로써 CORS
     * 문제 해결하였다. 참고) CORS(Cross-Origin Resource Sharing)는 교차(다른) 출처 리소스 공유
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedOrigins("http://localhost:3000", "https://localhost:3000")
                .allowedMethods(
                        HttpMethod.GET.name(),
                        HttpMethod.POST.name(),
                        HttpMethod.DELETE.name(),
                        HttpMethod.PUT.name(),
                        HttpMethod.PATCH.name(),
                        HttpMethod.OPTIONS.name());
        WebMvcConfigurer.super.addCorsMappings(registry);
    }

    /**
     * 리소스 핸들러 이미지, 자바스크립트, CSS 그리고 HTML 파일과 같은 정적인 리소스를 처리하는 핸들러 등록하는 방법 스프링부트는 기본정적 리소스 핸들러와 캐싱
     * 제공
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/mobile/**")
                .addResourceLocations("classpath:/mobile/") // classpath == resource directory
                .setCacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES))
        // .resourceChain(true)
        ;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/health", "/swagger-ui.html");

    }
}

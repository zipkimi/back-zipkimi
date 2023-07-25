package com.zipkimi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@EnableWebMvc
public class SwaggerConfig implements WebMvcConfigurer {

	private static final String API_NAME = "집킴이 Spring Boot REST API";
	private static final String API_VERSION = "0.0.1";
	private static final String API_DESCRIPTION = "인테리어 안전 플랫폼 집킴이 API 명세서";

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.zipkimi"))  // Swagger를 적용할 클래스의 package명
				.paths(PathSelectors.any())  // 해당 package 하위에 있는 모든 url에 적용
				.build()
				.apiInfo(apiInfo());
	}

	public ApiInfo apiInfo() {  // API의 이름, 현재 버전, API에 대한 정보
		return new ApiInfoBuilder()
				.title(API_NAME)
				.version(API_VERSION)
				.description(API_DESCRIPTION)
				.build();
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/swagger-ui/**")
				.addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/3.0.0/");

	}
}

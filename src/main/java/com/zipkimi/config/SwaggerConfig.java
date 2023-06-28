package com.zipkimi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
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

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
			.apiInfo(apiInfo()) // API에 대한 정보 입력
			// .globalRequestParameters() // API를 테스트할때 모든 API에 전역 파라미터를 설정 가능
			.select()
			.apis(RequestHandlerSelectors.basePackage("com.zipkimi")/* Swagger를 적용할 클래스의 package 명 */)
			.paths(PathSelectors.any()) // package 하위에 있는 모든 url에 적용 - 특정 URL들만 필터링하도록 설정추가 가능
			.build();
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
			.title("Safe-In Spring Boot REST API")
			.version("1.0.0")
			.description("인테리어 안전 플랫폼 집킴이의 swagger api 입니다.")
			.build();
	}
}

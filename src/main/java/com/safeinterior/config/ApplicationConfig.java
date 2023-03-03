package com.safeinterior.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class ApplicationConfig {
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	/**
	 * Message
	 * */
	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:messages");
		messageSource.setDefaultEncoding("UTF-8");
		return messageSource;
	}
	/*
	 * 궁금한 점 -> ResourceBundleMessageSource 로 타입을 했을때는 안됐는데 ReloadableResourceBundleMessageSource 로는 실행된다.
	 * 'import org.springframework.context.support.ResourceBundleMessageSource;'
	 * 에러 메시지
	 * org.springframework.context.NoSuchMessageException: No message found under code 'message.error.parameter.not.null' for locale 'ko_KR'. 에러 발생시킴
	 *
	 * ReloadableResourceBundleMessageSource 개발 문서 설명
	 * JDK 기반 ResourceBundleMessageSource와 달리 이 클래스는 속성 인스턴스를 메시지에 대한 사용자 지정 데이터 구조로 사용하여
	 * Spring 리소스 핸들에서 PropertiesPersister 전략을 통해 메시지를 로드합니다.
	 * 이 전략은 타임스탬프 변경에 따라 파일을 다시 로드할 수 있을 뿐만 아니라
	 * 특정 문자 인코딩을 사용하여 속성 파일을 로드할 수도 있습니다. XML 속성 파일도 감지합니다.
	 *
	 * "classpath:" 접두사를 사용하면 클래스 경로에서 리소스를 계속 로드할 수 있지만 "-1"(영구 캐싱) 이외의 "cacheSeconds" 값은 이 경우 안정적으로 작동하지 않을 수 있습니다.
	 * */

}

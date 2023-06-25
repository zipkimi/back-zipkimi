package com.zipkimi.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableEncryptableProperties
public class PropertyEncryptConfig {

	public static final String JASYPT_STRING_ENCRYPTOR = "jasyptStringEncryptor";

	/*
	  복호화 키값(jasypt.encryptor.password)는 Application 실행 시 외부 Environment 를 통해 주입 받는다.

	  # JAR 예
	  	-Djasypt.encryptor.password=jasypt_password.!
	  # WAR 예
	  	-Djasypt.encryptor.password="jasypt_password.!"

	  알고리즘은 따로 설정하지 않음.
 */

	@Value("${jasypt.encryptor.password}")
	private String encryptKey;

	@Bean(JASYPT_STRING_ENCRYPTOR)
	public StringEncryptor stringEncryptor() {
		log.info("encryptKey: " + encryptKey);

		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		SimpleStringPBEConfig config = new SimpleStringPBEConfig();
		config.setPassword(encryptKey);
		config.setKeyObtentionIterations("1000");
		config.setPoolSize("1");
		config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
		config.setStringOutputType("base64");
		encryptor.setConfig(config);

		log.info("end");

		return encryptor;
	}
}

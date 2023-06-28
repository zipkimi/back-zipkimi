package com.zipkimi.service;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.zipkimi.entity.SmsAuthEntity;
import com.zipkimi.entity.UserEntity;
import com.zipkimi.repository.SmsAuthRepository;
import com.zipkimi.repository.UserRepository;

@SpringBootTest
class UserServiceTest {
	@Autowired
	UserService userService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	SmsAuthRepository smsAuthRepository;

	@Test
	public void userTest(){
		// SmsAuthEntity smsAuth = SmsAuthEntity.builder()
		// 	.smsAuthNumber("1234")
		// 	.phoneNumber("01012345678")
		// 	.isUse(true)
		// 	.isAuthenticate(false)
		// 	.createdDt(LocalDateTime.now())
		// 	.updatedDt(LocalDateTime.now())
		// 	.build();
		// smsAuthRepository.save(smsAuth);

		SmsAuthEntity smsAuth = new SmsAuthEntity();
		smsAuth.setSmsAuthNumber("1234");
			smsAuth.setPhoneNumber("01012345678");
			smsAuth.setIsUse(true);
			smsAuth.setIsAuthenticate(false);
			smsAuth.setCreatedDt(LocalDateTime.now());
			smsAuth.setUpdatedDt(LocalDateTime.now());
		smsAuthRepository.save(smsAuth);

		// UserEntity user = UserEntity.builder()
		// 	.name("유푸름")
		// 	.email("ypr821@gmail.com")
		// 	.password("test123")
		// 	.phoneNumber("01012345678")
		// 	.smsAuthEntity(smsAuth)
		// 	.createdDt(LocalDateTime.now())
		// 	.updatedDt(LocalDateTime.now())
		// 	.build();
		// userRepository.save(user);


		UserEntity user = new UserEntity();
		user.setName("유푸름");
		user.setEmail("ypr821@gmail.com");
		user.setPassword("test123");
		user.setPhoneNumber("01012345678");
		user.setSmsAuthEntity(smsAuth);
		user.setCreatedDt(LocalDateTime.now());
		user.setUpdatedDt(LocalDateTime.now());
		user.setAuthority("general");
		userRepository.save(user);



	}
}
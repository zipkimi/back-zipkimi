package com.safeinterior.user.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.safeinterior.entity.UserEntity;
import com.safeinterior.user.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Transactional(rollbackOn = Exception.class)
@Slf4j
public class UserService {
	private UserRepository userRepository;

	public String getTest() {
		log.info("실행확인");
		List<UserEntity> users = userRepository.findAll();
		users.forEach(UserEntity::getName);
		return " Service 실행 완료";
	}
}

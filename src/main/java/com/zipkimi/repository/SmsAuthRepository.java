package com.zipkimi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zipkimi.entity.SmsAuthEntity;

@Repository
public interface SmsAuthRepository extends JpaRepository<SmsAuthEntity, Long> {

    // 휴대폰 번호와 인증번호로 SMS 인증 정보를 조회하는 메서드
    SmsAuthEntity findByPhoneNumberAndSmsAuthNumber(String phoneNumber, String smsAuthNumber);

}

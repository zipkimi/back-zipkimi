package com.zipkimi.repository;

import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.zipkimi.entity.SmsAuthEntity;

@Repository
public interface SmsAuthRepository extends JpaRepository<SmsAuthEntity, Long> {

    // 휴대폰 번호와 인증번호로 SMS 인증 정보를 조회하는 메서드
    SmsAuthEntity findByPhoneNumberAndSmsAuthNumber(String phoneNumber, String smsAuthNumber);

    // 유효한 인증번호 조회 (만료시간)
    @Query("SELECT sa FROM SmsAuthEntity sa WHERE sa.phoneNumber = :phoneNumber AND sa.isAuthenticate = false AND sa.expirationTime > :currentDateTime")
    SmsAuthEntity findValidSmsAuthByPhoneNumber(@Param("phoneNumber") String phoneNumber, @Param("currentDateTime") LocalDateTime currentDateTime);

}

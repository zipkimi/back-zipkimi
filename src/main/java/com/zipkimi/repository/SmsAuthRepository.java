package com.zipkimi.repository;

import com.zipkimi.entity.SmsAuthEntity;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SmsAuthRepository extends JpaRepository<SmsAuthEntity, Long> {

    List<SmsAuthEntity> findByPhoneNumberAndIsAuthenticateFalseAndIsUseTrue(String phoneNumber);

    // 휴대폰 번호와 인증번호로 SMS 인증 정보를 조회하는 메서드
    SmsAuthEntity findByPhoneNumberAndSmsAuthNumber(String phoneNumber, String smsAuthNumber);

    // 유효한 인증번호 조회 (만료시간)
    @Query("SELECT sa FROM SmsAuthEntity sa WHERE sa.phoneNumber = :phoneNumber AND sa.isAuthenticate = false AND sa.expirationTime > :currentDateTime AND sa.smsAuthType = :smsAuthType")
    SmsAuthEntity findValidSmsAuthByPhoneNumberAndType(
            @Param("phoneNumber") String phoneNumber,
            @Param("currentDateTime") LocalDateTime currentDateTime,
            @Param("smsAuthType") String smsAuthType);

}

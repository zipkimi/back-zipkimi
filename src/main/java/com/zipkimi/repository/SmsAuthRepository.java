package com.zipkimi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zipkimi.entity.SmsAuthEntity;

@Repository
public interface SmsAuthRepository extends JpaRepository<SmsAuthEntity, Long> {

}

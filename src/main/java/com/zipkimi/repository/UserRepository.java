package com.zipkimi.repository;

import com.zipkimi.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByPhoneNumberAndIsUseIsTrue(String phoneNumber);
    Optional<UserEntity> findByPhoneNumberAndEmailAndIsUseIsTrue(String phoneNumber, String email);

    Optional<UserEntity> findByEmailAndIsUseIsTrue(String email);
    Optional<UserEntity> findByPhoneNumber(String phoneNumber);
    Optional<UserEntity> findByEmail(String email);

}

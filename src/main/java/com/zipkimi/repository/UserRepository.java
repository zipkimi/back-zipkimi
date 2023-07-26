package com.zipkimi.repository;

import com.zipkimi.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByPhoneNumberAndIsUseIsTrue(String phoneNumber);
    Optional<UserEntity> findByPhoneNumberAndEmail(String phoneNumber, String email);

    UserEntity findByPhoneNumber(String phoneNumber);
    UserEntity findByEmail(String email);
}

package com.zipkimi.repository;

import com.zipkimi.entity.BuilderUserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuilderUserRepository extends JpaRepository<BuilderUserEntity, Long> {

    Optional<BuilderUserEntity> findByPhoneNumberAndIsUseIsTrue(String phoneNumber);

}

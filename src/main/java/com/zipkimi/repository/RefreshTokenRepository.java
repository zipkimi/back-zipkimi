package com.zipkimi.repository;

import com.zipkimi.entity.RefreshTokenEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByAccountEmail(String email);
}

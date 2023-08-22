package com.zipkimi.global.jwt.repository;

import com.zipkimi.entity.RefreshTokenEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByEmail(String email);
    Optional<RefreshTokenEntity> findByRefreshTokenId(Long refreshTokenId);
    Optional<RefreshTokenEntity> findByUserId(Long id);
}

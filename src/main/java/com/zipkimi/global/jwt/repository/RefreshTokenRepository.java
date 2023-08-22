package com.zipkimi.global.jwt.repository;

import com.zipkimi.entity.RefreshTokenEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByEmail(String email);
    Optional<RefreshTokenEntity> findByRefreshTokenId(Long refreshTokenId);
    Optional<RefreshTokenEntity> findByUserId(Long id);
    Optional<RefreshTokenEntity> findByRefreshToken(String refreshToken);

    //생성
    boolean existsByEmailAndUserAgent(String userEmail, String userAgent);

    //삭제
    void deleteByEmailAndUserAgent(String userEmail, String userAgent);

}

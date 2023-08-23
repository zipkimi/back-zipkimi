package com.zipkimi.global.jwt.entity;

import com.zipkimi.entity.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "refresh_token")
@Getter
@NoArgsConstructor
public class RefreshTokenEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id", nullable = false)
    private Long refreshTokenId;

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    private Long userId;

    @Builder
    public RefreshTokenEntity(String token, Long userId) {
        this.refreshToken = token;
        this.userId = userId;
    }

    public RefreshTokenEntity updateToken(String token) {
        this.refreshToken = token;
        return this;
    }

}

package com.zipkimi.entity;

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
    private Long id;

    @Column(nullable = false)
    private String refreshToken;

    private String accountEmail;

    @Builder
    public RefreshTokenEntity(String token, String email) {
        this.refreshToken = token;
        this.accountEmail = email;
    }

    public RefreshTokenEntity updateToken(String token) {
        this.refreshToken = token;
        return this;
    }

}

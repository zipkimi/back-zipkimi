package com.zipkimi.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@AllArgsConstructor
public class UserEntity extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long userId;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    //권한 : 일반, 시공사 등
    @Column(name = "builder_id")
    private String builderId;

    @Column(name = "is_use", columnDefinition = "true")
    private boolean isUse;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING) //저장될때는 String 으로 저장되도록
    private UserRole role;

    //TODO CustomUserDetails 수정 테스트 중


}

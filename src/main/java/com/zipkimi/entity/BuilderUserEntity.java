package com.zipkimi.entity;

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
@Table(name = "builder_users")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@AllArgsConstructor
public class BuilderUserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "builder_member_id")
    private Long builderMemberId;

    //TODO builder와 연관 관계 맵핑 필요
    private Long builderId;

    @Column(name = "id")
    private String id;

    @Column(name = "password")
    private String password;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "is_use", columnDefinition = "true")
    private boolean isUse;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING) //저장될때는 String 으로 저장되도록
    private UserRole role;

}

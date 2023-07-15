package com.zipkimi.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "sms_auth")
public class SmsAuthEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sms_auth_id")
    private long smsAuthId;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "sms_auth_number")
    private String smsAuthNumber;

    @Column(name = "is_use")
    private Boolean isUse;

    @Column(name = "is_authenticate")
    private Boolean isAuthenticate;

    @Column(name = "expiration_time")
    private LocalDateTime expirationTime;

    @Column(name = "sms_auth_type")
    private String smsAuthType;

}

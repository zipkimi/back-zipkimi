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
// @Builder
//@AllArgsConstructor
// @RequiredArgsConstructor
@Entity
@Table(name = "sms_auth")
public class SmsAuthEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="sms_auth_id")
	private long smsAuthId;

	@Column(name = "phone_number")
	private String phoneNumber;

	@Column(name = "sms_auth_number")
	private String smsAuthNumber;

	@Column(name = "is_use")
	private Boolean isUse;

	@Column(name="is_authenticate")
	private Boolean isAuthenticate;

	// @CreatedDate
	@Column(name = "created_dt")
	private LocalDateTime createdDt;

	// @LastModifiedDate
	@Column(name = "updated_dt")
	private LocalDateTime updatedDt;

}

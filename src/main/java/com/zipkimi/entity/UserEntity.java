package com.zipkimi.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
// @Builder
//@AllArgsConstructor
// @RequiredArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="user_id")
	private long userId;

	@OneToOne
	@JoinColumn(name = "sms_auth_id")
	private SmsAuthEntity smsAuthEntity;

	@Column(name = "email")
	private String email;

	@Column(name = "password")
	private String password;

	@Column(name = "name")
	private String name;

	@Column(name="phone_number")
	private String phoneNumber;

	//권한 : 일반, 시공사 등
	@Column(name = "authority")
	private String authority;

	@CreatedDate
	@Column(name = "created_dt")
	private LocalDateTime createdDt;

	@LastModifiedDate
	@Column(name = "updated_dt")
	private LocalDateTime updatedDt;
}

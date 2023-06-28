package com.zipkimi.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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

	@Column(name = "name")
	private String name;

	@Column(name = "pw")
	private String pw;

	@Column(name = "phone")
	private String phone;

	@Column(name = "token")
	private String token;

	@Column(name = "reg_dt")
	private LocalDateTime regDt;

	@Column(name = "mod_dt")
	private LocalDateTime modDt;

	@Column(name = "mod_id")
	private Long modUserId;
}

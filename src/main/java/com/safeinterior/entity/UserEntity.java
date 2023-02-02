package com.safeinterior.entity;

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

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "user")
public class UserEntity implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne(targetEntity = CompanyEntity.class)
	@JoinColumn(name = "com_id", insertable = false, updatable = false)
	private CompanyEntity company;

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

package com.safeinterior.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "company")
public class CompanyEntity implements Serializable {
	@Id
	private long id;
	private String name;
	private String phone;
	private String ceoName;
	private String corpNum;
	private String bizNum;
	private String address;
}

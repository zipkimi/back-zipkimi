package com.zipkimi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "builder")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@AllArgsConstructor
public class BuilderEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "builder_id")
    private Long id;

    @Column(name = "builder_name")
    private String builder_name;

    @Column(name = "status")
    private String status;  //요청, 취소, 승인완료, 승인거절, 재요청

    @Column(name = "builder_number")
    private String builder_number;

    @Column(name = "builder_com_number")
    private String builder_com_number;

    @Column(name = "representative_name")
    private String representative_name;

    @Column(name = "builder_contact_number")
    private String builder_contact_number;

    @Column(name = "taxation")
    private String taxation;    //과세구분 (일반과세자, 간이과세자, 법인과세자, 면세법인 사업자)

    @Column(name = "business_item")
    private String business_item;   //종목 (개인대표, 공동대표)

    @Column(name = "representative_division")
    private String representative_division; //대표자 구분 (개인대표, 공동대표)

    @Column(name = "representative_birth_date")
    private String representative_birth_date;

    @Column(name = "business_location")
    private String business_location;

    @Column(name = "zip_code")
    private String zip_code;

    @Column(name = "image_id")
    private Long image_id;


}

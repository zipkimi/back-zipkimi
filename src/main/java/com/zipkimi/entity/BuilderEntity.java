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
    private String builderName;

    @Column(name = "status")
    private String status;  //요청, 취소, 승인완료, 승인거절, 재요청

    @Column(name = "builder_number")
    private String builderNumber;

    @Column(name = "builder_com_number")
    private String builderComNumber;

    @Column(name = "representative_name")
    private String representativeName;

    @Column(name = "builder_contact_number")
    private String builderContactNumber;

    @Column(name = "taxation")
    private String taxation;    //과세구분 (일반과세자, 간이과세자, 법인과세자, 면세법인 사업자)

    @Column(name = "business_item")
    private String businessItem;   //종목 (개인대표, 공동대표)

    @Column(name = "representative_division")
    private String representativeDivision; //대표자 구분 (개인대표, 공동대표)

    @Column(name = "representative_birth_date")
    private String representativeBirthDate;

    @Column(name = "business_location")
    private String businessLocation;

    @Column(name = "zip_code")
    private String zipCode;

    @Column(name = "image_id")
    private Long imageId;


}

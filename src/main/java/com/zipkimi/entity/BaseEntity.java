package com.zipkimi.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

    @CreatedBy
    @Column(name = "created_user", updatable = false)
    private Long createdUser;

    @CreatedDate
    @Column(name = "created_dt", updatable = false)
    private LocalDateTime createdDt;


    @LastModifiedBy
    @Column(name = "updated_user")
    private Long updatedUser;

    @CreatedDate
    @LastModifiedDate
    @Column(name = "updated_dt")
    private LocalDateTime updatedDt;

}
package com.zipkimi.repository;

import com.zipkimi.entity.BuilderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuilderRepository extends JpaRepository<BuilderEntity, Long> {

}

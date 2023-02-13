package com.safeinterior.board;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.safeinterior.entity.BoardEntity;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Long> {
	Page<BoardEntity> findByType(String type, Pageable pageable);
	BoardEntity findByUserId(long userId);
}

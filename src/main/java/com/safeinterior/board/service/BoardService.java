package com.safeinterior.board.service;

import java.time.ZonedDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.safeinterior.board.BoardRepository;
import com.safeinterior.board.dto.request.FraudPreventionRequest;
import com.safeinterior.board.dto.response.FraudPreventionResponse;
import com.safeinterior.entity.BoardEntity;
import com.safeinterior.user.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Transactional(rollbackOn = Exception.class)
@Slf4j
public class BoardService {
	private final BoardRepository boardRepository;
	private final UserRepository userRepository;
	private final ModelMapper modelMapper;

	public void setFraudPrevention(FraudPreventionRequest fraudPreventionRequest) {
		BoardEntity entity = BoardEntity.builder()
			.user(userRepository.findById(fraudPreventionRequest.getUserId()).get())
			.type("fraudPrevention")
			.title(fraudPreventionRequest.getTitle())
			.content(fraudPreventionRequest.getContent())
			.build();
		entity.setRegDt(ZonedDateTime.now());
		boardRepository.save(entity);
	}

	public Page<FraudPreventionResponse> getFraudPreventions(Pageable pageable) {
		Page<BoardEntity> pageBoardEntities = boardRepository.findByType("fraudPrevention", pageable);
		List<FraudPreventionResponse> fraudPreventionResponses = pageBoardEntities.getContent()
			.stream()
			.map(boardEntity -> modelMapper.map(boardEntity, FraudPreventionResponse.class))
			.toList();
		return new PageImpl<>(fraudPreventionResponses, pageable, pageBoardEntities.getTotalElements());
	}
}

package com.safeinterior.board.service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.safeinterior.board.BoardRepository;
import com.safeinterior.board.dto.request.FraudPreventionGetRequest;
import com.safeinterior.board.dto.request.FraudPreventionPatchRequest;
import com.safeinterior.board.dto.response.FraudPreventionGetResponse;
import com.safeinterior.board.dto.response.FraudPreventionGetsResponse;
import com.safeinterior.entity.BoardEntity;
import com.safeinterior.exception.BadRequestException;
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

	public void setFraudPrevention(FraudPreventionGetRequest fraudPreventionGetRequest) {
		BoardEntity entity = BoardEntity.builder()
			.user(userRepository.findById(fraudPreventionGetRequest.getUserId())
				.orElseThrow(() -> new RuntimeException("등록하지 않은 사용자 입니다.")))
			.type("fraudPrevention")
			.title(fraudPreventionGetRequest.getTitle())
			.subTitle(fraudPreventionGetRequest.getSubTitle())
			.content(fraudPreventionGetRequest.getContent())
			.build();
		entity.setRegDt(ZonedDateTime.now());
		boardRepository.save(entity);
	}

	public Page<FraudPreventionGetsResponse> getFraudPreventions(Pageable pageable) {
		Page<BoardEntity> pageBoardEntities = boardRepository.findByType("fraudPrevention", pageable);
		List<FraudPreventionGetsResponse> fraudPreventionGetsRespons = pageBoardEntities.getContent()
			.stream()
			.map(boardEntity -> modelMapper.map(boardEntity, FraudPreventionGetsResponse.class))
			.toList();
		return new PageImpl<>(fraudPreventionGetsRespons, pageable, pageBoardEntities.getTotalElements());
	}

	public FraudPreventionGetResponse getFraudPrevention(long id) {
		Optional<BoardEntity> boardEntityOptional = boardRepository.findById(id);
		FraudPreventionGetResponse response = null;
		if (!boardEntityOptional.isEmpty()) {
			response = modelMapper.map(boardEntityOptional.get(), FraudPreventionGetResponse.class);
		}
		return response;
	}

	public void patchFraudPrevention(long id, FraudPreventionPatchRequest requestDto) {
		Optional<BoardEntity> optionalBoard = boardRepository.findById(id);
		BoardEntity board;
		if (optionalBoard.isPresent()) {
			board = optionalBoard.get();
		} else {
			throw new BadRequestException();
		}
		board.setTitle(requestDto.getTitle() == null ? board.getTitle() : requestDto.getTitle());
		board.setSubTitle(requestDto.getSubTitle() == null ? board.getSubTitle() : requestDto.getSubTitle());
		board.setContent(requestDto.getContent() == null ? board.getContent() : requestDto.getContent());
		boardRepository.save(board);
	}
}

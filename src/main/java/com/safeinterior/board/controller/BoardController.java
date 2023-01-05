package com.safeinterior.board.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.safeinterior.board.dto.request.FraudPreventionRequest;
import com.safeinterior.board.dto.response.FraudPreventionResponse;
import com.safeinterior.board.service.BoardService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/board")
public class BoardController {
	private BoardService boardService;

	@GetMapping("/hello")
	public String hello() {
		return "hello";
	}

	@PostMapping("/basic")
	public ResponseEntity<Object> setBoard(FraudPreventionRequest fraudPreventionRequest) {
		fraudPreventionRequest.getContent();
		fraudPreventionRequest.getTitle();
		boardService.setFraudPrevention(fraudPreventionRequest);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/fraud-prevention")
	public ResponseEntity<HttpStatus> setFraudPrevention(HttpServletRequest request, FraudPreventionRequest fraudPreventionRequest) {
		boardService.setFraudPrevention(fraudPreventionRequest);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/fraud-prevention")
	public ResponseEntity<Page<FraudPreventionResponse>> getFraudPreventions(HttpServletRequest request,
		@PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
		return new ResponseEntity<>(boardService.getFraudPreventions(pageable), HttpStatus.OK);
	}

}

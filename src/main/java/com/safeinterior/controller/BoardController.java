package com.safeinterior.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.safeinterior.dto.request.FraudPreventionGetRequest;
import com.safeinterior.dto.request.FraudPreventionPatchRequest;
import com.safeinterior.dto.response.FraudPreventionGetResponse;
import com.safeinterior.dto.response.FraudPreventionGetsResponse;
import com.safeinterior.service.BoardService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@Api(value = "게시판 API - 피해예방법, 피해커퓨니티")
@RequestMapping(value = "/board")
public class BoardController {
	private BoardService boardService;

	@PostMapping("/basic")
	public ResponseEntity<Object> setBoard(FraudPreventionGetRequest requestDto) {
		boardService.setFraudPrevention(requestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * 피해예방법 - 등록
	 * */
	@PostMapping("/fraud-prevention")
	@ApiOperation(value = "피해예방법 - 등록")
	public ResponseEntity<HttpStatus> setFraudPrevention(HttpServletRequest request, FraudPreventionGetRequest requestDto) {
		boardService.setFraudPrevention(requestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * 피해예방법 - 목록 조회
	 * */
	@GetMapping("/fraud-prevention")
	@ApiOperation(value = "피해예방법 - 목록 조회")
	public ResponseEntity<Page<FraudPreventionGetsResponse>> getFraudPreventions(HttpServletRequest request,
		@PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
		return new ResponseEntity<>(boardService.getFraudPreventions(pageable), HttpStatus.OK);
	}

	/**
	 * 피해예방법 - 상세 조회
	 * @param id
	 * */
	@GetMapping("/fraud-prevention/{id}")
	@ApiOperation(value = "피해예방법 - 상세 조회")
	@ApiImplicitParam(name = "id", value = "게시판 아이디", required = true)
	public ResponseEntity<FraudPreventionGetResponse> getFraudPrevention(HttpServletRequest request, @PathVariable long id) {
		return new ResponseEntity<>(boardService.getFraudPrevention(id), HttpStatus.OK);
	}

	/**
	 * 피해예방법 - 수정
	 * @param id
	 * */
	@PatchMapping("/fraud-prevention/{id}")
	@ApiOperation(value = "피해예방법 - 수정")
	@ApiImplicitParam(name = "id", value = "게시판 아이디", required = true)
	public ResponseEntity<FraudPreventionGetResponse> patchFraudPrevention(HttpServletRequest request,
		@PathVariable long id, FraudPreventionPatchRequest requestDto) {
		boardService.patchFraudPrevention(id, requestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * 피해예방법 - 삭제
	 * @param id
	 * */
	@DeleteMapping("/fraud-prevention/{id}")
	@ApiOperation(value = "피해예방법 - 삭제")
	@ApiImplicitParam(name = "id", value = "게시판 아이디", required = true)
	public ResponseEntity<HttpStatus> deleteFraudPrevention(HttpServletRequest request, @PathVariable long id) {
		boardService.deleteFraudPrevention(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}


}

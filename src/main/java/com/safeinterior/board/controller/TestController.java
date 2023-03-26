package com.safeinterior.board.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.safeinterior.board.dto.request.TestDto;

import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@Api(value = "Test API")
public class TestController {

	@GetMapping("/health")
	public String health() {
		return "hello world! :)";
	}

	@GetMapping("/health/{name}")
	public String health(@PathVariable("name") TestDto dto) {
		return "hello "+ dto.getName() +" world! :)";
	}

}

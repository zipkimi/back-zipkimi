package com.safeinterior.board.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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

}

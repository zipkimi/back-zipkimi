package com.zipkimi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.zipkimi.dto.request.TestDto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@Api(value = "Test API")
public class TestController {

	// @GetMapping("/health")
	@GetHealthMapping
	public String health() {
		return "hello world! :)";
	}

	@ApiOperation(value = "Health Check API")
	@GetMapping("/health/{name}")
	public String health(@PathVariable("name") TestDto dto) {
		return "hello "+ dto.getName() +" world! :)";
	}

}

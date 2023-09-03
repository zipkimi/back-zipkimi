package com.zipkimi.global.controller;

import com.zipkimi.global.dto.request.TestDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Api(tags = "테스트")
public class TestController {

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

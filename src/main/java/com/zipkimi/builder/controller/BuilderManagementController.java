package com.zipkimi.builder.controller;

import com.zipkimi.builder.service.BuilderManagementService;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@Api(value = "시공사 회원 관리")
@RequestMapping("/api/v1/builders")
public class BuilderManagementController {

    private BuilderManagementService builderManagementService;

}

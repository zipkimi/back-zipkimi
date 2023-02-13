package com.safeinterior.user.controller;

import org.springframework.web.bind.annotation.RestController;

import com.safeinterior.user.service.UserService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class UserController {
	private UserService userService;

}

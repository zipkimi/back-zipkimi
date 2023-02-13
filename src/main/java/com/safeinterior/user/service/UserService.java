package com.safeinterior.user.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Transactional(rollbackOn = Exception.class)
@Slf4j
public class UserService {

}

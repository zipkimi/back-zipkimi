package com.zipkimi.builder.service;

import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class BuilderManagementService {

}

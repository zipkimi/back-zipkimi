package com.zipkimi.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UserRole {

    ROLE_USER("유저"),
    ROLE_BUILDER("시공사");

    private final String key;

}

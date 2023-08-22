package com.zipkimi.global.exception;

public class CustomUserNotFoundException extends RuntimeException {

    public CustomUserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomUserNotFoundException(String message) {
        super(message);
    }

    public CustomUserNotFoundException() {
        super();
    }
}
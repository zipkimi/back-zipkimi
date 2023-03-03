package com.safeinterior.common;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class Message {
	private final MessageSource messageSource;

	public String getMessage(String message) {
		return messageSource.getMessage(message, null, Locale.getDefault());
	}

	public String getMessage(String message, String[] args) {
		return messageSource.getMessage(message, args, Locale.getDefault());
	}
}

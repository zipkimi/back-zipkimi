package com.safeinterior.common;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class Message {
	private static MessageSource messageSource;

	public static String getMessage(String messageCode){
		return messageSource.getMessage(messageCode, null, Locale.getDefault());
	}
}

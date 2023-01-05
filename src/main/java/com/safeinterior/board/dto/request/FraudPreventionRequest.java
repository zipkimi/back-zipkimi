package com.safeinterior.board.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FraudPreventionRequest {
	private Long userId;

	private String title;

	private String content;
}

package com.safeinterior.board.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FraudPreventionGetRequest {
	private Long userId;

	private String title;

	private String subTitle;

	private String content;
}

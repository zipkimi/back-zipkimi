package com.safeinterior.board.dto.response;

import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FraudPreventionResponse {
	private Long id;

	private String title;

	private String content;

	private String type;

	private ZonedDateTime regDt;
}

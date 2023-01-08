package com.safeinterior.board.dto.response;

import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FraudPreventionGetResponse {
	private Long id;

	private String title;

	private String subTitle;

	private String content;

	private ZonedDateTime regDt;



}

package com.safeinterior.board.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FraudPreventionPatchRequest {
	private long boardId;

	private long userId;

	private String title;

	private String subTitle;

	private String content;
}

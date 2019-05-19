package com.demo.model.error;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorModel {
	private int errorCode;
	private String message;
	private String type;
	private String additionalInfo;
}

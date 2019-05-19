package com.demo.exception;

import lombok.Getter;

/**
 * A Base exception for all types of validation exception which
 * different exception sub types inherits.
 *
 */
@Getter
public class MoneyTransferBaseException extends RuntimeException {
	private int errorCode;
	private String type;

	public MoneyTransferBaseException(String message, int errorCode, String type) {
		super(message);
		this.errorCode = errorCode;
		this.type = type;
	}
}

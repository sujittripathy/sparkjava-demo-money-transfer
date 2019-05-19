package com.demo.exception;

import com.demo.util.ErrorEnum;

public class TransactionException extends MoneyTransferBaseException {
	public TransactionException(ErrorEnum errorEnum) {
		super(errorEnum.getMessage(), errorEnum.getCode(), errorEnum.getType());
	}

	public TransactionException(ErrorEnum errorEnum, String message) {
		super(message, errorEnum.getCode(), errorEnum.getType());
	}
}

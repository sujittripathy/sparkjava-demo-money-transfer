package com.demo.exception;

import com.demo.util.ErrorEnum;

public class AccountClosedException extends MoneyTransferBaseException {
	public AccountClosedException(ErrorEnum errorEnum) {
		super(errorEnum.getMessage(), errorEnum.getCode(), errorEnum.getType());
	}
}

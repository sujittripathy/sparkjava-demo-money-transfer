package com.demo.exception;

import com.demo.util.ErrorEnum;

public class AccountNotFoundException extends MoneyTransferBaseException {
	public AccountNotFoundException(ErrorEnum errorEnum) {
		super(errorEnum.getMessage(), errorEnum.getCode(), errorEnum.getType());
	}
}

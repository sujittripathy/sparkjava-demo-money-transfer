package com.demo.exception;

import com.demo.util.ErrorEnum;

/**
 * This exception is used for any kind of input data validation before processing
 * further such as format error, negative amount etc..
 */
public class DataValidationException extends MoneyTransferBaseException {
	public DataValidationException(ErrorEnum errorEnum) {
		super(errorEnum.getMessage(), errorEnum.getCode(), errorEnum.getType());
	}
}

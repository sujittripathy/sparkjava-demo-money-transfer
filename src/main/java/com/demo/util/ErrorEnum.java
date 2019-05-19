package com.demo.util;

/**
 * This is an error enum contains all validations errors such as
 * Common, Account and Transfer related.
 */
public enum ErrorEnum {
	//Common
	INVALID_ACCOUNT_FORMAT("Account format is not valid", 400, "DATA_VALIDATION"),
	FROM_TO_ACCOUNT_IS_SAME("From and To account can't be the same", 400, "DATA_VALIDATION"),
	INVALID_DATE_FORMAT("Input date format is not valid", 400, "DATA_VALIDATION"),
	PAST_TRANSFER_DATE("Scheduled transfer date cannot past", 400, "DATA_VALIDATION"),
	TRANSACTION_EXCEPTION("Exception while performing the transaction", 402, "TRANSACTION_ERROR"),

	//Account
	INITIAL_BALANCE_NEGATIVE("Account balance can't be negative at open", 400, "DATA_VALIDATION"),
	INVALID_ACCOUNT_TYPE("Account type should be Checking or Savings", 400, "DATA_VALIDATION"),
	ACCOUNT_CLOSING_ERROR("Unable to close the account.", 402, "ACCOUNT_CLOSING_ERROR"),

	//Transfer
	TRANSFER_AMOUNT_ZERO_OR_NEGATIVE("Transfer amount can't be negative", 400, "DATA_VALIDATION"),
	ACCOUNT_NOT_EXISTS("Account doesn't exists", 404, "DATA_VALIDATION"),
	AMOUNT_OVERDRAWN("Transfer amount exceeds available balance",402, "AMOUNT_OVERDRAWN"),
	;

	private String message;
	private int code;
	private String type;

	ErrorEnum(String message, int code, String type) {
		this.message = message;
		this.code = code;
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public int getCode() {
		return code;
	}

	public String getType() {
		return type;
	}
}

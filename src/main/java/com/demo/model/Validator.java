package com.demo.model;

import com.google.inject.Inject;
import com.demo.db.Account;
import com.demo.db.MoneyTransferDAO;
import com.demo.exception.AccountNotFoundException;
import com.demo.exception.DataValidationException;
import com.demo.model.request.AccountRequest;
import com.demo.model.request.TransferRequest;
import com.demo.util.ErrorEnum;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class Validator {

	private static final List<String> ACCT_TYPES = Arrays.asList("Checking","Savings");

	private final MoneyTransferDAO moneyTransferDAO;

	@Inject
	public Validator(MoneyTransferDAO moneyTransferDAO) {
		this.moneyTransferDAO = moneyTransferDAO;
	}

	public void validateAccountRequest(AccountRequest request) {
		//Data Validation
		if(request.getAmount().compareTo(BigDecimal.ZERO) < 0) {
			throw new DataValidationException(ErrorEnum.INITIAL_BALANCE_NEGATIVE);
		}
		if(!ACCT_TYPES.contains(request.getType())) {
			throw new DataValidationException(ErrorEnum.INVALID_ACCOUNT_TYPE);
		}
	}

	public void validateTransferRequest(TransferRequest request) {
		//Data Validation
		try{
			Integer.valueOf(request.getFromAccount());
			Integer.valueOf(request.getToAccount());
		} catch (NumberFormatException e) {
			throw new DataValidationException(ErrorEnum.INVALID_ACCOUNT_FORMAT);
		}
		if(request.getFromAccount().equalsIgnoreCase(request.getToAccount())) {
			throw new DataValidationException(ErrorEnum.FROM_TO_ACCOUNT_IS_SAME);
		}
		if(request.getAmount().compareTo(BigDecimal.ZERO) < 0) {
			throw new DataValidationException(ErrorEnum.TRANSFER_AMOUNT_ZERO_OR_NEGATIVE);
		}

		//Domain Validation
		Account fromAccount = moneyTransferDAO.findOneAccount(Integer.valueOf(request.getFromAccount()));
		if(fromAccount == null) {
			throw new AccountNotFoundException(ErrorEnum.ACCOUNT_NOT_EXISTS);
		}
		Account toAccount = moneyTransferDAO.findOneAccount(Integer.valueOf(request.getToAccount()));
		if(toAccount == null) {
			throw new AccountNotFoundException(ErrorEnum.ACCOUNT_NOT_EXISTS);
		}
	}

	public void validateAccount(String inputAccount) {
		Account account = moneyTransferDAO.findOneAccount(Integer.valueOf(inputAccount));
		if(account == null) {
			throw new AccountNotFoundException(ErrorEnum.ACCOUNT_NOT_EXISTS);
		}
	}
}

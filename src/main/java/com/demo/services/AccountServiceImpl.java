package com.demo.services;

import com.google.inject.Inject;
import com.demo.db.Account;
import com.demo.db.MoneyTransferDAO;
import com.demo.model.Validator;
import com.demo.model.request.AccountRequest;
import com.demo.model.response.AccountResponse;
import com.demo.model.response.AccountResponseList;
import com.demo.util.StatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class AccountServiceImpl implements AccountService {
	private static final Logger LOGGER = LoggerFactory.getLogger(AccountServiceImpl.class);

	private final MoneyTransferDAO moneyTransferDAO;
	private final Validator validator;

	@Inject
	public AccountServiceImpl(MoneyTransferDAO moneyTransferDAO, Validator validator) {
		this.moneyTransferDAO = moneyTransferDAO;
		this.validator = validator;
	}

	@Override
	public AccountResponse addAccount(AccountRequest account) {
		validator.validateAccountRequest(account);
		long accountNumber = moneyTransferDAO
				.insertAccount(account.getAlias(), account.getType(),
						account.getAmount(), account.getCurrency());
		LOGGER.info("Account created successfully. Account Number : {} ",accountNumber);
		return AccountResponse.builder()
				.status(StatusEnum.Account.ACTIVE.name())
				.accountNumber(String.valueOf(accountNumber)).build();
	}

	@Override
	public AccountResponseList fetchAllAccounts() {
		List<Account> accounts = moneyTransferDAO.fetchAllAccounts();
		List<AccountResponse> responses =
				accounts.stream().map(this::mapObject).collect(Collectors.toList());
		return AccountResponseList.builder().accounts(responses).build();
	}

	@Override
	public AccountResponse findOneAccount(String accountNumber) {
		Account account = moneyTransferDAO.findOneAccount(Integer.valueOf(accountNumber));
		return mapObject(account);
	}

	@Override
	public AccountResponse deposit(AccountRequest account) {
		validator.validateAccount(account.getAccountNumber());
		moneyTransferDAO.deposit(Integer.valueOf(account.getAccountNumber()), account.getAmount());
		return AccountResponse.builder()
				.accountNumber(account.getAccountNumber())
				.status(StatusEnum.Transaction.SUCCESS.name())
				.build();
	}

	@Override
	public AccountResponse withdraw(AccountRequest account) {
		validator.validateAccount(account.getAccountNumber());
		moneyTransferDAO.withdraw(Integer.valueOf(account.getAccountNumber()), account.getAmount());
		return AccountResponse.builder()
				.accountNumber(account.getAccountNumber())
				.status(StatusEnum.Transaction.SUCCESS.name())
				.build();
	}

	@Override
	public AccountResponse close(String accountNumber) {
		moneyTransferDAO.closeAccount(Integer.valueOf(accountNumber));
		return AccountResponse.builder()
				.accountNumber(accountNumber)
				.status(StatusEnum.Account.CLOSED.name())
				.build();
	}

	private AccountResponse mapObject(Account source) {
		return AccountResponse.builder()
				.accountNumber(String.valueOf(source.getId()))
				.status(source.getStatus())
				.alias(source.getAlias())
				.type(source.getType())
				.dateOpened(source.getOpenDate())
				.balance(source.getBalance())
				.currency(source.getCurrency()).build();
	}
}

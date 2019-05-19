package com.demo.services;

import com.demo.model.request.AccountRequest;
import com.demo.model.response.AccountResponse;
import com.demo.model.response.AccountResponseList;

public interface AccountService {
	AccountResponse addAccount(AccountRequest account);

	AccountResponseList fetchAllAccounts();

	AccountResponse findOneAccount(String accountNumber);

	AccountResponse deposit(AccountRequest account);

	AccountResponse withdraw(AccountRequest account);

	AccountResponse close(String accountNumber);
}

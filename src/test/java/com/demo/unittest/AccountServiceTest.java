package com.demo.unittest;

import com.demo.db.Account;
import com.demo.db.MoneyTransferDAO;
import com.demo.model.Validator;
import com.demo.model.request.AccountRequest;
import com.demo.model.response.AccountResponse;
import com.demo.model.response.AccountResponseList;
import com.demo.services.AccountServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {

	@Mock
	private MoneyTransferDAO moneyTransferDAO;

	@Mock
	private Validator validator;

	@InjectMocks
	private AccountServiceImpl accountServiceImpl;

	@Test
	public void testAddAccount() {
		when(moneyTransferDAO.insertAccount(anyString(), anyString(), any(),anyString()))
				.thenReturn(new Long(100010));
		AccountResponse response = accountServiceImpl.addAccount(mockAccountNewRequest());

		assertThat(response.getAccountNumber()).isEqualTo("100010");
	}

	@Test
	public void testFetchAllAccounts() {
		when(moneyTransferDAO.fetchAllAccounts()).thenReturn(mockAccountList());
		AccountResponseList accountResponseList = accountServiceImpl.fetchAllAccounts();

		assertThat(accountResponseList.getAccounts().size()).isEqualTo(2);
		assertThat(accountResponseList.getAccounts().get(0).getAccountNumber()).isEqualTo("1000");
	}

	@Test
	public void testFindOneAccount() {
		when(moneyTransferDAO.findOneAccount(1000)).thenReturn(mockAccountList().get(0));
		AccountResponse accountResponse = accountServiceImpl.findOneAccount("1000");

		assertThat(accountResponse.getAccountNumber()).isEqualTo("1000");
	}

	@Test
	public void testDeposit() {
		when(moneyTransferDAO.deposit(1000, new BigDecimal(20000))).thenReturn(true);
		AccountResponse response = accountServiceImpl.deposit(mockAccountTransRequest());

		assertThat(response.getStatus()).isEqualTo("SUCCESS");
		assertThat(response.getAccountNumber()).isEqualTo("1000");
	}

	@Test
	public void testWithdraw() {
		when(moneyTransferDAO.withdraw(1000, new BigDecimal(20000))).thenReturn(true);
		AccountResponse response = accountServiceImpl.withdraw(mockAccountTransRequest());

		assertThat(response.getStatus()).isEqualTo("SUCCESS");
		assertThat(response.getAccountNumber()).isEqualTo("1000");
	}

	private AccountRequest mockAccountNewRequest() {
		return AccountRequest.builder()
				.alias("My Mock Checking Account")
				.type("Checking")
				.amount(new BigDecimal("100"))
				.currency("USD")
				.build();
	}

	private List<Account> mockAccountList() {
		List<Account> accountList = new ArrayList<>();
		accountList.add(Account.builder()
				.id(1000)
				.alias("My checking account")
				.balance(new BigDecimal(100))
				.currency("USD")
				.status("ACTIVE")
				.type("Checking")
				.openDate(LocalDateTime.now().toString())
				.build());
		accountList.add(Account.builder()
				.id(1001)
				.alias("My savings account")
				.balance(new BigDecimal(500))
				.currency("USD")
				.status("ACTIVE")
				.type("Savings")
				.openDate(LocalDateTime.now().toString())
				.build());
		return accountList;
	}

	private AccountRequest mockAccountTransRequest() {
		return AccountRequest.builder()
				.accountNumber("1000")
				.amount(new BigDecimal("20000"))
				.build();
	}
}

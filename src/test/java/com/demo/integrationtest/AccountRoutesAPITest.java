package com.demo.integrationtest;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.demo.MoneyTransferMain;
import com.demo.model.error.ErrorModel;
import com.demo.model.request.AccountRequest;
import com.demo.model.response.AccountResponse;
import com.demo.model.response.AccountResponseList;
import com.demo.util.ErrorEnum;
import com.demo.util.StatusEnum;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.Spark;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This test covers all account related functionalities such as
 *  Add account, deposit, withdraw, search etc.
 */
public class AccountRoutesAPITest {

	@BeforeClass
	public static void beforeClass() throws Exception {
		MoneyTransferMain.main(null);
		Spark.awaitInitialization();
	}

	@Test
	public void addAccountTest() throws UnirestException {
		HttpResponse<String> response = Unirest.post("http://localhost:4567/v1/account/add")
				.header("accept", "application/json")
				.body(new Gson().toJson(mockAccount()))
				.asString();
		AccountResponse accountResponse = new Gson().fromJson(response.getBody(), AccountResponse.class);

		assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED_201);
		assertThat(accountResponse.getStatus()).isEqualTo(StatusEnum.Account.ACTIVE.name());
	}

	@Test
	public void verifyNewAccountBalanceIsPositive() throws UnirestException {
		AccountRequest request = mockAccount();
		request.setAmount(new BigDecimal(-10));
		HttpResponse<String> response = Unirest.post("http://localhost:4567/v1/account/add")
				.header("accept", "application/json")
				.body(new Gson().toJson(request))
				.asString();
		ErrorModel errorModel =
				new Gson().fromJson(response.getBody(), ErrorModel.class);

		assertThat(errorModel.getErrorCode()).isEqualTo(ErrorEnum.INITIAL_BALANCE_NEGATIVE.getCode());
	}

	@Test
	public void verifyCorrectAccountType() throws UnirestException {
		AccountRequest request = mockAccount();
		request.setType("investment");
		HttpResponse<String> response = Unirest.post("http://localhost:4567/v1/account/add")
				.header("accept", "application/json")
				.body(new Gson().toJson(request))
				.asString();
		ErrorModel errorModel =
				new Gson().fromJson(response.getBody(), ErrorModel.class);

		assertThat(errorModel.getErrorCode()).isEqualTo(ErrorEnum.INVALID_ACCOUNT_TYPE.getCode());
	}

	@Test
	public void verifyListAllAccounts() throws UnirestException {
		HttpResponse<String> response = Unirest.get("http://localhost:4567/v1/account/all")
				.asString();
		AccountResponseList responseList =
					new Gson().fromJson(response.getBody(), AccountResponseList.class);

		assertThat(responseList.getAccounts().get(0).getAccountNumber()).isEqualTo("100000");
	}

	@Test
	public void verifyQueryOneAccount() throws UnirestException {
		HttpResponse<String> response = Unirest.get("http://localhost:4567/v1/account/100000")
				.asString();
		AccountResponse account =
				new Gson().fromJson(response.getBody(), AccountResponse.class);

		assertThat(account.getAccountNumber()).isEqualTo("100000");
		assertThat(account.getStatus()).isEqualTo("ACTIVE");
	}

	@Test
	public void verifyQueryAccountNotExists() throws UnirestException {
		HttpResponse<String> response = Unirest.get("http://localhost:4567/v1/account/100009")
				.asString();

		ErrorModel errorModel =
				new Gson().fromJson(response.getBody(), ErrorModel.class);

		assertThat(errorModel.getErrorCode()).isEqualTo(ErrorEnum.ACCOUNT_NOT_EXISTS.getCode());
	}

	@Test
	public void verifySuccessfulDeposit() throws UnirestException {
		//Create a new account
		HttpResponse<String> resCreate = Unirest.post("http://localhost:4567/v1/account/add")
				.header("accept", "application/json")
				.body(new Gson().toJson(mockAccount()))
				.asString();
		AccountResponse acctResponse = new Gson().fromJson(resCreate.getBody(), AccountResponse.class);

		//Make a deposit
		AccountRequest depReq = depositRequest(acctResponse.getAccountNumber(), new BigDecimal(900));
		HttpResponse<String> resDep = Unirest.post("http://localhost:4567/v1/account/deposit")
				.header("accept", "application/json")
				.body(new Gson().toJson(depReq))
				.asString();
		AccountResponse acctDepResponse = new Gson().fromJson(resDep.getBody(), AccountResponse.class);

		//Query account details for updated balance check
		HttpResponse<String> response =
				Unirest.get("http://localhost:4567/v1/account/"+acctResponse.getAccountNumber()).asString();
		AccountResponse account =
				new Gson().fromJson(response.getBody(), AccountResponse.class);

		assertThat(acctDepResponse.getStatus()).isEqualTo("SUCCESS");
		assertThat(account.getBalance()).isEqualByComparingTo(new BigDecimal(2000));
	}

	@Test
	public void verifySuccessfulWithdraw() throws UnirestException {
		//Create a new account
		HttpResponse<String> resCreate = Unirest.post("http://localhost:4567/v1/account/add")
				.header("accept", "application/json")
				.body(new Gson().toJson(mockAccount()))
				.asString();
		AccountResponse acctResponse = new Gson().fromJson(resCreate.getBody(), AccountResponse.class);

		//Make a withdraw
		AccountRequest depReq = depositRequest(acctResponse.getAccountNumber(), new BigDecimal(100));
		HttpResponse<String> resDep = Unirest.post("http://localhost:4567/v1/account/withdraw")
				.header("accept", "application/json")
				.body(new Gson().toJson(depReq))
				.asString();
		AccountResponse acctDepResponse = new Gson().fromJson(resDep.getBody(), AccountResponse.class);

		//Query account details for updated balance check
		HttpResponse<String> response =
				Unirest.get("http://localhost:4567/v1/account/"+acctResponse.getAccountNumber()).asString();
		AccountResponse account =
				new Gson().fromJson(response.getBody(), AccountResponse.class);

		assertThat(acctDepResponse.getStatus()).isEqualTo("SUCCESS");
		assertThat(account.getBalance()).isEqualByComparingTo(new BigDecimal(1000));
	}

	@Test
	public void verifyOverdrawnWhileWithdraw() throws UnirestException {
		//Create a new account
		HttpResponse<String> resCreate = Unirest.post("http://localhost:4567/v1/account/add")
				.header("accept", "application/json")
				.body(new Gson().toJson(mockAccount()))
				.asString();
		AccountResponse acctResponse = new Gson().fromJson(resCreate.getBody(), AccountResponse.class);

		//Make a withdraw > balance
		AccountRequest depReq = withdrawRequest(acctResponse.getAccountNumber(), new BigDecimal(1100.01));
		HttpResponse<String> resDep = Unirest.post("http://localhost:4567/v1/account/withdraw")
				.header("accept", "application/json")
				.body(new Gson().toJson(depReq))
				.asString();
		ErrorModel errorResponse = new Gson().fromJson(resDep.getBody(), ErrorModel.class);

		assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorEnum.TRANSACTION_EXCEPTION.getCode());
		assertThat(errorResponse.getType()).isEqualTo(ErrorEnum.TRANSACTION_EXCEPTION.getType());
	}

	@Test
	public void verifyCloseAccount() throws UnirestException {
		//Create a new account
		HttpResponse<String> resCreate = Unirest.post("http://localhost:4567/v1/account/add")
				.header("accept", "application/json")
				.body(new Gson().toJson(mockAccount()))
				.asString();
		AccountResponse acctResponse = new Gson().fromJson(resCreate.getBody(), AccountResponse.class);

		String accountNumber = acctResponse.getAccountNumber();
		//Close the account and assert response
		HttpResponse<String> response =
				Unirest.put("http://localhost:4567/v1/account/"+accountNumber+"/close")
						.asString();
		AccountResponse account = new Gson().fromJson(response.getBody(), AccountResponse.class);

		assertThat(account.getStatus()).isEqualTo(StatusEnum.Account.CLOSED.name());
	}

	@AfterClass
	public static void afterClass() throws InterruptedException {
		Spark.stop();
		Thread.sleep(2000);
	}

	private AccountRequest mockAccount() {
		return AccountRequest.builder()
				.alias("My Test Checking Account")
				.type("Checking")
				.amount(new BigDecimal("1100.00"))
				.currency("USD")
				.build();
	}

	private AccountRequest depositRequest(String accountNumber, BigDecimal amount) {
		return AccountRequest.builder()
				.accountNumber(accountNumber)
				.amount(amount)
				.build();
	}

	private AccountRequest withdrawRequest(String accountNumber, BigDecimal amount) {
		return AccountRequest.builder()
				.accountNumber(accountNumber)
				.amount(amount)
				.build();
	}
}

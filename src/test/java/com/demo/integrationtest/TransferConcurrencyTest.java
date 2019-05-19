package com.demo.integrationtest;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.demo.MoneyTransferMain;
import com.demo.model.request.AccountRequest;
import com.demo.model.request.TransferRequest;
import com.demo.model.response.AccountResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.Spark;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This class tests for concurrency tests with 100 threads via
 * an executor service fixed thread pool. after the transactions completes
 * asserts the remaining balance whether correct or not.
 *
 * The transfer transaction is being made synchronized after concurrency test.
 *
 */
public class TransferConcurrencyTest {
	@BeforeClass
	public static void beforeClass() throws Exception {
		MoneyTransferMain.main(null);
		Spark.awaitInitialization();
	}

	@Test
	public void verifyBalanceAfterConcurrencyTransfer() throws UnirestException, InterruptedException {
		// Create account#1 with deposit of $30000
		HttpResponse<String> responseOne = Unirest.post("http://localhost:4567/v1/account/add")
				.header("accept", "application/json")
				.body(new Gson().toJson(accountOne()))
				.asString();
		AccountResponse acctResponseOne = new Gson().fromJson(responseOne.getBody(), AccountResponse.class);

		// Create account#2 with a deposit of $0
		HttpResponse<String> responseTwo = Unirest.post("http://localhost:4567/v1/account/add")
				.header("accept", "application/json")
				.body(new Gson().toJson(accountTwo()))
				.asString();
		AccountResponse acctResponseTwo = new Gson().fromJson(responseTwo.getBody(), AccountResponse.class);

		// Create 100 threads and transfer $10.00 from account 1 to account 2
		final int threads = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(threads);
		for(int i=0;i<threads;i++) {
			executorService.submit(() -> {
				TransferRequest transferRequest =
						transferFromAccountOneToAccountTwo(acctResponseOne.getAccountNumber(),
								acctResponseTwo.getAccountNumber());
				try {
					Unirest.post("http://localhost:4567/v1/transfer/immediate")
							.header("accept", "application/json")
							.body(new Gson().toJson(transferRequest))
							.asString();
				} catch (UnirestException e) {
					e.printStackTrace();
				}
			});
		}
		executorService.shutdown();
		executorService.awaitTermination(10, TimeUnit.SECONDS);

		// Check the remaining balance in account#1 is $29000 (30000 - (100 * 10)) in Account Table
		final String accountOne = acctResponseOne.getAccountNumber();
		HttpResponse<String> responseOneAfter = Unirest.get("http://localhost:4567/v1/account/"+accountOne)
				.asString();
		AccountResponse accountOneAfter =
				new Gson().fromJson(responseOneAfter.getBody(), AccountResponse.class);
		// Check the new balance in account#2 is #100
		final String accountTwo = acctResponseTwo.getAccountNumber();
		HttpResponse<String> responseTwoAfter = Unirest.get("http://localhost:4567/v1/account/"+accountTwo)
				.asString();
		AccountResponse accountTwoAfter =
				new Gson().fromJson(responseTwoAfter.getBody(), AccountResponse.class);

		// Finally Check the total records in Transfer Table Should be 100 with $10 each
		// Account 1 - 29900 & Account 2- 100
		assertThat(accountOneAfter.getBalance()).isEqualByComparingTo(new BigDecimal(29000));
		assertThat(accountTwoAfter.getBalance()).isEqualByComparingTo(new BigDecimal(1000));
	}

	@AfterClass
	public static void afterClass() throws InterruptedException {
		Spark.stop();
		Thread.sleep(2000);
	}

	private AccountRequest accountOne() {
		return AccountRequest.builder()
				.alias("My Checking Account - One")
				.type("Checking")
				.amount(new BigDecimal(30000))
				.currency("USD")
				.build();
	}

	private AccountRequest accountTwo() {
		return AccountRequest.builder()
				.alias("My Savings Account - Two")
				.type("Savings")
				.amount(new BigDecimal(0))
				.currency("USD")
				.build();
	}

	private TransferRequest transferFromAccountOneToAccountTwo(String fromAcct, String toAcct) {
		return TransferRequest.builder()
				.fromAccount(fromAcct)
				.toAccount(toAcct)
				.amount(BigDecimal.valueOf(10))
				.currency("USD")
				.build();
	}
}

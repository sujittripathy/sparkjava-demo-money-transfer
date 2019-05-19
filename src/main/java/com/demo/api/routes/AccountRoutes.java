package com.demo.api.routes;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.demo.api.handler.JsonTransformer;
import com.demo.model.request.AccountRequest;
import com.demo.services.AccountService;
import org.eclipse.jetty.http.HttpStatus;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

/**
 * This class serves all account related resources such as add new account,
 * fetch existing account details, deposit and withdraw like functionality.
 *
 * @author Sujit Tripathy
 */
public class AccountRoutes {

	@Inject
	private AccountService accountService;

	public AccountRoutes() {
		initializeAccountRoutes();
	}

	public void initializeAccountRoutes() {
		post("/v1/account/add", (request, response) -> {
			response.status(HttpStatus.CREATED_201);

			AccountRequest account = new Gson().fromJson(request.body(), AccountRequest.class);
			return accountService.addAccount(account);
		}, JsonTransformer::toJson);

		get("/v1/account/all", (request, response) -> accountService.fetchAllAccounts(),
				JsonTransformer::toJson);

		get("/v1/account/:id", ((request, response) ->
						accountService.findOneAccount(request.params(":id"))),
				JsonTransformer::toJson);

		post("/v1/account/deposit", (request, response) -> {
			AccountRequest account = new Gson().fromJson(request.body(), AccountRequest.class);
			return accountService.deposit(account);
		}, JsonTransformer::toJson);

		post("/v1/account/withdraw", (request, response) -> {
			AccountRequest account = new Gson().fromJson(request.body(), AccountRequest.class);
			return accountService.withdraw(account);
		}, JsonTransformer::toJson);

		put("/v1/account/:id/close", (request, response) ->
				accountService.close(request.params(":id")), JsonTransformer::toJson);
	}
}

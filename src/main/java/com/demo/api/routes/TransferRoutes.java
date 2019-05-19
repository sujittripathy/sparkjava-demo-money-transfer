package com.demo.api.routes;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.demo.api.handler.JsonTransformer;
import com.demo.model.request.TransferRequest;
import com.demo.services.TransferService;

import static spark.Spark.get;
import static spark.Spark.post;

/**
 * This class serves all transfer related resources such as transfer immediate,
 * finding an existing transfer detail.
 *
 * @author Sujit Tripathy
 */
public class TransferRoutes {
	@Inject
	private TransferService transferService;

	public TransferRoutes() {
		initializeTransferRoutes();
	}

	public void initializeTransferRoutes() {
		post("/v1/transfer/immediate", ((request, response) -> {
			TransferRequest transferRequest = new Gson().fromJson(request.body(), TransferRequest.class);
			return  transferService.transfer(transferRequest);
		}), JsonTransformer::toJson);

		post("/v1/transfer/schedule", ((request, response) -> {
			TransferRequest transferRequest = new Gson().fromJson(request.body(), TransferRequest.class);
			return  transferService.transferSchedule(transferRequest);
		}), JsonTransformer::toJson);

		get("/v1/transfer/:id", ((request, response) ->
						transferService.findTransferByTransId(Integer.valueOf(request.params(":id")))),
				JsonTransformer::toJson);
	}
}

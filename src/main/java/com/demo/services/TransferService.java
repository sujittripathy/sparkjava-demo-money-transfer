package com.demo.services;

import com.demo.model.request.TransferRequest;
import com.demo.model.response.TransferResponse;

public interface TransferService {
	TransferResponse transfer(TransferRequest request);

	TransferResponse transferSchedule(TransferRequest request);

	TransferResponse findTransferByTransId(Integer transId);
}

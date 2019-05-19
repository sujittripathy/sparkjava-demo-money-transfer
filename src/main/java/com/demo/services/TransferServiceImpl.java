package com.demo.services;

import com.google.inject.Inject;
import com.demo.db.MoneyTransferDAO;
import com.demo.db.Transfer;
import com.demo.model.Validator;
import com.demo.model.request.TransferRequest;
import com.demo.model.response.TransferResponse;
import com.demo.util.Utils;

import java.time.LocalDate;

public class TransferServiceImpl implements TransferService {

	private final MoneyTransferDAO moneyTransferDAO;
	private final Validator validator;


	@Inject
	public TransferServiceImpl(MoneyTransferDAO moneyTransferDAO, Validator validator) {
		this.moneyTransferDAO = moneyTransferDAO;
		this.validator = validator;
	}

	@Override
	public TransferResponse transfer(TransferRequest request) {
		validator.validateTransferRequest(request);
		Transfer transfer = moneyTransferDAO.transfer(Integer.valueOf(request.getFromAccount()),
				Integer.valueOf(request.getToAccount()), request.getAmount(),
				request.getCurrency());
		return mapObject(transfer);
	}

	@Override
	public TransferResponse transferSchedule(TransferRequest request) {
		validator.validateTransferRequest(request);
		LocalDate schDate = Utils.parseStringToDate(request.getScheduleDate());
		Transfer transfer = moneyTransferDAO.transferSchedule(Integer.valueOf(request.getFromAccount()),
				Integer.valueOf(request.getToAccount()), request.getAmount(),
				schDate, request.getCurrency());
		return mapObject(transfer);
	}

	@Override
	public TransferResponse findTransferByTransId(Integer transId) {
		Transfer transfer = moneyTransferDAO.findOneTransfer(transId);
		return mapObject(transfer);
	}

	private TransferResponse mapObject(Transfer transfer) {
		return TransferResponse.builder()
				.status(transfer.getStatus())
				.transactionId(String.valueOf(transfer.getTransId()))
				.transactionDate(transfer.getTransDate() != null? transfer.getTransDate().toString() : null)
				.scheduledDate(transfer.getScheduleDate() != null? transfer.getScheduleDate().toString() : null)
				.amount(transfer.getAmount())
				.currency(transfer.getCurrency())
				.fromAccount(transfer.getFromAcct() != null ? String.valueOf(transfer.getFromAcct()) : null )
				.toAccount(transfer.getToAcct() != null ? String.valueOf(transfer.getToAcct()) : null)
				.build();

	}
}

package com.demo.configuration;

import com.google.inject.AbstractModule;
import com.demo.db.MoneyTransferDAO;
import com.demo.db.MoneyTransferDAOImpl;
import com.demo.model.Validator;
import com.demo.services.AccountService;
import com.demo.services.AccountServiceImpl;
import com.demo.services.TransferService;
import com.demo.services.TransferServiceImpl;

public class GuiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(AccountService.class).to(AccountServiceImpl.class);
		bind(TransferService.class).to(TransferServiceImpl.class);
		bind(MoneyTransferDAO.class).to(MoneyTransferDAOImpl.class);
		bind(Validator.class);
	}
}

package com.demo;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.demo.api.routes.AccountRoutes;
import com.demo.api.routes.TransferRoutes;
import com.demo.configuration.APIConfiguration;
import com.demo.configuration.GuiceModule;
import com.demo.model.Validator;
import com.demo.services.AccountServiceImpl;
import com.demo.services.TransferServiceImpl;

public class MoneyTransferMain {

	public static void main(String[] args) throws Exception {
		initializeInjectors();
		//Initialize API Configuration
		new APIConfiguration();
	}

	private static void initializeInjectors() {
		//Initialize DI modules
		Injector injector = Guice.createInjector(new GuiceModule());
		injector.getInstance(AccountRoutes.class);
		injector.getInstance(TransferRoutes.class);
		injector.getInstance(AccountServiceImpl.class);
		injector.getInstance(TransferServiceImpl.class);
		injector.getInstance(Validator.class);
	}
}

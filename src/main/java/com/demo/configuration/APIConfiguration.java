package com.demo.configuration;

import com.demo.api.filter.AfterFilter;
import com.demo.api.handler.ExceptionHandler;
import com.demo.api.routes.AccountRoutes;
import com.demo.api.routes.TransferRoutes;

public class APIConfiguration {
	public APIConfiguration() throws Exception {
		Class[] classes = {AccountRoutes.class, TransferRoutes.class,
								ExceptionHandler.class, AfterFilter.class};
		for(Class clazz: classes) {
			clazz.getDeclaredConstructors()[0].newInstance();
		}
	}
}

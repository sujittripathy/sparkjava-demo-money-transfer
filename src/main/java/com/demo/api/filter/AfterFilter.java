package com.demo.api.filter;

import static spark.Spark.after;

public class AfterFilter {
	public AfterFilter() {
		after(((request, response) -> {
			response.header("Content-Type", "application/json");
		}));
	}
}

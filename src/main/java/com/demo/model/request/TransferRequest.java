package com.demo.model.request;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TransferRequest {
	private String fromAccount;
	private String toAccount;
	private BigDecimal amount;
	private String currency;
	private String scheduleDate;
}

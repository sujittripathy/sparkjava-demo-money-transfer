package com.demo.model.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TransferResponse {
	private String id;
	private String status;
	private String transactionId;
	private String transactionDate;
	private String scheduledDate;
	private String fromAccount;
	private String toAccount;
	private BigDecimal amount;
	private String currency;
}

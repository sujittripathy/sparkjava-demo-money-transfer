package com.demo.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponse {
	private String accountNumber;
	private String dateOpened;
	private String alias;
	private String type;
	private BigDecimal balance;
	private String currency;
	private String status;
	private String accountStatus;
}

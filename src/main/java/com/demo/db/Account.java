package com.demo.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
	private Integer id;
	private String status;
	private String openDate;
	private String alias;
	private String type;
	private BigDecimal balance;
	private String currency;
}

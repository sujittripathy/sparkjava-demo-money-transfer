package com.demo.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transfer {
	private Integer fromAcct;
	private Integer toAcct;
	private String status;
	private Long transId;
	private LocalDateTime transDate;
	private BigDecimal amount;
	private String currency;
	private LocalDate scheduleDate;
}

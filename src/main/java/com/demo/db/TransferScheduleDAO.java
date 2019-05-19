package com.demo.db;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface TransferScheduleDAO {
	@SqlUpdate("insert into transfer_schedule(from_acct,to_acct,amount,currency,sch_transfer_date, status)" +
			" values (:from_acct, :to_acct, :amount, :currency, :sch_transfer_date, :status)")
	@GetGeneratedKeys("trans_id")
	long transferScheduled(@Bind("from_acct") Integer fromAccount, @Bind("to_acct") Integer toAccount,
	                       @Bind("amount") BigDecimal amount, @Bind("currency") String currency,
	                       @Bind("sch_transfer_date") LocalDate sch_transfer_date, @Bind("status") String status);
}

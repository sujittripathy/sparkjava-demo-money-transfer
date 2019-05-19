package com.demo.db;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.Timestamped;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.math.BigDecimal;

public interface TransferDAO {
	@SqlUpdate("insert into transfer(from_acct,to_acct,amount,currency,trans_date, status)" +
			" values (:from_acct, :to_acct, :amount, :currency, :now, :status)")
	@GetGeneratedKeys("trans_id")
	@Timestamped
	long insert(@Bind("from_acct") Integer fromAccount, @Bind("to_acct") Integer toAccount,
	            @Bind("amount") BigDecimal amount, @Bind("currency") String currency,
	            @Bind("status") String status);

	@SqlQuery("select * from transfer where trans_id = :id")
	@RegisterBeanMapper(Transfer.class)
	Transfer getTransferByTransId(@Bind("id") Integer id);
}

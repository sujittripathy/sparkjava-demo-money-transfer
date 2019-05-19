package com.demo.db;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.Timestamped;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountDAO {

	@SqlUpdate("insert into account (alias,type,balance,open_date,currency) VALUES (:alias, :type, :balance, :now, :currency)")
	@GetGeneratedKeys("id")
	@Timestamped
	long insert(@Bind("alias") String alias, @Bind("type") String type, @Bind("balance") BigDecimal balance,
	            @Bind("currency") String currency);

	@SqlQuery("select * from account order by open_date")
	@RegisterBeanMapper(Account.class)
	List<Account> listAccounts();

	@SqlQuery("select * from account where id = :id")
	@RegisterBeanMapper(Account.class)
	Optional<Account> getAccountById(@Bind("id") Integer id);

	@SqlUpdate("update account set balance = :balance where id = :account")
	boolean updateAccount(@Bind("account") Integer account, @Bind("balance") BigDecimal balance);

	@SqlUpdate("update account set status = :status where id = :account")
	boolean closeAccount(@Bind("account") Integer account, @Bind("status") String status);
}

package com.demo.db;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface MoneyTransferDAO {
	long insertAccount(String alias, String type, BigDecimal balance, String currency);

	List<Account> fetchAllAccounts();

	Account findOneAccount(Integer id);

	boolean deposit(Integer id, BigDecimal amount);

	boolean withdraw(Integer id, BigDecimal amount);

	boolean closeAccount(Integer id);

	Transfer transfer(Integer fromAccount, Integer toAccount, BigDecimal amount, String currency);

	Transfer transferSchedule(Integer fromAccount, Integer toAccount, BigDecimal amount, LocalDate transDate, String currency);

	Transfer findOneTransfer(Integer transId);
}

package com.demo.db;

import com.demo.configuration.JDBIConfiguration;
import com.demo.exception.AccountClosedException;
import com.demo.exception.AccountNotFoundException;
import com.demo.exception.AmountOverdrawnException;
import com.demo.exception.TransactionException;
import com.demo.util.ErrorEnum;
import com.demo.util.StatusEnum;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The DB helper class for managing the account and transfer related
 * operations. This class uses jdbi APIs for database connections and execution
 * or queries. The transfer operation is atomic and managed by lock.
 *
 * @author Sujit Tripathy
 */
public class MoneyTransferDAOImpl implements MoneyTransferDAO {
	private static final Logger LOGGER = LoggerFactory.getLogger(MoneyTransferDAOImpl.class);
	private Jdbi jdbi = JDBIConfiguration.getJdbi();
	private final ReentrantLock lock;

	public MoneyTransferDAOImpl() {
		this.lock = new ReentrantLock();
	}

	@Override
	public long insertAccount(String alias, String type, BigDecimal balance, String currency) {
		return jdbi.withExtension(AccountDAO.class,
				dao -> dao.insert(alias, type, balance, currency));
	}

	@Override
	public List<Account> fetchAllAccounts() {
		return jdbi.withExtension(AccountDAO.class, AccountDAO::listAccounts);
	}

	@Override
	public Account findOneAccount(Integer id) {
		return jdbi.withExtension(AccountDAO.class,
				dao -> dao.getAccountById(id)
						.orElseThrow(() -> new AccountNotFoundException(ErrorEnum.ACCOUNT_NOT_EXISTS)));
	}


	@Override
	public boolean deposit(Integer id, BigDecimal amount) {
		try {
			lock.lock();
			Account account = findOneAccount(id);
			return jdbi.withExtension(AccountDAO.class, dao -> {
				BigDecimal toAcctNewBal = account.getBalance().add(amount);
				boolean status = dao.updateAccount(id, toAcctNewBal);
				LOGGER.info("Deposit of {} is successful to account {}. New Balance : {}",
						amount, id, toAcctNewBal);
				return status;
			});
		} catch (Exception e) {
			LOGGER.error("Error while proceeding with deposit. account : {}", id);
			throw new TransactionException(ErrorEnum.TRANSACTION_EXCEPTION, e.getMessage());
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean withdraw(Integer id, BigDecimal amount) {
		try {
			lock.lock();
			Account account = findOneAccount(id);
			return jdbi.withExtension(AccountDAO.class, dao -> {
				BigDecimal toAcctNewBal = account.getBalance().subtract(amount);
				if (toAcctNewBal.compareTo(BigDecimal.ZERO) < 0) {
					throw new AmountOverdrawnException(ErrorEnum.AMOUNT_OVERDRAWN);
				}
				boolean status = dao.updateAccount(id, toAcctNewBal);
				LOGGER.info("Withdraw of {} is successful to account {}. New Balance : {}",
						amount, id, toAcctNewBal);
				return status;
			});
		} catch (Exception e) {
			LOGGER.error("Error while proceeding with withdraw. account : {}", id +
				". Exception: " + e.getMessage());
			throw new TransactionException(ErrorEnum.TRANSACTION_EXCEPTION, e.getMessage());
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean closeAccount(Integer id) {
		try {
			return jdbi.withExtension(AccountDAO.class, dao ->
					dao.closeAccount(id, StatusEnum.Account.CLOSED.name()));
		} catch (Exception e) {
			LOGGER.error("Error while closing the account : {}", id);
			throw new AccountClosedException(ErrorEnum.ACCOUNT_CLOSING_ERROR);
		}
	}

	@Override
	public Transfer transfer(Integer fromAccount, Integer toAccount, BigDecimal amount, String currency) {
		try {
			lock.lock();
			return jdbi.inTransaction(handle -> {
				// Subtract from Account 1, throw exception is amount > balance
				AccountDAO accountDAO = handle.attach(AccountDAO.class);

				BigDecimal balFrom = getAccountBalance(fromAccount).subtract(amount);
				if (balFrom.compareTo(BigDecimal.ZERO) < 0) {
					throw new AmountOverdrawnException(ErrorEnum.AMOUNT_OVERDRAWN);
				}
				accountDAO.updateAccount(fromAccount, balFrom);

				// Add in Account 2
				BigDecimal balTo = getAccountBalance(toAccount).add(amount);
				accountDAO.updateAccount(toAccount, balTo);

				// Insert the record in Transfer table as Success
				TransferDAO transferDAO = handle.attach(TransferDAO.class);

				long transId = transferDAO.insert(fromAccount,
						toAccount, amount, currency,  StatusEnum.Transfer.COMPLETED.name());
				LOGGER.info("Transfer successful. amount {} has been transferred from {} to {} account." +
								"From account balance : {}, To account balance : {}",
						amount, fromAccount, toAccount, balFrom, balTo);
				return Transfer.builder()
						.transDate(LocalDateTime.now())
						.transId(transId)
						.status(StatusEnum.Transfer.COMPLETED.name()).build();
			});
		} catch(AmountOverdrawnException e) {
			LOGGER.info("Transfer amount exceeded from account balance. amount {} cannot be transfer from " +
							"  {} to {} account.", amount, fromAccount, toAccount);
			throw e;
		} catch (Exception e) {
			LOGGER.info("Transfer has been failed due to exception. amount {} hasn't been transferred " +
							"  from {} to {} account. Exception is : " + e.getMessage(),
					amount, fromAccount, toAccount);
			jdbi.open().attach(TransferDAO.class).insert(fromAccount, toAccount, amount,
								currency, StatusEnum.Transfer.FAILED.name());
			throw e;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * This is for scheduled transfer which inserts in scheduled table and a separate
	 * batch job can be written to pull the future transfers from scheduled transfer
	 * table and process the transfer. The batch job is not implemented and can be
	 * added in future
	 *
	 * @param fromAccount fromAccount
	 * @param toAccount toAccount
	 * @param amount amount
	 * @param transDate transactionDate
	 * @param transDate transDate
	 * @return Transfer
	 */
	@Override
	public Transfer transferSchedule(Integer fromAccount, Integer toAccount, BigDecimal amount, LocalDate transDate, String currency) {
		try {
			lock.lock();
			return jdbi.withExtension(TransferScheduleDAO.class, dao -> {
				BigDecimal balFrom = getAccountBalance(fromAccount).subtract(amount);
				if (balFrom.compareTo(BigDecimal.ZERO) < 0) {
					throw new AmountOverdrawnException(ErrorEnum.AMOUNT_OVERDRAWN);
				}
				long id = dao.transferScheduled(fromAccount, toAccount, amount, currency, transDate, StatusEnum.Transfer.SCHEDULED.name());
				return Transfer.builder()
						.transId(id).status(StatusEnum.Transfer.SCHEDULED.name())
						.amount(amount).scheduleDate(transDate)
						.currency(currency).build();
			});
		} catch (Exception e) {
			LOGGER.info("Unable to schedule the transfer right now. " +
							"  from {} to {} account. Exception is : " + e.getMessage(),
					amount, fromAccount, toAccount);
			throw e;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Transfer findOneTransfer(Integer transId) {
		return jdbi.withExtension(TransferDAO.class, dao -> dao.getTransferByTransId(transId));
	}

	/**
	 * This method uses the lock object which is called from transfer so, if a lock obtain at the
	 * transfer time the same thread will be able to get the latest balance.
	 *
	 * @param account Integer
	 * @return BigDecimal
	 */
	private BigDecimal getAccountBalance(Integer account) {
		return findOneAccount(account).getBalance();
	}
}
package com.demo.api.handler;

import com.demo.exception.AccountNotFoundException;
import com.demo.exception.AmountOverdrawnException;
import com.demo.exception.DataValidationException;
import com.demo.exception.MoneyTransferBaseException;
import com.demo.exception.TransactionException;
import com.demo.model.error.ErrorModel;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.demo.util.Utils.getObjectToJsonString;
import static com.demo.util.Utils.limitString;
import static org.eclipse.jetty.http.HttpStatus.INTERNAL_SERVER_ERROR_500;
import static spark.Spark.exception;

/**
 * Global exception handler class to handle all types of application
 * exception and format for client call when error occurs.
 *
 * @author Sujit Tripathy
 */
public class ExceptionHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandler.class);

	public ExceptionHandler() {
		initializeExceptionHandler();
	}

	private void initializeExceptionHandler() {
		exception(Exception.class, (e, req, res) -> {
			ErrorModel errorModel;
			if(e instanceof DataValidationException) {
				res.status(HttpStatus.BAD_REQUEST_400);
				DataValidationException exception = (DataValidationException) e;
				errorModel = ErrorModel.builder()
						.errorCode(exception.getErrorCode())
						.message(e.getMessage())
						.type(exception.getType())
						.build();
			} else if(e instanceof AmountOverdrawnException) {
				res.status(HttpStatus.PAYMENT_REQUIRED_402);
				AmountOverdrawnException exception = (AmountOverdrawnException) e;
				errorModel = ErrorModel.builder()
						.errorCode(exception.getErrorCode())
						.message(e.getMessage())
						.type(exception.getType())
						.build();
			} else if(e instanceof TransactionException) {
				res.status(HttpStatus.PAYMENT_REQUIRED_402);
				TransactionException exception = (TransactionException) e;
				errorModel = ErrorModel.builder()
						.errorCode(exception.getErrorCode())
						.message(e.getMessage())
						.type(exception.getType())
						.build();
			} else if(e instanceof AccountNotFoundException) {
				res.status(HttpStatus.NOT_FOUND_404);
				AccountNotFoundException exception = (AccountNotFoundException) e;
				errorModel = ErrorModel.builder()
						.errorCode(exception.getErrorCode())
						.message(e.getMessage())
						.type(exception.getType())
						.build();
			} else if(e instanceof MoneyTransferBaseException) {
				res.status(INTERNAL_SERVER_ERROR_500);
				MoneyTransferBaseException exception = (MoneyTransferBaseException) e;
				errorModel = ErrorModel.builder()
						.errorCode(exception.getErrorCode())
						.type(exception.getType())
						.message(e.getMessage())
						.build();
			} else {
				res.status(INTERNAL_SERVER_ERROR_500);
				errorModel = ErrorModel.builder()
						.errorCode(INTERNAL_SERVER_ERROR_500)
						.message(e.getMessage() != null ? limitString(e.getMessage(), 100)
										: limitString(e.toString(), 100))
						.type(HttpStatus.getMessage(INTERNAL_SERVER_ERROR_500))
						.build();
			}
			LOGGER.info("Exception handler message: {}", e.getMessage());

			res.body(getObjectToJsonString(errorModel));
			res.header("Content-Type", "application/json");
		});
	}
}

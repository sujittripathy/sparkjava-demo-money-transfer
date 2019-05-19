package com.demo.unittest;

import com.demo.db.MoneyTransferDAO;
import com.demo.db.Transfer;
import com.demo.model.Validator;
import com.demo.model.request.TransferRequest;
import com.demo.model.response.TransferResponse;
import com.demo.services.TransferServiceImpl;
import com.demo.util.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class TransferServiceTest {
	@Mock
	private MoneyTransferDAO moneyTransferDAO;

	@Mock
	private Validator validator;

	@InjectMocks
	private TransferServiceImpl transferService;

	@Test
	public void testTransferTransfer() {
		when(moneyTransferDAO.transfer(10000,10001,
				new BigDecimal(100) ,"USD"))
				.thenReturn(mockTransferRes());

		TransferResponse response = transferService.transfer(mockTransferReq());

		assertThat(response.getTransactionId()).isEqualTo("12345678");
	}

	@Test
	public void fetchOneTransferTransaction() {
		when(moneyTransferDAO.findOneTransfer(100100))
				.thenReturn(mockTransferRes());
		TransferResponse response = transferService.findTransferByTransId(100100);

		assertThat(response.getTransactionId()).isEqualTo("12345678");
		assertThat(response.getStatus()).isEqualTo("SUCCESS");
	}

	@Test
	public void verifyScheduledTransfer() {
		when(moneyTransferDAO.transferSchedule(10003, 10004, new BigDecimal(500),
				Utils.parseStringToDate("06/01/2019"), "USD")).thenReturn(mockTransferRes());
		TransferResponse transferResponse =
						transferService.transferSchedule(mockScheduleTransferReq());
		assertThat(transferResponse.getTransactionId()).isEqualTo("12345678");
	}

	private TransferRequest mockTransferReq() {
		return TransferRequest.builder()
				.fromAccount("10000")
				.toAccount("10001")
				.amount(new BigDecimal(100))
				.currency("USD")
				.build();
	}

	private Transfer mockTransferRes() {
		return Transfer.builder()
				.transId(12345678L)
				.status("SUCCESS")
				.transDate(LocalDateTime.now())
				.build();
	}

	private TransferRequest mockScheduleTransferReq() {
		return TransferRequest.builder()
				.fromAccount("10003")
				.toAccount("10004")
				.amount(new BigDecimal(500))
				.scheduleDate("06/01/2019")
				.currency("USD")
				.build();
	}

}

package com.demo.util;

import com.google.gson.Gson;
import com.demo.exception.DataValidationException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.demo.util.ErrorEnum.INVALID_DATE_FORMAT;
import static com.demo.util.ErrorEnum.PAST_TRANSFER_DATE;

public class Utils {

	private static final Gson gson = new Gson();

	public static String getObjectToJsonString(Object o) {
		return gson.toJson(o);
	}

	public static String limitString(String str, int len) {
		if(str.length() < len) {
			return str;
		} else {
			return str.substring(0, len).concat("...");
		}
	}

	public static LocalDate parseStringToDate(String date) {
		try {
			LocalDate dt = LocalDate.parse(date, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
			if(dt.isBefore(LocalDate.now())) {
				throw new DataValidationException(PAST_TRANSFER_DATE);
			}
			return dt;
		} catch (DataValidationException e) {
			throw  e;
		} catch (Exception e) {
			throw new DataValidationException(INVALID_DATE_FORMAT);
		}
	}
}

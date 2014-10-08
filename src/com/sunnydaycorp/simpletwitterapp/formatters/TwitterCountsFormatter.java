package com.sunnydaycorp.simpletwitterapp.formatters;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TwitterCountsFormatter {

	public static String getCountString(long count) {
		if (count < 1000) {
			return String.valueOf(count);
		}
		if (count < 10000 && count / 1000 > 1) {
			StringBuilder sb = new StringBuilder(String.valueOf(count));
			sb.insert(1, ",");
			return sb.toString();
		}
		if (count / 1000000 < 1) {
			return new BigDecimal(count / (float) 1000).setScale(1, RoundingMode.FLOOR).toString() + "K";
		}
		return new BigDecimal(count / (float) 1000000).setScale(1, RoundingMode.FLOOR).toString() + "M";
	}
}

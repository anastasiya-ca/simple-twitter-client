package com.sunnydaycorp.simpletwitterapp.formatters;

import java.util.Calendar;
import java.util.Date;

import android.text.format.DateUtils;

public class ElapsedTimeFormatter {

	public static String getElapsedTimeString(Date fromdate) {

		long then;
		then = fromdate.getTime();
		Date date = new Date(then);

		StringBuffer dateStr = new StringBuffer();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		Calendar now = Calendar.getInstance();

		int days = daysBetween(calendar.getTime(), now.getTime());
		int minutes = hoursBetween(calendar.getTime(), now.getTime());
		int hours = minutes / 60;
		if (days == 0) {
			int second = minuteBetween(calendar.getTime(), now.getTime());
			if (minutes > 60) {
				if (hours >= 1 && hours <= 24) {
					dateStr.append(hours).append("h");
				}
			} else {
				if (second <= 10) {
					dateStr.append("now");
				} else if (second > 10 && second <= 30) {
					dateStr.append("few sec");
				} else if (second > 30 && second <= 60) {
					dateStr.append(second).append("s");
				} else if (second >= 60 && minutes <= 60) {
					dateStr.append(minutes).append("m");
				}
			}
		} else if (hours > 24 && days <= 7) {
			dateStr.append(days).append("d");
		}
		// TODO what if more than 7 days
		return dateStr.toString();
	}

	public static int minuteBetween(Date d1, Date d2) {
		return (int) ((d2.getTime() - d1.getTime()) / DateUtils.SECOND_IN_MILLIS);
	}

	public static int hoursBetween(Date d1, Date d2) {
		return (int) ((d2.getTime() - d1.getTime()) / DateUtils.MINUTE_IN_MILLIS);
	}

	public static int daysBetween(Date d1, Date d2) {
		return (int) ((d2.getTime() - d1.getTime()) / DateUtils.DAY_IN_MILLIS);
	}
}

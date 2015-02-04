package com.sunnydaycorp.simpletwitterapp.formatters;

import java.util.Calendar;
import java.util.Date;

import android.text.format.DateUtils;

public class ElapsedTimeFormatter {

	public static String getElapsedTimeString(Date fromDate) {
		StringBuffer dateStr = new StringBuffer();
		Calendar then = Calendar.getInstance();
		then.setTime(fromDate);
		Calendar now = Calendar.getInstance();

		int weeks = weeksBetween(then.getTime(), now.getTime());
		int days = daysBetween(then.getTime(), now.getTime());
		int minutes = minutesBetween(then.getTime(), now.getTime());
		int hours = minutes / 60;
		int second = secondsBetween(then.getTime(), now.getTime());

		if (days == 0) {
			if (minutes > 60) {
				if (hours >= 1 && hours <= 24) {
					return dateStr.append(hours).append("h").toString();
				}
			} else {
				if (second <= 10) {
					return dateStr.append("now").toString();
				} else if (second > 10 && second <= 30) {
					return dateStr.append("few sec").toString();
				} else if (second > 30 && second <= 60) {
					return dateStr.append(second).append("s").toString();
				} else if (second >= 60 && minutes <= 60) {
					return dateStr.append(minutes).append("m").toString();
				}
			}
		} else if (hours > 24 && days <= 7) {
			return dateStr.append(days).append("d").toString();
		} else {
			return dateStr.append(weeks).append("w").toString();
		}
		return dateStr.toString();
	}

	public static int secondsBetween(Date d1, Date d2) {
		return (int) ((d2.getTime() - d1.getTime()) / DateUtils.SECOND_IN_MILLIS);
	}

	public static int minutesBetween(Date d1, Date d2) {
		return (int) ((d2.getTime() - d1.getTime()) / DateUtils.MINUTE_IN_MILLIS);
	}

	public static int daysBetween(Date d1, Date d2) {
		return (int) ((d2.getTime() - d1.getTime()) / DateUtils.DAY_IN_MILLIS);
	}

	public static int weeksBetween(Date d1, Date d2) {
		return (int) ((d2.getTime() - d1.getTime()) / DateUtils.WEEK_IN_MILLIS);
	}
}

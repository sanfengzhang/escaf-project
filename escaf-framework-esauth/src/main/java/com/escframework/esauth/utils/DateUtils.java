package com.escframework.esauth.utils;

import java.util.Calendar;
import java.util.Date;

public class DateUtils
{

	public static Date addMinutes(Date date, int amount)
	{
		return add(date, Calendar.MINUTE, amount);
	}

	public static Date addSeconds(Date date, int amount)
	{
		return add(date, Calendar.SECOND, amount);
	}

	public static Date addMilliseconds(Date date, int amount)
	{
		return add(date, Calendar.MILLISECOND, amount);
	}

	public static Date add(Date date, int calendarField, int amount)
	{
		if (date == null)
		{
			throw new IllegalArgumentException("The date must not be null");
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(calendarField, amount);
		return c.getTime();
	}

	public static boolean compareDate(Date date1, Date date2)
	{
		if (date1.getTime() > date2.getTime())
		{
			return true;
		}

		return false;
	}

}

/**
 * 
 */
package com.github.mlaursen.database.utils;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * This has util methods for dealing with SQL Dates.
 * 
 * @author mlaursen
 * 
 */
public class DateUtil {
	
	/**
	 * Converts a string date into a java.sql.Date using the basic format of yyyy-MM-dd OR MM/dd/yyyy
	 * 
	 * @param dateString
	 *            The Date String
	 * @return A java.sql.Date
	 */
	public static Date stringToDate(String dateString) {
		boolean hyphen = dateString.contains("-");
		String s = dateString.replace("-", "").replace("/", "");
		return stringToDate(s, (hyphen ? "yyyyMMdd" : "MMddyyyy"));
	}
	
	/**
	 * Attempts to convert a date string into a java.sql.Date
	 * 
	 * @param dateString
	 *            The possible date
	 * @param dateFormat
	 *            The format to parse the date string
	 * @return a java.sql.Date OR a new java.sql.Date for the current time
	 */
	public static Date stringToDate(String dateString, String dateFormat) {
		try {
			return new Date(new SimpleDateFormat(dateFormat).parse(dateString).getTime());
		}
		catch(ParseException e) {
			return new Date(Calendar.getInstance().getTimeInMillis());
		}
	}
	
	/**
	 * Converts a sysdate string(yyyy-MM-dd h:m:s) for Oracle to a java.sql.Date
	 * 
	 * @param sys
	 *            The sysdate
	 * @return A java.sql.Date
	 */
	public static Date sysdateToDate(String sys) {
		if(sys != null) {
			try {
				java.util.Date utilDate = new SimpleDateFormat("yyyy-MM-dd h:m:s").parse(sys);
				return new Date(utilDate.getTime());
			}
			catch(ParseException e) {
				e.printStackTrace();
			}
		}
		Calendar cal = Calendar.getInstance();
		return new Date(cal.getTime().getTime());
	}
	
	/**
	 * Checks if two dates are the same date by checking if they are in the same year and they are the same day of the year.
	 * 
	 * @param d1
	 *            first date
	 * @param d2
	 *            second date
	 * @return True if they are on the same day
	 */
	public static boolean sameDate(Date d1, Date d2) {
		Calendar c1 = new GregorianCalendar();
		c1.setTime(d1);
		Calendar c2 = new GregorianCalendar();
		c2.setTime(d2);
		return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
	}
}

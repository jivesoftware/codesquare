/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//package com.jivesoftware.mapReduceScheduler;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
/**
 *
 * @author justin.kikuchi
 */
public class ChooseMapReduce {
	public static void main(String[] args) throws Exception {
		String output;
		if (args.length == 1) {
			//output = getArguement("Mon Jul 4 04:00:38 PDT 2011");
			output = getArguement(args[0]);
		} else {
			System.out.println("wrong number of arguements" + args.length);
			throw new Exception("wrong number of arguements");
		}
		System.out.println(output);
	}

	public static String getArguement(String day) throws Exception {
		Calendar cal = Calendar.getInstance();
		String basePath = "/user/interns/Commits/";
		DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
		Date date = df.parse(day);
		cal.setTime(date);

		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int min = cal.get(Calendar.MINUTE);
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		String path = "";
		String command = "hadoop jar MapRedHadoop.jar codesquare.badges.";

		if (dayOfWeek == Calendar.MONDAY && hour == 1 && min == 00) {
			// run weekly map reduce
			for (int i = 1; i < 8; i++) {
				path = path + " " + basePath
						+ previousDateString(day, Calendar.DAY_OF_YEAR, -i);
			}
			command = command + "Badge_21_22_23";

		} else if (hour == 00 && min == 00) {
			// run daily map reduce
			path = " " + basePath
					+ previousDateString(day, Calendar.DAY_OF_YEAR, -1);
			command = command + "Badge_20_24";
		} else if (min == 00) {
			// run hour map reduce
			path = " " + basePath + previousDateString(day, Calendar.HOUR, -1)
					+ " " + basePath
					+ previousDateString(day, Calendar.HOUR, -2);
			command = command + "Badge_14_15";
		} else {
			// run minute map reduce
			path = " " + basePath
					+ previousDateString(day, Calendar.MINUTE, -1) + " "
					+ basePath + previousDateString(day, Calendar.MINUTE, -2);
			command = command + "Badge_25";
		}

		return command + path;
	}

	public static String previousDateString(String dateString,
			int fieldToChange, int offset) throws ParseException {
		// Create a date formatter using your format string
		DateFormat output = null;
		DateFormat dateFormat = new SimpleDateFormat(
				"EEE MMM dd HH:mm:ss zzz yyyy");
		// output formatt depending on field
		if (fieldToChange == Calendar.DAY_OF_YEAR)
			output = new SimpleDateFormat("yyyy/M/d/");
		else if (fieldToChange == Calendar.HOUR)
			output = new SimpleDateFormat("yyyy/M/d/H/");
		else if (fieldToChange == Calendar.MINUTE)
			output = new SimpleDateFormat("yyyy/M/d/H/m/");

		// Parse the given date string into a Date object.
		// Note: This can throw a ParseException.
		Date myDate = dateFormat.parse(dateString);

		// Use the Calendar class to subtract specified day
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(myDate);
		calendar.add(fieldToChange, offset);

		// Use the date formatter to produce a formatted date string
		Date previousDate = calendar.getTime();
		String result = output.format(previousDate);

		return result;
	}

}

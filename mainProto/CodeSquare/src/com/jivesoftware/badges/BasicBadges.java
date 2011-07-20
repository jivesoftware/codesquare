package com.jivesoftware.badges;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;

import com.jivesoftware.backendServlet.Commit;
import com.jivesoftware.toolbox.HbaseTools;

public class BasicBadges {
	
	/***
	 * This method takes various information from a recent commit, and uses it
	 * to check for basic badges. It updates the row in the HBase with the
	 * appropriate badges, and information.
	 * 
	 * @param table
	 *            connection to Hbase
	 * @param c
	 *            commit object
	 * @param numBugs
	 *            number of bugs the user fixed, currently not in use
	 */
	public static void checkUpdateBadges(HTable table, Commit c, int numBugs) {

		Result data = HbaseTools.getRowData(table, c.getEmail());
		// person does not exist
		if (data == null) {
			return;
		}
		String date = c.getPushDate().getYear()+"-"+c.getPushDate().getMonth()+"-"+c.getPushDate().getDate();
		int[] fieldValues = HbaseTools.getFields(data, new String[] { "badgesWeek", "numBugs", "numCommits", "consecCommits" });
		String lastCommit = HbaseTools.getLastCommit(data);
		ArrayList<String> badges = testDateTimeBadges(date, c.getPushDate().getDay(), c.getPushDate().getHour());

		fieldValues[1] = fieldValues[1] + numBugs;
		fieldValues[2] = fieldValues[2] + 1;

		int[] consecCommits = checkConsecCommits(date, lastCommit, fieldValues[3]);

		badges.addAll(checkNumericalBadges(fieldValues, consecCommits));
		// checks for jive in the message
		if (c.getMessage().toLowerCase().contains("jive")) {
			badges.add("26");
		}
		Object[] badgeList = HbaseTools.getBadges(data);
		@SuppressWarnings("unchecked")
		ArrayList<String> aquiredBadges = (ArrayList<String>) badgeList[0];
		for (int i = 0; i < badges.size(); i++) {
			if (aquiredBadges.contains(badges.get(i))) {
				badges.remove(i--);
			}
		}
		// checks to see if there are more than 7 new badges in the week
		// ???
		if ((badges.size() + fieldValues[0]) > 7) {
			if(!aquiredBadges.contains("16")){
				badges.add("16");
			}
		}
		String newBadges = (String) badgeList[1];
		String[] results = new String[badges.size()];
		HbaseTools.addRow(table, c.getEmail(), date, badges.size() + fieldValues[0],
				fieldValues[1], fieldValues[2], consecCommits[0], newBadges,
				badges.toArray(results));
	}

	
	/***
	 * This method takes in information passed in from the HBase to check for
	 * number of commits, and committed person
	 * 
	 * @param fieldValues
	 *            array of three things, [0] is number of badges in week, [1] is
	 *            bugs, [2] is number of commits
	 * @param consecCommits
	 *            [0] is committed person, [1] is 2 commits in a day, [2] is 2
	 *            commits in >5days
	 * @return ArrayList of acquired badges
	 */
	public static ArrayList<String> checkNumericalBadges(int[] fieldValues,
			int[] consecCommits) {
		ArrayList<String> badges = new ArrayList<String>();
		int totNumBugs = fieldValues[1];
		int totNumCommits = fieldValues[2];

		if (consecCommits[0] >= 7) {
			badges.add("30");
		}
		if (consecCommits[1] == 1) {
			badges.add("27");
		} else if (consecCommits[1] == 2) {
			badges.add("28");
		}

		// bug stuff is not currently implemented, but is being checked for here
		if (totNumBugs > 100) {
			badges.add("35");
		} else if (totNumBugs > 50) {
			badges.add("34");
		} else if (totNumBugs > 25) {
			badges.add("33");
		} else if (totNumBugs > 10) {
			badges.add("32");
		} else if (totNumBugs > 1) {
			badges.add("31");
		}

		if (totNumCommits > 4999) {
			badges.add("5");
		} else if (totNumCommits > 999) {
			badges.add("4");
		} else if (totNumCommits > 499) {
			badges.add("3");
		} else if (totNumCommits > 49) {
			badges.add("2");
		} else if (totNumCommits > 0) {
			badges.add("1");
		}

		return badges;
	}
	
	/***
	 * This method checks to see if there are commits on consecutive days, twice
	 * in a day, or greater than 5days
	 * 
	 * @param commitDate
	 *            New commit date
	 * @param lastCommit
	 *            Previous commit date
	 * @param consecCommitsOld
	 *            Consecutive commits before the newest commit
	 * @return An integer array, [0] is consecutive commits, [1] is commits in a
	 *         day, [2] is commits in >5days
	 */
	public static int[] checkConsecCommits(String commitDate,
			String lastCommit, int consecCommitsOld) {
		int[] result = new int[2]; // result[0] is commited person field, and
									// result[1] is (value = 1)2 commits in a
									// day or (value = 2)2 commits in >5 days: 0
									// is none
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat dfcheck = new SimpleDateFormat("dd");
		Date check = null;
		Date date = null;
		Date dateOld = null;

		result[0] = 1;
		result[1] = 0;
		if (lastCommit.isEmpty()) {
			return result;
		}
		try {
			date = df.parse(commitDate);
			dateOld = df.parse(lastCommit);
			check = dfcheck.parse("02");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if ((date.getTime() - dateOld.getTime()) < check.getTime()
				&& date.getTime() != dateOld.getTime()) {
			result[0] = consecCommitsOld + 1;
		} else if (date.getTime() == dateOld.getTime()) {
			result[0] = consecCommitsOld;
			result[1] = 1;
		}
		try {
			check = dfcheck.parse("05");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (date.getTime() - dateOld.getTime() > check.getTime()) {
			result[1] = 2;
		}
		return result;
	}
	
	/***
	 * This method determines simple date and time badges
	 * 
	 * @param date
	 *            date of commit
	 * @param dayofWeek
	 *            day of the week user committed
	 * @param hour
	 *            hour of the commit
	 * @return ArrayList of the attained badges
	 */

	public static ArrayList<String> testDateTimeBadges(String date,
			String dayofWeek, int hour) {
		ArrayList<String> badges = new ArrayList<String>();

		if (date.contains("-03-17")) {
			badges.add("7");
		} else if (date.contains("-10-31")) {
			badges.add("6");
		} else if (date.contains("-02-29")) {
			badges.add("8");
		} else if (date.contains("-02-14")) {
			badges.add("9");
		} else if (date.contains("-03-14")) {
			badges.add("10");
		}

		if (dayofWeek.equals("Mon") && hour <= 5) {
			badges.add("18");
		} else if (dayofWeek.equals("Fri") && hour >= 16) {
			badges.add("19");
		} else if (dayofWeek.equals("Sat") || dayofWeek.equals("Sun")) {
			badges.add("29");
		}
		if (hour >= 22) {
			badges.add("12");
		}
		if (hour <= 6) {
			badges.add("13");
		}

		return badges;
	}
	
}

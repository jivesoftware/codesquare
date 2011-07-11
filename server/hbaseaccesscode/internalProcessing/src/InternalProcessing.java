import BackEnd.BackEndJar;
import BackEnd.Commit;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.NavigableSet;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.conf.Configuration;

// Class that has nothing but a main.
// Does a Put, Get and a Scan against an hbase table.
/***
 * This Class class the git parser and checks for basic badges, and updates the
 * hbase
 * 
 * @author justin.kikuchi
 * 
 */
public class InternalProcessing {

	/***
	 * This is the main method for the back end processing, it calls the git
	 * parser and receives an array of Commits. From there it calls the
	 * checkUpdateBadges to update the HBase.
	 * 
	 * @param args
	 *            consists of a string that contains unparsed commit data
	 */
	public static void main(String[] args) {
		ArrayList<Commit> output = BackEndJar.parseGitInput(args[0]);
		Object[] out = setup();
		HTable table = (HTable) out[1];
		Configuration config = (Configuration) out[0];
		BackEndJar.insertCommitsIntoHDFS(output);

		if (table == null) {
			System.out.println("could not find table");
			return;
		}
		for (int i = output.size() - 1; i >= 0; i--) {
			Commit c = output.get(i);
			checkUpdateBadges(table, c.getEmail(), c.getDate(), c.getDay(),
					new Integer(c.getHour()).toString(), c.getMessage(), 0);
		}
		Result data = getRowData(table, "eric.ren@jivesoftware.com");
		test(data);
		try {
			table.close();
			// free resources and close connections
			HConnectionManager.deleteConnection(config, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/***
	 * This method sets up the hbase configuration.
	 * 
	 * @return Object array consisting of a HTable(1) and the config object(0)
	 */
	public static Object[] setup() {
		Object[] output = new Object[2];
		Configuration config = HBaseConfiguration.create();
		config.set("hbase.cluster.distributed", "true");
		config.set("hbase.rootdir",
				"hdfs://hadoopdev008.eng.jiveland.com:54310/hbase");
		config.set(
				"hbase.zookeeper.quorum",
				"hadoopdev008.eng.jiveland.com,hadoopdev002.eng.jiveland.com,hadoopdev001.eng.jiveland.com");
		config.set("hbase.zookeeper.property.clientPort", "2181");
		config.set("hbase.hregion.max.filesize", "1073741824");
		// Create a table
		try {
			HBaseAdmin admin = new HBaseAdmin(config);
			if (!admin.tableExists("EmpBadges")) {
				admin.createTable(new HTableDescriptor("EmpBadges"));
				admin.disableTable("EmpBadges");
				admin.addColumn("EmpBadges", new HColumnDescriptor("Info"));
				admin.addColumn("EmpBadges", new HColumnDescriptor("Badge"));
				admin.enableTable("EmpBadges");
			}
		} catch (MasterNotRunningException e1) {
			System.err.println(e1.getMessage());
		} catch (ZooKeeperConnectionException e1) {
			System.err.println(e1.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println();
		}

		HTable table = null;
		try {
			table = new HTable(config, "EmpBadges");
			output[0] = config;
			output[1] = table;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return output;
		}
		return output;
	}

	/***
	 * This method takes various information from a recent commit, and uses it
	 * to check for basic badges. It updates the row in the HBase with the
	 * appropriate badges, and information.
	 * 
	 * @param email
	 *            unique identifier for the row in the HBase
	 * @param date
	 *            date of commit
	 * @param dayofWeek
	 *            day of the week that user committed
	 * @param hour
	 *            hour in the day that user committed
	 * @param message
	 *            commit message
	 * @param numBugs
	 *            number of bugs the user fixed, currently not in use
	 */
	public static void checkUpdateBadges(HTable table, String email,
			String date, String dayofWeek, String hour, String message,
			int numBugs) {

		Result data = getRowData(table, email);
		// person does not exist
		if (data == null) {
			return;
		}
		String[] fields = { "badgesWeek", "numBugs", "numCommits",
				"consecCommits" };
		int[] fieldValues = getFields(data, fields);

		String lastCommit = getLastCommit(data);
		ArrayList<String> badges = testDateTimeBadges(date, dayofWeek, hour);

		fieldValues[1] = fieldValues[1] + numBugs;
		fieldValues[2] = fieldValues[2] + 1;

		int[] consecCommits = checkConsecCommits(date, lastCommit,
				fieldValues[3]);

		badges.addAll(checkNumericalBadges(fieldValues, consecCommits));
		// checks for jive in the message
		if (message.toLowerCase().contains("jive")) {
			badges.add("26");
		}
		Object[] badgeList = getBadges(data);
		@SuppressWarnings("unchecked")
		ArrayList<String> aquiredBadges = (ArrayList<String>) badgeList[0];
		for (int i = 0; i < badges.size(); i++) {
			if (aquiredBadges.contains(badges.get(i))) {
				badges.remove(i--);
			}
		}
		// checks to see if there are more than 7 new badges in the week
		if ((badges.size() + fieldValues[0]) > 7) {
			badges.add("16");
		}
		String newBadges = (String) badgeList[1];
		String[] results = new String[badges.size()];
		addRow(table, email, date, badges.size() + fieldValues[0],
				fieldValues[1], fieldValues[2], consecCommits[0], newBadges,
				badges.toArray(results));
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
			String dayofWeek, String hour) {
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

		if (dayofWeek.equals("Mon") && Integer.parseInt(hour) <= 5) {
			badges.add("18");
		} else if (dayofWeek.equals("Fri") && Integer.parseInt(hour) >= 16) {
			badges.add("19");
		} else if (dayofWeek.equals("Sat") || dayofWeek.equals("Sun")) {
			badges.add("29");
		}
		if (!hour.isEmpty()) {
			if (Integer.parseInt(hour) >= 22) {
				badges.add("12");
			}
			if (Integer.parseInt(hour) <= 6) {
				badges.add("13");
			}
		}

		return badges;
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

		if (totNumCommits > 999) {
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
	 * This method adds a row to the table, if one exits it overwrites it. It's
	 * parameters are updated information
	 * 
	 * @param table
	 *            HTable to put to
	 * @param email
	 *            Unique identifier for the row
	 * @param lastCommit
	 *            Most recent commit
	 * @param badgesWeek
	 *            Number of badges in the current week
	 * @param numBugs
	 *            Number of total bugs
	 * @param numCommits
	 *            Total number of bugs
	 * @param consecCommits
	 *            Number of consecutive commits by day
	 * @param newBadges
	 *            Newly acquired badges
	 * @param badges
	 *            already acquired badges
	 */
	public static void addRow(HTable table, String email, String lastCommit,
			int badgesWeek, int numBugs, int numCommits, int consecCommits,
			String newBadges, String[] badges) {
		for (int i = 0; i < badges.length; i++) {
			newBadges = newBadges + " " + badges[i];
		}

		Put row = new Put(Bytes.toBytes(email));

		row.add(Bytes.toBytes("Info"), Bytes.toBytes("lastCommit"),
				Bytes.toBytes(lastCommit));
		row.add(Bytes.toBytes("Info"), Bytes.toBytes("badgesWeek"),
				Bytes.toBytes(badgesWeek));
		row.add(Bytes.toBytes("Info"), Bytes.toBytes("numBugs"),
				Bytes.toBytes(numBugs));
		row.add(Bytes.toBytes("Info"), Bytes.toBytes("numCommits"),
				Bytes.toBytes(numCommits));
		row.add(Bytes.toBytes("Info"), Bytes.toBytes("consecCommits"),
				Bytes.toBytes(consecCommits));
		row.add(Bytes.toBytes("Info"), Bytes.toBytes("newBadges"),
				Bytes.toBytes(newBadges));
		if (badges != null) {
			for (int i = 0; i < badges.length; i++) {
				row.add(Bytes.toBytes("Badge"), Bytes.toBytes(badges[i]),
						Bytes.toBytes("1"));
			}
		}
		try {
			table.put(row);
		} catch (Exception e) {
			System.err.println();
		}
	}

	/***
	 * Deletes row from the HBase
	 * 
	 * @param table
	 *            HTable to delete row from
	 * @param email
	 *            Identifies row to delete
	 */
	public static void deleteRow(HTable table, String email) {
		Delete d = new Delete(Bytes.toBytes(email));

		try {
			table.delete(d);
		} catch (Exception e) {
			System.err.println();
		}
	}

	/***
	 * Adds Badges to the specified user
	 * 
	 * @param table
	 *            HTable to modify
	 * @param email
	 *            Row Identifier
	 * @param badges
	 *            Badges to Add
	 */
	public static void updateBadges(HTable table, String email, String[] badges) {

		Put row = new Put(Bytes.toBytes(email));

		for (int i = 0; i < badges.length; i++) {
			row.add(Bytes.toBytes("Badge"), Bytes.toBytes(badges[i]),
					Bytes.toBytes("1"));
		}

		try {
			table.put(row);
		} catch (Exception e) {
			System.err.println();
		}
	}

	/***
	 * Returns the acquired and new badges
	 * 
	 * @param data
	 *            The data from the hbase
	 * 
	 * @return Object[0] is an ArrayList of acquired badges, Object[1] is newly
	 *         acquired badges
	 */
	public static Object[] getBadges(Result data) {
		Object[] output = new Object[2];
		ArrayList<String> resultingBadges = new ArrayList<String>();
		String newBadges = "";
		output[0] = resultingBadges;
		output[1] = newBadges;

		if (data == null) {
			System.out.println("Not found");
			return output;
		}
		if (data.isEmpty()) {
			return output;
		}

		byte[] badges = Bytes.toBytes("Badge");

		NavigableSet<byte[]> badges_awarded = data.getFamilyMap(badges)
				.descendingKeySet();
		for (byte[] badge : badges_awarded) {
			resultingBadges.add(new String(badge));
		}
		newBadges = new String(data.getValue(Bytes.toBytes("Info"),
				Bytes.toBytes("newBadges")));

		output[0] = resultingBadges;
		output[1] = newBadges;
		return output;
	}

	/***
	 * This method updates the boss on the specified row
	 * 
	 * @param table
	 *            HTable to alter
	 * @param email
	 *            Row Identifier
	 * @param bossEmail
	 *            New boss email
	 */
	public static void updateBoss(HTable table, String email, String bossEmail) {
		Put row = new Put(Bytes.toBytes(email));

		row.add(Bytes.toBytes("Info"), Bytes.toBytes("bossEmail"),
				Bytes.toBytes(bossEmail));

		try {
			table.put(row);
		} catch (Exception e) {
			System.err.println();
		}
	}

	/***
	 * This method retrieves the last commit from the HBase
	 * 
	 * @param data
	 *            The data from the hbase
	 * 
	 * @return The String of the last commit date
	 */
	public static String getLastCommit(Result data) {
		String lastCommit = "";
		if (data == null) {
			return "";
		}
		if (data.isEmpty()) {
			return "";
		}
		try {
			lastCommit = new String(data.getValue(Bytes.toBytes("Info"),
					Bytes.toBytes("lastCommit")));
		} catch (java.lang.NullPointerException e) {
			return "";
		}
		return lastCommit;
	}

	/***
	 * This method retrieves the specified integer fields from the HBase.
	 * 
	 * @param data
	 *            The data from the hbase
	 * @param fields
	 *            Column names to retrieve
	 * @return integer array of the fields requested
	 */
	public static int[] getFields(Result data, String[] fields) {
		int[] results = new int[fields.length];

		for (int i = 0; i < fields.length; i++) {
			results[i] = byteArrayToInt(data.getValue(Bytes.toBytes("Info"),
					Bytes.toBytes(fields[i])));
		}
		return results;
	}

	public static Result getRowData(HTable table, String email) {
		Get get = new Get(Bytes.toBytes(email));
		Result data = null;

		try {
			data = table.get(get);
		} catch (Exception e) {
			System.err.println();
		}

		if (data.isEmpty()) {
			return null;
		}
		return data;
	}

	/***
	 * This method is a helper function to getFields that converts a byte array
	 * to an integer
	 * 
	 * @param b
	 *            The byte array to convert
	 * @return integer value of the byte array
	 */
	private static int byteArrayToInt(byte[] b) {
		int value = 0;
		try {
			for (int i = 0; i < 4; i++) {
				int shift = (4 - 1 - i) * 8;
				value += (b[i] & 0x000000FF) << shift;
			}
		} catch (java.lang.NullPointerException e) {
			return 0;
		}
		return value;
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
	 * This method resets the new badge column in the HBase
	 * 
	 * @param table
	 *            HTable to modify
	 * @param email
	 *            Row Identifier
	 */
	public static void resetNewBadges(HTable table, String email) {
		Put row = new Put(Bytes.toBytes(email));

		row.add(Bytes.toBytes("Info"), Bytes.toBytes("newBadges"),
				Bytes.toBytes(""));

		try {
			table.put(row);
		} catch (Exception e) {
			System.err.println();
		}
	}

	/***
	 * This method prints the specified row in the HBase
	 * 
	 * @param data
	 *            The data from the hbase
	 */
	public static void test(Result data) {
		System.out
				.println("Printing data........................................................");
		@SuppressWarnings("unchecked")
		ArrayList<String> badges_awarded = (ArrayList<String>) (getBadges(data))[0];
		String newBadges = (String) (getBadges(data))[1];
		System.out.println("newBadges: " + newBadges);
		for (int i = 0; i < badges_awarded.size(); i++)
			System.out.println("Badges=\t" + badges_awarded.get(i));
		String[] fields = { "badgesWeek", "numBugs", "numCommits",
				"consecCommits" };
		int[] results = getFields(data, fields);
		System.out.println("1numbugs=\t" + results[1]);
		System.out.println("1lastCommit=\t" + getLastCommit(data));
		System.out.println("1badgesWeek=\t" + results[0]);
		System.out.println("1numCommits=\t" + results[2]);
		System.out.println("1consecCommits=\t" + results[3]);
		System.out
				.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	}
}
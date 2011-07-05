
import BackEnd.BackEndJar;
import BackEnd.Commit;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.conf.Configuration;


// Class that has nothing but a main.
// Does a Put, Get and a Scan against an hbase table.
public class InternalProcessing {
	public static void main(String[] args) {
		ArrayList<Commit> output = BackEndJar.parseGitInput(args[0]);

		BackEndJar.insertCommitsIntoHDFS(output);

		for (Commit c : output) {
			System.out.println(c.getDate());
			checkUpdateBadges(c.getEmail(), c.getDate(), c.getDay(),
					new Integer(c.getHour()).toString(), c.getMessage(), 0);
		}
	}

	public static void checkUpdateBadges(String email, String date,
			String dayofWeek, String hour, String message, int numBugs) {

		Configuration config = HBaseConfiguration.create();
		// config.addResource(new
		// Path("/Users/diivanand.ramalingam/Downloads/hbase/conf/hbase-site.xml"));
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		String[] fields = { "badgesWeek", "numBugs", "numCommits",
				"consecCommits" };
		int[] fieldValues = getFields(table, email, fields);
		String lastCommit = getLastCommit(table, email);
		ArrayList<String> badges = testDateTimeBadges(date, dayofWeek, hour);
		if (lastCommit == null) {
			lastCommit = "";
		}
		fieldValues[1] = fieldValues[1] + numBugs;
		fieldValues[2] = fieldValues[2] + 1;

		int[] consecCommits = checkConsecCommits(table, email, date,
				lastCommit, fieldValues[3]);

		badges.addAll(checkNumericalBadges(fieldValues, consecCommits));
		if (message.toLowerCase().contains("jive")) {
			badges.add("26");
		}
		if ((badges.size() + fieldValues[0]) > 7) {
			badges.add("16");
		}
		Object[] badgeList = getBadges(table, email);
		@SuppressWarnings("unchecked")
		ArrayList<String> aquiredBadges = (ArrayList<String>) badgeList[0];
		for (int i = 0; i < badges.size(); i++) {
			if (aquiredBadges.contains(badges.get(i))) {
				badges.remove(i--);
			}
		}
		String newBadges = (String) badgeList[1];
		String[] results = new String[badges.size()];
		addRow(table, email, date, badges.size() + fieldValues[0],
				fieldValues[1], fieldValues[2], consecCommits[0], newBadges,
				badges.toArray(results));

		test(table, email);
	}

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
		} else if (dayofWeek.equals("Fri") && Integer.parseInt(hour) >= 4) {
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

	public static ArrayList<String> checkNumericalBadges(int[] fieldValues,
			int[] consecCommits) {
		ArrayList<String> badges = new ArrayList<String>();
		int totNumBugs = fieldValues[1];
		int totNumCommits = fieldValues[2];

		// consecCommits[0] is commited person, and consecCommits[1] is (value =
		// 1)2 commits in a day or (value = 2)2 commits in >5 days: 0 is none
		if (consecCommits[0] >= 7) {
			badges.add("30");
		}
		if (consecCommits[1] == 1) {
			badges.add("27");
		} else if (consecCommits[1] == 2) {
			badges.add("28");
		}
		
		/*bug stuff is not currently implemented, but is being checked for here
		if(totNumBugs > 0){
			badges.add("31");
		}
		else if(totNumBugs > 10){
			badges.add("32");
		}
		else if(totNumBugs > 25){
			badges.add("33");
		}
		else if(totNumBugs > 50){
			badges.add("34");
		}
		else if(totNumBugs > 100){
			badges.add("35");
		}*/
		
		if (totNumCommits > 0) {
			badges.add("1");
		} else if (totNumCommits > 50) {
			badges.add("2");
		} else if (totNumCommits > 500) {
			badges.add("3");
		} else if (totNumCommits > 1000) {
			badges.add("4");
		} else if (totNumCommits > 5000) {
			badges.add("5");
		}

		return badges;
	}

	public static void addRow(HTable table, String email, String lastCommit,
			int badgesWeek, int numBugs, int numCommits, int consecCommits,
			String newBadges, String[] badges) {
		Get get = new Get(Bytes.toBytes(email));
		Result data = null;
		try {
			data = table.get(get);
		} catch (Exception e) {
			System.err.println();
		}
		 
		/*if (!data.isEmpty()) {
			System.out.println("Already Exists");
			return;
		}*/
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

	public static void deleteRow(HTable table, String email) {
		Delete d = new Delete(Bytes.toBytes(email));

		try {
			table.delete(d);
		} catch (Exception e) {
			System.err.println();
		}
	}

	public static void updateBadges(HTable table, String email, String[] badges) {

		Put row = new Put(Bytes.toBytes(email));

		for (int i = 0; i < badges.length - 1; i = i + 2) {
			row.add(Bytes.toBytes("Badge"), Bytes.toBytes(badges[i]),
					Bytes.toBytes(badges[i + 1]));
		}

		try {
			table.put(row);
		} catch (Exception e) {
			System.err.println();
		}
	}

	public static Object[] getBadges(HTable table, String email) {
		Get get = new Get(Bytes.toBytes(email));
		Result data = null;
		Object[] output = new Object[2];
		ArrayList<String> resultingBadges = new ArrayList<String>();
		String newBadges = "";
		output[0] = resultingBadges;
		output[1] = newBadges;
		try {
			data = table.get(get);
		} catch (Exception e) {
			System.err.println();
		}

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

		/* checks for personal message, not used at the moment
		String[] result =  new String[resultingBadges.size()];
		result = resultingBadges.toArray(result);
		
		for(int i=0; i<result.length; i++){
			String customDescription = new String(data.getValue(Bytes.toBytes("Badge"), Bytes.toBytes(result[i])));
			int currentBadgeIndex = resultingBadges.indexOf(result[i]);
			resultingBadges.add(currentBadgeIndex+1, customDescription);
		}*/

		output[0] = resultingBadges;
		output[1] = newBadges;
		return output;
	}

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

	public static String getLastCommit(HTable table, String email) {
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

		return new String(data.getValue(Bytes.toBytes("Info"),
				Bytes.toBytes("lastCommit")));
	}

	public static int[] getFields(HTable table, String email, String[] fields) {
		Get get = new Get(Bytes.toBytes(email));
		int[] results = new int[fields.length];
		Result data = null;

		try {
			data = table.get(get);
		} catch (Exception e) {
			System.err.println();
		}

		if (data.isEmpty()) {
			return results;
		}

		for (int i = 0; i < fields.length; i++) {
			results[i] = byteArrayToInt(
					data.getValue(Bytes.toBytes("Info"),
							Bytes.toBytes(fields[i])), 0);
		}
		return results;
	}

	public static int byteArrayToInt(byte[] b, int offset) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (b[i + offset] & 0x000000FF) << shift;
		}
		return value;
	}

	public static int[] checkConsecCommits(HTable table, String email,
			String commitDate, String lastCommit, int consecCommitsOld) {
		int[] result = new int[2]; // result[0] is commited person, and
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

	public static void test(HTable table, String email) {
		System.out
				.println("Pringting data........................................................");
		@SuppressWarnings("unchecked")
		ArrayList<String> badges_awarded = (ArrayList<String>) (getBadges(
				table, email))[0];
		String newBadges = (String) (getBadges(table, email))[1];
		System.out.println("newBadges: " + newBadges);
		for (int i = 0; i < badges_awarded.size(); i++)
			System.out.println("Badges=\t" + badges_awarded.get(i));
		String[] fields = { "badgesWeek", "numBugs", "numCommits",
				"consecCommits" };
		int[] results = getFields(table, email, fields);
		System.out.println("1numbugs=\t" + results[1]);
		System.out.println("1lastCommit=\t" + getLastCommit(table, email));
		System.out.println("1badgesWeek=\t" + results[0]);
		System.out.println("1numCommits=\t" + results[2]);
		System.out.println("1consecCommits=\t" + results[3]);
		System.out
				.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	}
}





	/*
	public static void updateRow(HTable table,  String email, String bossEmail, String lastCommit, int badgesWeek, int numBugs, int numCommits, int consecCommits, String[] badges){
		String[] fields = {"badgesWeek", "numBugs", "numCommits"};
		int[] resultFields = getFields(table, email, fields);
		int badgesWeekOld = resultFields[0];
		int numBugsOld = resultFields[1];
		int numCommitsOld = resultFields[2];
		System.out.println("Baa: "+ consecCommits);
		addRow(table,  email, bossEmail, lastCommit, badgesWeek + badgesWeekOld, numBugs + numBugsOld, numCommits + numCommitsOld, consecCommits, badges);
	}
	
		public static void test(){
		Configuration config = HBaseConfiguration.create();
		//config.addResource(new Path("/Users/diivanand.ramalingam/Downloads/hbase/conf/hbase-site.xml"));
		 
		//Create a table
		try {
			HBaseAdmin admin = new HBaseAdmin(config);
			if (!admin.tableExists("EmpBadges")) {
				admin.createTable( new HTableDescriptor("EmpBadges"));
			}
			admin.disableTable("EmpBadges");
			admin.addColumn("EmpBadges", new HColumnDescriptor("Info"));
		    admin.addColumn("EmpBadges", new HColumnDescriptor("Badge"));
			admin.enableTable("EmpBadges");
		} catch (MasterNotRunningException e1) {
			System.err.println(e1.getMessage());
		} catch (ZooKeeperConnectionException e1) {
			System.err.println(e1.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println();
		}

		HTable table;
		try {
			table = new HTable(config, "EmpBadges");
			deleteRow(table, "justin.kikuchi@jivesoftware.com");
			String[] badges = {"2", "desc2", "4", "desc4", "6", "desc6"};
			addRow(table, "justin.kikuchi@jivesoftware.com", "boss@gmail.com", "1999-6-13", 1, 4, 5, 2, badges);
			String[] badges1 = {"1","desc1"};
			//updateBadges(table, "justin.kikuchi@jivesoftware.com", badges1);
			updateBoss(table, "justin.kikuchi@jivesoftware.com", "newboss@gmail.com");

			String[] badges_awarded = getBadges(table, "justin.kikuchi@jivesoftware.com");
			for(int i=0;i<badges_awarded.length;i++)
				System.out.println(badges_awarded[i]);
			String[] fields = {"badgesWeek", "numBugs", "numCommits", "consecCommits"};
			int[] results = getFields(table, "justin.kikuchi@jivesoftware.com", fields);
			System.out.println("1numbugs" + results[0]);
			System.out.println("1lastCommit" + getLastCommit(table, "justin.kikuchi@jivesoftware.com"));
			System.out.println("1badgesWeek" + results[1]);
			System.out.println("1numCommits" + results[2]);
			System.out.println("1consecCommits" + results[3]);
			System.out.println("++++++++++++++++++++++++++++++++++++++++");

			//addRow(table,  "justin", "newboss@blah.com", "1999-6-14", 1, 1, 1, badges1);
			System.out.println("numbugs" + results[0]);
			System.out.println("lastCommit" + getLastCommit(table, "justin.kikuchi@jivesoftware.com"));
			System.out.println("badgesWeek" + results[1]);
			System.out.println("numCommits" + results[2]);
			System.out.println("consecCommits" + results[3]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	*/
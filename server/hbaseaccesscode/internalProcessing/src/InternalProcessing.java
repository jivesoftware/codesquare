
import java.io.IOException;
import java.util.ArrayList;
import java.util.NavigableSet;
import java.util.StringTokenizer;

import org.apache.hadoop.fs.Path;
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
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.conf.Configuration;


// Class that has nothing but a main.
// Does a Put, Get and a Scan against an hbase table.
public class InternalProcessing {
	public static void main(String[] args){
		/*ArrayList<String> test;
		System.out.println("asdf");
		test = testDateTimeBadges("Fri 2011-02-14", "17 3 45");
		for(int i=0;i<test.size();i++){
			System.out.println(test.get(i));
		}
		System.out.println("==============================");
		test();*/

		checkUpdateBadges("justin.kikuchi@jivesoftware.com", "boss@gmail.com", "Mon 1999-04-5", "10 6 28", "this is a message", 0);
		checkUpdateBadges("justin.kikuchi@jivesoftware.com", "boss@gmail.com", "Sat 1999-04-6", "10 6 28", "this is a message1", 1);
		checkUpdateBadges("justin.kikuchi@jivesoftware.com", "boss@gmail.com", "Fri 1999-04-6", "10 6 28", "jive this is a message1", 1);
	}
	public static void checkUpdateBadges(String email, String bossEmail, String date, String time, String message, int numBugs){

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

		HTable table = null;
		try{
			table = new HTable(config, "EmpBadges");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		
		String[] fields = {"badgesWeek", "numBugs", "numCommits", "consecCommits"};
		int[] fieldValues = getFields(table, email, fields);
		String lastCommit = getLastCommit(table, email);
		ArrayList<String> badges = testDateTimeBadges(date, time);
		System.out.println("lastcommit: " + lastCommit);
		if(lastCommit == null){
			lastCommit = "";
		}
		fieldValues[1] = fieldValues[1] + numBugs;
		fieldValues[2] = fieldValues[2] + 1;
		
		int[] consecCommits = checkConsecCommits(table, email, date,  lastCommit, fieldValues[3]);
		
		
		badges.addAll(checkNumericalBadges(table, email, fieldValues, consecCommits, date));
		if(message.toLowerCase().contains("jive")){
			badges.add("26");
		}
		if((badges.size()+fieldValues[0]) > 7){
				badges.add("16");
		}
		
		ArrayList<String> aquiredBadges = new ArrayList<String>(getBadges(table, email));
		for(int i=0;i<badges.size();i++){
			if(aquiredBadges.contains(badges.get(i))){
				badges.remove(i--);
			}
		}
		String[] results = new String[badges.size()];
		addRow(table,  email, bossEmail, date, badges.size() + fieldValues[0], fieldValues[1], fieldValues[2], consecCommits[0], badges.toArray(results));
		
		test(table, email);
	}

	public static ArrayList<String> testDateTimeBadges(String date, String time){
		ArrayList<String> badges = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(time);
		String hour = st.nextToken();
		
		if(date.contains("-03-17")){
			badges.add("7");
		}
		else if(date.contains("-10-31")){
			badges.add("6");
		}
		else if(date.contains("-02-29")){
			badges.add("8");
		}
		else if(date.contains("-02-14")){
			badges.add("9");
		}
		else if(date.contains("-03-14")){
			badges.add("10");
		}

		if(date.contains("Mon") && Integer.parseInt(hour) <= 5){
			badges.add("18");
		}
		else if(date.contains("Fri") && Integer.parseInt(hour) >= 4){
			badges.add("19");
		}
		else if(date.contains("Sat") || date.contains("Sun")){
			badges.add("29");
		}
		if(Integer.parseInt(hour) >= 10){
			badges.add("12");
		}
		if(Integer.parseInt(hour) <= 6){
			badges.add("13");
		}
		
		return badges;
	}
	public static ArrayList<String> checkNumericalBadges(HTable table, String email, int[] fieldValues, int[] consecCommits, String commitDate){
		ArrayList<String> badges = new ArrayList<String>();
		int totNumBugs = fieldValues[1];
		int totNumCommits = fieldValues[2];
		
		//result[0] is commited person, and result[1] is (value = 1)2 commits in a day or (value = 2)2 commits in >5 days: 0 is none
		if(consecCommits[0] > 7){
			badges.add("30");
		}
		if(consecCommits[1] == 1){
			badges.add("27");
		}
		else if(consecCommits[1] == 2){
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
		
		if(totNumCommits > 0){
			badges.add("1");
		}
		else if(totNumCommits > 50){
			badges.add("2");
		}
		else if(totNumCommits > 500){
			badges.add("3");
		}
		else if(totNumCommits > 1000){
			badges.add("4");
		}
		else if(totNumCommits > 5000){
			badges.add("5");
		}

		return badges;
	}
	
	public static void addRow(HTable table,  String email, String bossEmail, String lastCommit, int badgesWeek, int numBugs, int numCommits, int consecCommits, String[] badges){
		Get get = new Get(Bytes.toBytes(email));
		Result data = null;
		try {
		     data = table.get(get);
		} catch(Exception e) {
			System.err.println();
		}
		 
		/*if (!data.isEmpty()) {
			System.out.println("Already Exists");
			return;
		}*/
		
		Put row = new Put(Bytes.toBytes(email));
		
		row.add(Bytes.toBytes("Info"),Bytes.toBytes("bossEmail"),Bytes.toBytes(bossEmail));
		row.add(Bytes.toBytes("Info"),Bytes.toBytes("lastCommit"),Bytes.toBytes(lastCommit));
		row.add(Bytes.toBytes("Info"),Bytes.toBytes("badgesWeek"),Bytes.toBytes(badgesWeek));
		row.add(Bytes.toBytes("Info"),Bytes.toBytes("numBugs"),Bytes.toBytes(numBugs));
		row.add(Bytes.toBytes("Info"),Bytes.toBytes("numCommits"),Bytes.toBytes(numCommits));
		row.add(Bytes.toBytes("Info"),Bytes.toBytes("consecCommits"),Bytes.toBytes(consecCommits));
		if(badges != null){
			for(int i=0; i < badges.length; i++){
				row.add(Bytes.toBytes("Badge"),Bytes.toBytes(badges[i]),Bytes.toBytes("1"));
			}
		}
		try {
		    table.put(row);
		} catch(Exception e) {
			System.err.println();
		}
	}
	
	public static void deleteRow(HTable table, String email){
		Delete d = new Delete(Bytes.toBytes(email));
			 
		try {
		    table.delete(d);
		} catch(Exception e) {
			System.err.println();
		}
	}
	
	public static void updateBadges(HTable table, String email, String[] badges){
		
		Put row = new Put(Bytes.toBytes(email));
		
		for(int i=0; i < badges.length-1; i=i+2){
			row.add(Bytes.toBytes("Badge"),Bytes.toBytes(badges[i]),Bytes.toBytes(badges[i+1]));
		}
		
		try {
		    table.put(row);
		} catch(Exception e) {
			System.err.println();
		}
	}
	public static ArrayList<String> getBadges(HTable table, String email){
		Get get = new Get(Bytes.toBytes(email));	 
		Result data = null;
		ArrayList<String> resultingBadges = new ArrayList<String>();
		try {
		     data = table.get(get);
		} catch(Exception e) {
			System.err.println();
		}
		 
		if (data == null) {
			System.out.println("Not found");
			return resultingBadges;
		}
		if(data.isEmpty()){
			return resultingBadges;
		}
		 
		byte[] badges = Bytes.toBytes("Badge");

		NavigableSet<byte[]> badges_awarded = data.getFamilyMap(badges).descendingKeySet();
		for(byte[] badge: badges_awarded){
			resultingBadges.add(new String(badge));
		}
		
		/*
		String[] result =  new String[resultingBadges.size()];
		result = resultingBadges.toArray(result);
		
		for(int i=0; i<result.length; i++){
			String customDescription = new String(data.getValue(Bytes.toBytes("Badge"), Bytes.toBytes(result[i])));
			int currentBadgeIndex = resultingBadges.indexOf(result[i]);
			resultingBadges.add(currentBadgeIndex+1, customDescription);
		}*/

		
		return resultingBadges;
	}
	public static void updateBoss(HTable table, String email, String bossEmail){
		Put row = new Put(Bytes.toBytes(email));
		

		row.add(Bytes.toBytes("Info"),Bytes.toBytes("bossEmail"),Bytes.toBytes(bossEmail));
		
		try {
		    table.put(row);
		} catch(Exception e) {
			System.err.println();
		}
	}
	public static String getLastCommit(HTable table, String email){
		Get get = new Get(Bytes.toBytes(email));	 
		Result data = null;

		try {
		     data = table.get(get);
		} catch(Exception e) {
			System.err.println();
		}
		 
		if (data.isEmpty()) {
			return null;
		}
		
		return new String(data.getValue(Bytes.toBytes("Info"), Bytes.toBytes("lastCommit")));
	}
	public static int[] getFields(HTable table, String email, String[] fields){
		Get get = new Get(Bytes.toBytes(email));
		int[] results = new int[fields.length];
		Result data = null;

		try {
		     data = table.get(get);
		} catch(Exception e) {
			System.err.println();
		}
		 
		if (data.isEmpty()) {
			return results;
		}
		
		for(int i=0; i < fields.length; i++){
			results[i] = byteArrayToInt(data.getValue(Bytes.toBytes("Info"), Bytes.toBytes(fields[i])), 0);
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

	public static int[] checkConsecCommits(HTable table, String email, String commitDate, String lastCommit, int consecCommitsOld){
		int[] result = new int[2]; //result[0] is commited person, and result[1] is (value = 1)2 commits in a day or (value = 2)2 commits in >5 days: 0 is none
		
		result[0] = 1;
		result[1] = 0;
		if(lastCommit != ""){
			StringTokenizer st = new StringTokenizer(lastCommit);
			String dayof=st.nextToken();
			String yearOld = st.nextToken("-");
			String monthOld = st.nextToken("-");
			String dayOld = st.nextToken("-");
	
			st = new StringTokenizer(commitDate);
			dayof=st.nextToken();
			String year = st.nextToken("-");
			String month = st.nextToken("-");
			String day = st.nextToken("-");
			
			System.out.println("OLD: "+yearOld + monthOld + dayOld);
			System.out.println("NEW: "+year + month + day);
			if(year.equals(yearOld) && month.equals(monthOld)){
				if((Integer.parseInt(day)-1) == Integer.parseInt(dayOld)){
					System.out.println("HERE ");
					result[0] = consecCommitsOld + 1;
				}
				else if(day.equals(dayOld)){
					System.out.println("HERE 1");
					result[0]= consecCommitsOld;
					result[1] = 1;
				}
				else if((Integer.parseInt(day) - Integer.parseInt(dayOld)) > 4){
					result[1] = 2;
				}
			}
		}
		return result;
	}
	
	public static void test(HTable table, String email){
		System.out.println("Pringting data........................................................");
		ArrayList<String> badges_awarded = getBadges(table, email);
		for(int i=0;i<badges_awarded.size();i++)
			System.out.println("Badges=\t" +badges_awarded.get(i));
		String[] fields = {"badgesWeek", "numBugs", "numCommits", "consecCommits"};
		int[] results = getFields(table, email, fields);
		System.out.println("1numbugs=\t" + results[1]);
		System.out.println("1lastCommit=\t" + getLastCommit(table, email));
		System.out.println("1badgesWeek=\t" + results[0]);
		System.out.println("1numCommits=\t" + results[2]);
		System.out.println("1consecCommits=\t" + results[3]);
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
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

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
		String[] test;
		System.out.println("asdf");
		test = testDateTimeBadges("Fri 2011-02-14", "17 3 45");
		for(int i=0;i<test.length;i++){
			System.out.println(test[i]);
		}
	}
	
	public static String[] testDateTimeBadges(String date, String time){
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
		
		String[] out = new String[badges.size()];
		return badges.toArray(out);
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
			addRow(table, "justin.kikuchi@jivesoftware.com", "boss@gmail.com", "1999-6-13", 1, 4, 5, 6, badges);
			String[] badges1 = {"1","desc1"};
			//updateBadges(table, "justin.kikuchi@jivesoftware.com", badges1);
			updateBoss(table, "justin.kikuchi@jivesoftware.com", "newboss@gmail.com");

			String[] badges_awarded = getBadges(table, "justin.kikuchi@jivesoftware.com");
			for(int i=0;i<badges_awarded.length;i++)
				System.out.println(badges_awarded[i]);
			System.out.println(getField(table, "justin.kikuchi@jivesoftware.com", "numBugs"));
			System.out.println(getLastCommit(table, "justin.kikuchi@jivesoftware.com"));
			System.out.println(getField(table, "justin.kikuchi@jivesoftware.com", "badgesWeek"));
			System.out.println(getField(table, "justin.kikuchi@jivesoftware.com", "numCommits"));
			updateRow(table,  "justin.kikuchi@jivesoftware.com", "newboss@blah.com", "2000-7-19", 1, 1, 1, badges1);
			System.out.println(getField(table, "justin.kikuchi@jivesoftware.com", "numBugs"));
			System.out.println(getLastCommit(table, "justin.kikuchi@jivesoftware.com"));
			System.out.println(getField(table, "justin.kikuchi@jivesoftware.com", "badgesWeek"));
			System.out.println(getField(table, "justin.kikuchi@jivesoftware.com", "numCommits"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		row.add(Bytes.toBytes("Info"),Bytes.toBytes("consecCommits"),Bytes.toBytes(numCommits));
		if(badges != null){
			for(int i=0; i < badges.length-1; i=i+2){
				row.add(Bytes.toBytes("Badge"),Bytes.toBytes(badges[i]),Bytes.toBytes(badges[i+1]));
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
	public static String[] getBadges(HTable table, String email){
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
			return null;
		}
		 
		byte[] badges = Bytes.toBytes("badges");

		NavigableSet<byte[]> badges_awarded = data.getFamilyMap(badges).descendingKeySet();
		for(byte[] badge: badges_awarded){
			resultingBadges.add(new String(badge));
		}
		
		String[] result =  new String[resultingBadges.size()];
		result = resultingBadges.toArray(result);
		
		for(int i=0; i<result.length; i++){
			String customDescription = new String(data.getValue(Bytes.toBytes("Badge"), Bytes.toBytes(result[i])));
			int currentBadgeIndex = resultingBadges.indexOf(result[i]);
			resultingBadges.add(currentBadgeIndex+1, customDescription);
		}

		
		result =  new String[resultingBadges.size()];
		return resultingBadges.toArray(result);
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
	public static int getField(HTable table, String email, String field){
		Get get = new Get(Bytes.toBytes(email));	 
		Result data = null;

		try {
		     data = table.get(get);
		} catch(Exception e) {
			System.err.println();
		}
		 
		if (data.isEmpty()) {
			return 0;
		}
		return byteArrayToInt(data.getValue(Bytes.toBytes("Info"), Bytes.toBytes(field)), 0);
	}
	
	public static int byteArrayToInt(byte[] b, int offset) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (b[i + offset] & 0x000000FF) << shift;
        }
        return value;
    }
	
	public static void updateRow(HTable table,  String email, String bossEmail, String lastCommit, int badgesWeek, int numBugs, int numCommits, String[] badges){
		int badgesWeekOld = getField(table, email, "badgesWeek");
		int numBugsOld = getField(table, email, "numBugs");
		int numCommitsOld = getField(table, email, "numCommits");
		int consecCommitsOld = getField(table, email, "consecCommits");
		
		addRow(table,  email, bossEmail, lastCommit, badgesWeek + badgesWeekOld, numBugs + numBugsOld, numCommits + numCommitsOld, 1, badges);
	}
	/*public static int checkConsecCommits(HTable table, String email, String lastCommit){
		int consecCommits = 1;
		String lastCommitOld = getLastCommit(table, email);
		StringTokenizer st = new StringTokenizer(lastCommitOld, "-");
		st.nextToken("-");
		st.nextToken("-");
		st.nextToken("-");
		if(st.nextToken("-"))
		lastCommitOld = lastCommitOld
		return 0;
		
	}*/
}
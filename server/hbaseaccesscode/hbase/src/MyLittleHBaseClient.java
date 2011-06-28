
import java.io.IOException;
import java.util.ArrayList;
import java.util.NavigableSet;

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
public class MyLittleHBaseClient {
	
	
	public static void main(String[] args){
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
			addRow(table, "justin.kikuchi@jivesoftware.com", "boss@gmail.com", "1999-6-13", 4, 5, 6, badges);
			String[] badges1 = {"1","desc1"};
			addRow(table, "justin.kikuchi@jivesoftware.com", "boss@gmail.com", "1999-6-13", 4, 5, 6, badges1);
			//updateBadges(table, "justin.kikuchi@jivesoftware.com", badges1);
			updateBoss(table, "justin.kikuchi@jivesoftware.com", "newboss@gmail.com");

			String[] badges_awarded = getBadges(table, "justin.kikuchi@jivesoftware.com");
			for(int i=0;i<badges_awarded.length;i++)
				System.out.println(badges_awarded[i]);
			System.out.println(getField(table, "justin.kikuchi@jivesoftware.com", "numBugs"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void addRow(HTable table,  String email, String bossEmail, String lastCommit, int badgesWeek, int numBugs, int numCommits, String[] badges){
		Get get = new Get(Bytes.toBytes(email));
		Result data = null;
		try {
		     data = table.get(get);
		} catch(Exception e) {
			System.err.println();
		}
		 
		if (!data.isEmpty()) {
			System.out.println("Already Exists");
			return;
		}
		
		Put row = new Put(Bytes.toBytes(email));
		
		row.add(Bytes.toBytes("Info"),Bytes.toBytes("bossEmail"),Bytes.toBytes(bossEmail));
		row.add(Bytes.toBytes("Info"),Bytes.toBytes("lastCommit"),Bytes.toBytes(lastCommit));
		row.add(Bytes.toBytes("Info"),Bytes.toBytes("badgesWeek"),Bytes.toBytes(badgesWeek));
		row.add(Bytes.toBytes("Info"),Bytes.toBytes("numBugs"),Bytes.toBytes(numBugs));
		row.add(Bytes.toBytes("Info"),Bytes.toBytes("numCommits"),Bytes.toBytes(numCommits));
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
		String test = new String(data.getValue(Bytes.toBytes("Info"), Bytes.toBytes(field)));
		System.out.println("adf" + test);
		return Integer.parseInt(test);
	}
	public static void updateRow(HTable table,  String email, String bossEmail, String lastCommit, int badgesWeek, int numBugs, int numCommits, String[] badges){
		updateBoss(table, email, bossEmail);
		updateBadges(table, email, badges);
		
		
		
	
	}
}
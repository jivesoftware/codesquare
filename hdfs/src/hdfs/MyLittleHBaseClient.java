package hdfs;
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
			if (!admin.tableExists("CodeSquare")) {
				admin.createTable( new HTableDescriptor("CodeSquare"));
			}
			admin.disableTable("CodeSquare");
			admin.addColumn("CodeSquare", new HColumnDescriptor("info"));
		    admin.addColumn("CodeSquare", new HColumnDescriptor("badges"));
			admin.enableTable("CodeSquare");
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
			table = new HTable(config, "CodeSquare");
			String[] badges = {"2", "4", "6"};
			add_row(table, "justin.kikuchi@jivesoftware.com", "boss@gmail.com", badges);
			String[] badges1 = {"1"};
			add_row(table, "justin.kikuchi@jivesoftware.com", "boss@gmail.com", badges1);
			//delete_row(table, "justin.kikuchi@jivesoftware.com");
			String[] badges_awarded = get_badges(table, "justin.kikuchi@jivesoftware.com");
			for(int i=0;i<badges_awarded.length;i++)
				System.out.println(badges_awarded[i]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println(e.getMessage());
		}
	}
	
	public static void add_row(HTable table,  String email, String bossEmail, String[] badges){
		
		Put row = new Put(Bytes.toBytes(email));
		
		row.add(Bytes.toBytes("info"),Bytes.toBytes("bossEmail"),Bytes.toBytes(bossEmail));
		for(int i=0; i < badges.length; i++){
			row.add(Bytes.toBytes("badges"),Bytes.toBytes(badges[i]),Bytes.toBytes("1"));
		}
		
		try {
		    table.put(row);
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public static void delete_row(HTable table, String email){
		Delete d = new Delete(Bytes.toBytes(email));
			 
		try {
		    table.delete(d);
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public static void add_badges(HTable table, String email, String[] badges){
		Put row = new Put(Bytes.toBytes(email));
		
		for(int i=0; i < badges.length; i++){
			row.add(Bytes.toBytes("badges"),Bytes.toBytes(badges[i]),Bytes.toBytes("1"));
		}
		
		try {
		    table.put(row);
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public static String[] get_badges(HTable table, String email){
		Get get = new Get(Bytes.toBytes(email));	 
		Result data = null;
		ArrayList<String> resulting_badges = new ArrayList<String>();
		try {
		     data = table.get(get);
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
		 
		if (data == null) {
			System.out.println("Not found");
		}
		 
		byte[] badges = Bytes.toBytes("badges");

		NavigableSet<byte[]> badges_awarded = data.getFamilyMap(badges).descendingKeySet();
		for(byte[] badge: badges_awarded){
			resulting_badges.add(new String(badge));
		}
		String[] result = new String[resulting_badges.size()];
		return resulting_badges.toArray(result);
	}
	
}
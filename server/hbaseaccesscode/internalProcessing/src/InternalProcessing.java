
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
public class InternalProcessing {
	
	
	public static void main(String[] args){
		Configuration config = HBaseConfiguration.create();
		//config.addResource(new Path("/Users/diivanand.ramalingam/Downloads/hbase/conf/hbase-site.xml"));
		 
		//Create a table
		try {
			HBaseAdmin admin = new HBaseAdmin(config);
			if (!admin.tableExists("Badges")) {
				admin.createTable( new HTableDescriptor("Badges"));
			}
			admin.disableTable("Badges");
			admin.addColumn("Badges", new HColumnDescriptor("Info"));
			admin.enableTable("Badges");
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
			table = new HTable(config, "Badges");
			deleteRow(table, "1");
			addBadge(table, "1", "First Commit", "file:///blah/blah/foo.png", "This badge says your a rookie.");
			String[] badges_awarded = getBadgeInfo(table, "1");
			if(badges_awarded != null)
				for(int i=0;i<badges_awarded.length;i++)
					System.out.println(badges_awarded[i]);
			String[] badges_awarded1 = getBadgeInfo(table, "2");
			if(badges_awarded1 != null)
				for(int i=0;i<badges_awarded1.length;i++)
					System.out.println(badges_awarded1[i]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void addBadge(HTable table,  String badgeNumber, String badgeName, String iconURL, String description){
		
		Put row = new Put(Bytes.toBytes(badgeNumber));
		
		row.add(Bytes.toBytes("Info"),Bytes.toBytes("name"),Bytes.toBytes(badgeName));
		row.add(Bytes.toBytes("Info"),Bytes.toBytes("iconURL"),Bytes.toBytes(iconURL));
		row.add(Bytes.toBytes("Info"),Bytes.toBytes("description"),Bytes.toBytes(description));
		
		
		try {
		    table.put(row);
		} catch(Exception e) {
			System.err.println();
		}
	}
	
	public static void deleteRow(HTable table, String badgeNumber){
		Delete d = new Delete(Bytes.toBytes(badgeNumber));
			 
		try {
		    table.delete(d);
		} catch(Exception e) {
			System.err.println();
		}
	}
	
	public static String[] getBadgeInfo(HTable table, String badgeNumber){
		Get get = new Get(Bytes.toBytes(badgeNumber));	 
		Result data = null;

		try {
		     data = table.get(get);
		} catch(Exception e) {
			System.err.println();
		}
		 
		if (data.isEmpty()) {
			return null;
		}
		
		String[] result = new String[4];
		result[0] = badgeNumber;
		result[1] = new String(data.getValue(Bytes.toBytes("Info"), Bytes.toBytes("name")));
		result[2] = new String(data.getValue(Bytes.toBytes("Info"), Bytes.toBytes("description")));
		result[3] = new String(data.getValue(Bytes.toBytes("Info"), Bytes.toBytes("iconURL")));

		return result;
	}
	
}
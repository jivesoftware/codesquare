import java.io.IOException;
import java.util.ArrayList;
import java.util.NavigableSet;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

public class mapreducHbase {
	private static HTable table;
	
	public static void main(String[] args){
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

		table = null;
		try {
			table = new HTable(config, "EmpBadges");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}
	/***
	 * This method retrieves acquired badges checks against the new badges, and updates the table
	 * @param email Row Identifier
	 * @param badge New badge to add
	 */
	@SuppressWarnings("unchecked")
	public static void addBadges(String email, String badge) {
		Object[] badgeList = getBadges(table, email);
		ArrayList<String> aquiredBadges = null;
		String newBadges = null;
		try{
			aquiredBadges = (ArrayList<String>) badgeList[0];
			newBadges = (String)badgeList[1];
		}catch(java.lang.NullPointerException e){
			//USER IS HAS NOT INSTALLED APP
			return;
		}
		if(!aquiredBadges.contains(badge)){
			updateBadges(table, email, badge, newBadges+" "+badge);
		}
	}
	/***
	 * Returns the acquired and new badges
	 * 
	 * @param table
	 *            HTable to modify
	 * @param email
	 *            Row Identifier
	 * @return Object[0] is an ArrayList of acquired badges, Object[1] is newly
	 *         acquired badges
	 */
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
			return null;
		}
		if (data.isEmpty()) {
			return null;
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
	 * Adds Badges to the specified user
	 * 
	 * @param table
	 *            HTable to modify
	 * @param email
	 *            Row Identifier
	 * @param badges
	 *            Badges to Add
	 */
	public static void updateBadges(HTable table, String email, String badge, String newBadges) {

		Put row = new Put(Bytes.toBytes(email));

		row.add(Bytes.toBytes("Badge"), Bytes.toBytes(badge),
					Bytes.toBytes("1"));
		row.add(Bytes.toBytes("Info"), Bytes.toBytes("newBadges"),
				Bytes.toBytes(newBadges));

		try {
			table.put(row);
		} catch (Exception e) {
			System.err.println();
		}
	}
}

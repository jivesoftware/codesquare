package com.jivesoftware.codesquare;
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
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.conf.Configuration;

// Class that has nothing but a main.
// Does a Put, Get and a Scan against an hbase table.
/***
 * This Class calls the git parser and checks for basic badges, and updates the
 * hbase
 * 
 * @author justin.kikuchi
 * 
 */
public class BasicBadgesProcessor {

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













}
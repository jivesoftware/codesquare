package codesquare;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.NavigableSet;
import java.util.Random;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

/**
 * 
 * Class providing commonly used functions
 */
public class Toolbox {

	/**
	 * 
	 * @return the Jive CodeSquare HDFS file system
	 */
	public static FileSystem getHDFS(Configuration config) {

		// Connect to and Return HDFS
		try {
			FileSystem dfs = FileSystem.get(config);
			return dfs;
		} catch (IOException e) {
			System.err.println(e.getMessage());
			return null;
		}

	}
	
	/**
	 * 
	 * Retrieves a configuration object pertaining to the cluster our HDFS is located in 
	 */
	public static Configuration getConfiguration(){
		Configuration config = new Configuration();
		config.set("fs.default.name",
				"hdfs://hadoopdev001.eng.jiveland.com:54310");
		config.set("hadoop.log.dir", "/hadoop001/data/hadoop/logs");
		config.set("hadoop.tmp.dir", "/hadoop001/tmp");
		config.set("io.file.buffer.size", "131072");
		config.set("fs.inmemory.size.mb", "200");
		config.set("fs.checkpoint.period", "900");

		config.set("dfs.datanode.max.xceivers", "4096");
		config.set("dfs.block.size", "134217728");
		config.set(
				"dfs.name.dir",
				"/hadoop001/data/datanode,/hadoop002/data/datanode,/hadoop003/data/datanode,/hadoop004/data/datanode,/hadoop005/data/datanode,/hadoop006/data/datanode,/hadoop007/data/datanode,/hadoop008/data/datanode,/hadoop009/data/datanode,/hadoop010/data/datanode,/hadoop011/data/datanode,/hadoop012/data/datanode");
		config.set("dfs.umaskmode", "007");
		config.set("dfs.datanode.du.reserved", "107374182400");
		config.set("dfs.datanode.du.pct", "0.85f");
		return config;
	}
	
	
	/**
	 * 
	 * Retrieves a configuration object pertaining to the cluster our HBase is located in 
	 */
	public static Configuration getHBaseConfiguration(){
		Configuration config = HBaseConfiguration.create();
		config.set("hbase.cluster.distributed", "true");
		config.set("hbase.rootdir",
				"hdfs://hadoopdev008.eng.jiveland.com:54310/hbase");
		config.set(
				"hbase.zookeeper.quorum",
				"hadoopdev008.eng.jiveland.com,hadoopdev002.eng.jiveland.com,hadoopdev001.eng.jiveland.com");
		config.set("hbase.zookeeper.property.clientPort", "2181");
		config.set("hbase.hregion.max.filesize", "1073741824");
		return config;
	}

	/**
	 * 
	 * adds all non-hidden files to InputPath (excludes "_SUCCESS" file)
	 * 
	 */
	public static void addDirectory(Job job, FileSystem hdfs, Path directory)
			throws Exception {

		if (hdfs.exists(directory) && !directory.toString().contains("_logs")) {

			if (hdfs.isFile(directory)) {
				if (directory.toString().contains("_SUCCESS")) {
					//do nothing 
				}
				else {
					FileInputFormat.addInputPath(job, directory);
				}
			} else {

				FileStatus[] fs = hdfs.listStatus(directory);
				
				for (FileStatus entry : fs) {
					addDirectory(job, hdfs, entry.getPath());
				}

			}
		}

		// directory.get

		/*
		 * File[] entries = directory.listFiles(); for (File entry : entries) {
		 * if (entry.isFile() && (!entry.isHidden()) &&
		 * (!entry.toString().contains("_SUCCESS"))) {
		 * FileInputFormat.addInputPath(job, new Path(entry.toString())); } if
		 * (entry.isDirectory()) { addDirectory(job, hdfs,entry); } }
		 */
	}

	/**
	 * 
	 * deletes directory and its contents
	 * 
	 * @throws IOException
	 * 
	 */
	public static boolean deleteDirectory(Path path, FileSystem hdfs)
			throws IOException {
		return hdfs.delete(path, true);
		/*
		 * if (path.exists()) { File[] files = path.listFiles(); for (int i = 0;
		 * i < files.length; i++) { if (files[i].isDirectory()) {
		 * deleteDirectory(files[i]); } else { files[i].delete(); } } }
		 */
	}

	/**
	 * 
	 * generates a random string of length 36 (intended to be used for file
	 * names)
	 * 
	 */
	public static String generateString() {
		Random r = new Random();
		return "/user/interns/Commits/tmpOutput/"
				+ Long.toString(Math.abs(r.nextLong()), 36);
	}

	/**
	 * 
	 * adds all non-hidden files to InputPath (excludes "_SUCCESS" file)
	 * 
	 */
	@SuppressWarnings("deprecation")
	public static int subtractTime(String[] x, String[] y) {
		Time time1 = new Time(0, new Integer(x[0]), new Integer(x[1]));
		Time time2 = new Time(0, new Integer(y[0]), new Integer(y[1]));
		System.out.println((int) (time1.getTime() - time2.getTime()));
		return Math.abs((int) (time1.getTime() - time2.getTime()));
	}

	/***
	 * This method retrieves acquired badges checks against the new badges, and
	 * updates the table
	 * 
	 * @param email
	 *            Row Identifier
	 * @param badge
	 *            New badge to add
	 */
	@SuppressWarnings("unchecked")
	public static void addBadges(String email, String badge, HTable table) {
		Object[] badgeList = getBadges(table, email);
		ArrayList<String> aquiredBadges = null;
		String newBadges = null;
		try {
			aquiredBadges = (ArrayList<String>) badgeList[0];
			newBadges = (String) badgeList[1];
		} catch (java.lang.NullPointerException e) {
			// USER IS HAS NOT INSTALLED APP
			return;
		}
		if (!aquiredBadges.contains(badge)) {
			updateBadges(table, email, badge, newBadges + " " + badge);
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
	public static void updateBadges(HTable table, String email, String badge,
			String newBadges) {

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
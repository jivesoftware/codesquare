package com.jivesoftware.toolbox;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.jivesoftware.backendServlet.Commit;

public class HDFS {

	public static Configuration setConfiguration(Configuration config) {
		config.set("fs.default.name", "hdfs://hadoopdev001.eng.jiveland.com:54310");
		config.set("hadoop.log.dir", "/hadoop001/data/hadoop/logs");
		config.set("hadoop.tmp.dir", "/hadoop001/tmp");
		config.set("io.file.buffer.size", "131072");
		config.set("fs.inmemory.size.mb", "200");
		config.set("fs.checkpoint.period", "900");
		config.set("dfs.datanode.max.xceivers", "4096");
		config.set("dfs.block.size", "134217728");
		config.set("dfs.name.dir", "/hadoop001/data/datanode,/hadoop002/data/datanode,/hadoop003/data/datanode,/hadoop004/data/datanode,/hadoop005/data/datanode,/hadoop006/data/datanode,/hadoop007/data/datanode,/hadoop008/data/datanode,/hadoop009/data/datanode,/hadoop010/data/datanode,/hadoop011/data/datanode,/hadoop012/data/datanode");
		config.set("dfs.umaskmode", "007");
		config.set("dfs.datanode.du.reserved", "107374182400");
		config.set("dfs.datanode.du.pct", "0.85f");
		config.set("dfs.permissions", "false");
		return config;
	}
	
	/**
	 * Extends pathNames
	 * 
	 * @param current
	 * @param subDirName
	 * @return a new Path with a subdirectory attached to current
	 */
	private static Path extendPath(Path current, String subDirName) {
		return new Path(current.toString() + "/" + subDirName);
	}
	
	// HDFS methods
	/**
	 * inserts the commit objects as String into .txt files
	 * 
	 * @param commits
	 *            An arrayList of Commit objects
	 */
	public static void insertCommitsIntoHDFS(ArrayList<Commit> commits) {
		try {
			// Connect and open HDFS and set path variables
			FileSystem dfs = FileSystem.get(setConfiguration(new Configuration()));

			String pathString = "/user/interns/Commits";
			Path src = new Path(pathString);

			if (!dfs.exists(src)) {
				dfs.mkdirs(src);
			}

			for (Commit c : commits) {
		        // If the folders don't exist, create them
		        String[] extensions = {((Integer) c.getPushDate().getYear()).toString(),
		                ((Integer) c.getPushDate().getMonth()).toString(), c.getPushDate().getDay(),
		                ((Integer) c.getPushDate().getHour()).toString(), ((Integer) c.getPushDate().getMinute()).toString()};
		        for (int i = 0; i<extensions.length; i++){
		            src = extendPath(src, extensions[i]);
		            if (!dfs.exists(src)) {
		                dfs.mkdirs(src);
		            }
		        }

				// Write file
				src = new Path(src.toString() + "/" + c.getId() + ".txt");

				if (dfs.exists(src)) { dfs.delete(src, false); }

				FSDataOutputStream fs = dfs.create(src);
				fs.write(c.toString().getBytes());
				fs.close();

				src = new Path("/user/interns/Commits");
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

	}
	
}

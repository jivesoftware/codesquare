package codesquare.badges;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;

import codesquare.Toolbox;
import codesquare.badges.badge_20_24.Pass2;
import codesquare.badges.sharedpasses.LOC;

/**
 * Class to be run daily for badges 20 and 24
 * 
 * returned file format is {empId} {badge#}
 * where "{" and "}" are not expressed
 * ie. 4825 24 
 * 
 * accepts a directory - searches for all files recursively
 * 
 * 20 - negative LOC badge - employee commits more LOC than his boss
 * 24 - largest LOC badge - employee commits more LOC than all of his peers
 * 
 * @author deanna.surma
 * 
 */
public class Badge_20_24 {	
	public static void main(String[] args) throws Exception {
		Configuration hdfsConfig = Toolbox.getConfiguration();
		FileSystem hdfs = Toolbox.getHDFS(hdfsConfig);
		Configuration hBaseConfig = Toolbox.getHBaseConfiguration();
		System.out.println("I've got the HBase Configuration");
		HTable table = new HTable(hBaseConfig, "EmpBadges");
		String output1 = Toolbox.generateString();
		new LOC(args[0], output1, hdfsConfig, hdfs);
		System.out.println("Badge_20_24 FINISHED!!!");
		new Pass2(output1, hdfsConfig, hdfs);
		Toolbox.deleteDirectory(new Path(output1), hdfs);
		hdfs.close();
		table.close();
	}
}
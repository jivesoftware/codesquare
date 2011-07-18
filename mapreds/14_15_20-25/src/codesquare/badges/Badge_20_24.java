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
		Configuration config = HBaseConfiguration.create();
		config.set("hbase.cluster.distributed", "true");
		config.set("hbase.rootdir",
				"hdfs://hadoopdev008.eng.jiveland.com:54310/hbase");
		config.set(
				"hbase.zookeeper.quorum",
				"hadoopdev008.eng.jiveland.com,hadoopdev002.eng.jiveland.com,hadoopdev001.eng.jiveland.com");
		config.set("hbase.zookeeper.property.clientPort", "2181");
		config.set("hbase.hregion.max.filesize", "1073741824");
		HTable table = new HTable(config, "EmpBadges");
		
		String output1 = Toolbox.generateString();
		FileSystem hdfs = Toolbox.getHDFS();
		new LOC(args[0], output1);
		System.out.println("Badge_20_24 FINISHED!!!");
		Pass2 x = new Pass2(output1);
		Toolbox.addBadges(x.getMaxEmp(), "24", table);
		
		/*
		Writer output = null;
		File file = new File(output2);
		output = new BufferedWriter(new FileWriter(file));
		output.write(x.getMaxEmp()+" 24");
		output.close();
		*/
		
		Toolbox.deleteDirectory(new Path(output1), hdfs);
		hdfs.close();
	}
}
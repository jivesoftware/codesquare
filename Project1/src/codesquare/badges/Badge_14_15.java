package codesquare.badges;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

import codesquare.Toolbox;
import codesquare.badges.badge_14_15.Pass1;

/**
 * Class to be run weekly for badges 21, 22, and 23
 * 
 * returned file format is {empId} {badge#}
 * where "{" and "}" are not expressed
 * ie. 4825 24 
 * 
 * accepts a directory - searches for all files recursively
 * 
 * 14 - 2 ppl, 1 hour, 1 file
 * 15 - 9 ppl, 1 hour, 1 file
 * 
 * @author deanna.surma
 * 
 */
public class Badge_14_15 {
	public static void main(String[] args) throws Exception {
		Configuration config = Toolbox.getConfiguration();
		FileSystem hdfs = Toolbox.getHDFS(config);
		new Pass1(args[0],config,hdfs);
		System.out.println("Badge_14_15 FINISHED!!!");
		hdfs.close();
	}
}


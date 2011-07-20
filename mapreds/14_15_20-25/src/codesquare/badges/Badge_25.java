package codesquare.badges;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

import codesquare.Toolbox;
import codesquare.badges.badge_25.Pass1;

/**
 * Class to be run weekly for badge 25
 * 
 * returned file format is {empId} {badge#}
 * where "{" and "}" are not expressed
 * ie. 4825 24 
 * 
 * accepts 2 directories - searches for all files recursively
 * 
 * 25 - 2 + ppl same directory, within 10 seconds
 * 
 * @author deanna.surma
 * 
 */
public class Badge_25 {
	public static void main(String[] args) throws Exception {
		Configuration config = Toolbox.getConfiguration();
		FileSystem hdfs = Toolbox.getHDFS(config);
		new Pass1(args[0], args[1], config, hdfs);
		System.out.println("Badge_25 FINISHED!!!");
		hdfs.close();
	}
}


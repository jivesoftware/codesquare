package codesquare.badges;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.HTable;

import codesquare.Toolbox;
import codesquare.badges.badge_20_24.Pass2;
import codesquare.badges.badge_21_22_23.Pass3;
import codesquare.badges.badge_21_22_23.X;
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
public class Test {	
	public static void main(String[] args) throws Exception {
			Configuration config = Toolbox.getConfiguration();
			FileSystem hdfs = Toolbox.getHDFS(config);
			String output1 = Toolbox.generateString();
			String output2 = Toolbox.generateString();
			
			Path src = new Path("/user/interns/Extras/bossList.txt");
	        FSDataOutputStream fs = hdfs.create(src);
	        Toolbox.getBossFile(fs, Toolbox.getBosses());
	        fs.close();
	        new X(output1, output2, config, hdfs);
			System.out.println("LOC2 FINISHED!!!");
			
			//new X(output1, output2, config, hdfs);
			//System.out.println("X FINISHED!!!");
			
			Toolbox.deleteDirectory(new Path(output1),hdfs);
			Toolbox.deleteDirectory(new Path(output2),hdfs);	
			hdfs.close();
		}
	}
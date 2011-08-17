package com.jivesoftware.toolbox;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.jivesoftware.backendServlet.Commit;import java.util.Calendar;
;

public class HDFSTools {

	/**
	 * 
	 * @return the Configuration to the HDFS that contains our folders that store out commits
	 */
	public static Configuration getConfiguration() {
            Configuration config = new Configuration();
            Path file = new Path("conf/hdfs_conf.xml");
            config.addResource(file);
		return config;
	}
	
	// HDFS methods
	
        public static Path makePath(FileSystem dfs, Calendar date) throws IOException{
            Path src = new Path("/user/interns/Commits");
            String[] extensions = 
        	{((Integer) date.get(Calendar.YEAR)).toString(),
                ((Integer) (date.get(Calendar.MONTH) + 1)).toString(),
                ((Integer) date.get(Calendar.DAY_OF_MONTH)).toString(),
                ((Integer) date.get(Calendar.HOUR)).toString(),
                ((Integer) date.get(Calendar.MINUTE)).toString()};
            for (int i = 0; i<extensions.length; i++){
                src = new Path(src.toString() + "/" + extensions[i]);
                if (!dfs.exists(src)) {
                    dfs.mkdirs(src);
                }
            }
            return src;
        }
	
	/**
	 * Writes the Commit object as a string in it's own .txt file in the appropriate folder in HDFS
	 * @param dfs The HDFS that contains our folders
	 * @param c The Commit object
	 * @throws IOException
	 */
	public static boolean writeCommitToHDFS(FileSystem dfs, Commit c) throws IOException{
            // If the folders don't exist, create them
            Path src = makePath(dfs, c.getPushDate().getLocal());
                
            // Write file
            src = new Path(src.toString() + "/" + c.getId() + ".txt");
            if(dfs.exists(src)){
                return false;
            }
            FSDataOutputStream fs = dfs.create(src);
            fs.write(c.toString().getBytes());
            fs.close();
            System.out.println("File Written: " + src.toString() + "; " + c.toString());
            return true;
	}
	
}

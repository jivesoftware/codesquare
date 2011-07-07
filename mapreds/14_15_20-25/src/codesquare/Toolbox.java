package codesquare;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Time;
import java.util.Random;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
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
	public static FileSystem getHDFS(){
		Configuration config = new Configuration();
		config.set("fs.default.name", "hdfs://10.45.111.143:8020/");
                    
        //Connect to and Return HDFS           
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
	 * adds all non-hidden files to InputPath (excludes "_SUCCESS" file)
	 * 
	 */
	public static void addDirectory(Job job, FileSystem hdfs,Path directory) throws Exception {
		
		if(hdfs.exists(directory)){
			
			if(hdfs.isFile(directory) && !directory.toString().contains("_SUCCESS")){
				FileInputFormat.addInputPath(job, directory);
				
			}else{
				File dir = new File(directory.toString());
				
				File[] entries = dir.listFiles();
				
				for(File entry: entries){
					addDirectory(job,hdfs,new Path(entry.getPath()));
				}
				
			}
		}else{
			throw new FileNotFoundException("Directory doesn't exist: " + directory.toString());
		}
		
		//directory.get
		
		/*
		File[] entries = directory.listFiles();
		for (File entry : entries) {
			if (entry.isFile() && (!entry.isHidden())
					&& (!entry.toString().contains("_SUCCESS"))) {
				FileInputFormat.addInputPath(job, new Path(entry.toString()));
			}
			if (entry.isDirectory()) {
				addDirectory(job, hdfs,entry);
			}
		}
		*/
	}

	/**
	 * 
	 * deletes directory and its contents
	 * 
	 */
	public static boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	/**
	 * 
	 * generates a random string of length 36 (intended to be used for file
	 * names)
	 * 
	 */
	public static String generateString() {
		Random r = new Random();
		return Long.toString(Math.abs(r.nextLong()), 36);
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
}
package codesquare;
import java.io.File;
import java.util.Random;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

/**
 * 
 * Class providing commonly used functions
 * 
 */
public class Toolbox {
	
/**
 * 
 * adds all non-hidden files to InputPath (excludes "_SUCCESS" file)
 * 
 */
public static void addDirectory(Job job, File directory) throws Exception {
	File[] entries = directory.listFiles();
	for (File entry : entries) {
		if (entry.isFile() && (!entry.isHidden()) && (!entry.toString().contains("_SUCCESS"))) {
			FileInputFormat.addInputPath(job, new Path(entry.toString()));
			}
		if (entry.isDirectory()) {
			addDirectory(job, entry);
			}
		}
	}

/**
 * 
 * deletes directory and its contents
 * 
 */
public static boolean deleteDirectory(File path) {
	if( path.exists() ) {
		File[] files = path.listFiles();
		for(int i=0; i<files.length; i++) {
			if(files[i].isDirectory()) {
				deleteDirectory(files[i]);
				}
			else {
				files[i].delete();
			}
		}
	}
	return( path.delete() );
	}

/**
 * 
 * generates a random string of length 36 (intended to be used for file names)
 * 
 */
public static String generateString() {
	Random r = new Random();
	return Long.toString(Math.abs(r.nextLong()), 36);
}
}
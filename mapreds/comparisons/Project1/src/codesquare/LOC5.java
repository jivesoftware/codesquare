package codesquare;        
import java.io.File;
import java.io.IOException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
        
/**
 * Class to award badges 20 and to store badge 24
 * it takes input from LOC1
 * and writes empId badge#
 * 
 * returned file format is {empId} {badge#}
 * where "{" and "}" are not expressed
 * ie. 4825 24 
 * 
 * Here we test for badges 20 and 24
 * 20 - negative LOC badge - employee commits more LOC than his boss
 * 24 - largest LOC badge - employee commits more LOC than all of his peers
 *  
 * accepts a directory - searches for all files recursively
 * 
 * @author deanna.surma
 * 
 */
public class LOC5 {
	// gets empId LOC AND empId BossId
	// returns empId LOC BossId AND empId LOC
	private static int maxEmpLOC = 0;
	private static int maxEmp = 0;
	
/**
 * return maxEmp
 */
public int getMaxEmp() {
		return maxEmp;
	}
	
public LOC5(String input, String output) throws Exception {
	    Configuration conf = new Configuration();
	    Job job = new Job(conf, "LOC1");
	    job.setOutputKeyClass(Text.class);
	    job.setOutputValueClass(Text.class);
	    job.setMapperClass(Map.class);
	    job.setReducerClass(Reduce.class);
	    job.setInputFormatClass(TextInputFormat.class);
	    job.setOutputFormatClass(TextOutputFormat.class);
	    Toolbox.addDirectory(job, new File(input));
	    FileOutputFormat.setOutputPath(job, new Path(output));
	    job.waitForCompletion(true);
	 }
 
/**
 * trivial
 * @write key: empId 
 * 		  value: {LOC}
 */
public static class Map extends Mapper<LongWritable, Text, Text, Text> {    
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
    	String line = value.toString();
    	String [] components = line.split("\\s+");
    	context.write(new Text(components[0]), new Text(components[1]));
    }
 } 
        
/**
 * 
 * @write key: empId   value: 20
 */
public static class Reduce extends Reducer<Text, Text, Text, Text> {
    public void reduce(Text key, Iterable<Text> values, Context context) 
      throws IOException, InterruptedException {
        for (Text val : values) {
        	int LOC = Integer.parseInt(val.toString().substring(1));
        	if (LOC < 0) {
        		context.write(key, new Text("20"));
        	}
        	if (LOC > maxEmpLOC) {
        		maxEmpLOC = LOC;
        		maxEmp = Integer.parseInt(key.toString());
        	}
        }
    }
 }
}
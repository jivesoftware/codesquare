package codesquare.badges.badge_14_15;      
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import codesquare.Toolbox;


/**
 * Class to find each Employee's LOC based on commit files
 * 
 * returned file format is {empId} #{LOC}
 * where "{" and "}" are not expressed
 * ie. 4825 #14 
 * 
 * accepts a directory - searches for all files recursively
 * 
 * @author deanna.surma
 * 
 */
public class Pass1 {
// receives commits
// returns empId #LOC
	
// stores all empIds that have badge 14
private static HashMap<String, Integer> badge14 = new HashMap<String, Integer>();
//stores all empIds that have badge 15
private static HashMap<String, Integer> badge15 = new HashMap<String, Integer>();

public Pass1(String input, String output) throws Exception {
	    Configuration conf = new Configuration();
	    FileSystem hdfs = codesquare.Toolbox.getHDFS();
	    
	    Job job = new Job(conf, "LOC1");
	    job.setOutputKeyClass(Text.class);
	    job.setOutputValueClass(Text.class);
	    job.setMapperClass(Map.class);
	    job.setReducerClass(Reduce.class);
	    job.setInputFormatClass(TextInputFormat.class);
	    job.setOutputFormatClass(NullOutputFormat.class);
	    Toolbox.addDirectory(job, hdfs,new Path(input));
	    //FileOutputFormat.setOutputPath(job, new Path(output));
	    job.waitForCompletion(true);
	 }

/**
 * 
 * @write key: document name   value: empId 
 */
 public static class Map extends Mapper<LongWritable, Text, Text, Text> {
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
    	String line = value.toString();
    	String [] components = line.split("\\s+");
    	String [] documents = components[8].substring(1,(components[8].length()-1)).split(",");
    	for (int i = 0; i < documents.length; i++) {
    		context.write(new Text(documents[i]), new Text(components[1]));
    		System.out.println(documents[i]+" "+components[1]);
    	}
    }
 } 
    
/**
 * 
 * @write key: empId   value: badge#
 */
 public static class Reduce extends Reducer<Text, Text, Text, Text> {
    public void reduce(Text key, Iterable<Text> values, Context context) 
      throws IOException, InterruptedException {
    	System.out.println("REDUCE");
        ArrayList<String> acc = new ArrayList<String>();
        for (Text val : values) {
        	acc.add(val.toString());
        }
        if (acc.size() >= 2) {
        	for (int i = 0; i < acc.size(); i++) {
        		if (!badge14.containsKey(acc.get(i))){
        			context.write(new Text(acc.get(i)), new Text("14"));
        			badge14.put(acc.get(i), 1);
        		}
        	}
        	if (acc.size() >= 9) {
        		for (int i = 0; i < acc.size(); i++) {
            		if (!badge15.containsKey(acc.get(i))){
                                
                		context.write(new Text(acc.get(i)), new Text("15"));
                		badge15.put(acc.get(i), 1);
                		}
            	}
        	}
        }
    }
 }
}
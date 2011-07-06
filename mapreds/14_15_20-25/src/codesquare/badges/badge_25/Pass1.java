package codesquare.badges.badge_25;      
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import codesquare.Toolbox;

/**
 * Class to find each Employee's LOC based on commit files
 * 
 * returned file format is {empId} #{LOC}
 * where "{" and "}" are not expressed
 * ie. 4825 #25 
 * 
 * accepts 2 directories - searches for all files recursively
 * 
 * @author deanna.surma
 * 
 */
public class Pass1 {
// receives commits
// returns empId #badge
	
// stores all empIds that have badge 25
private static HashMap<String, Integer> badge25 = new HashMap<String, Integer>();

public Pass1(String input1, String input2, String output) throws Exception {
	    Configuration conf = new Configuration();
	    Job job = new Job(conf, "LOC1");
	    job.setOutputKeyClass(Text.class);
	    job.setOutputValueClass(Text.class);
	    job.setMapperClass(Map.class);
	    job.setReducerClass(Reduce.class);
	    job.setInputFormatClass(TextInputFormat.class);
	    job.setOutputFormatClass(TextOutputFormat.class);
	    Toolbox.addDirectory(job, new File(input1));
	    Toolbox.addDirectory(job, new File(input2));
	    FileOutputFormat.setOutputPath(job, new Path(output));
	    job.waitForCompletion(true);
	 }

/**
 * 
 * @write key: ten second partition ({minute}-{10sec interval}-{directory name}   value: {minute} {second} {empId} 
 */
 public static class Map extends Mapper<LongWritable, Text, Text, Text> {
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
    	String line = value.toString();
    	String [] components = line.split("\\s+");
    	
    	Integer [] tenSecPartition = new Integer[2];
    	Integer [] tenSecPartition2 = new Integer[2];
    	tenSecPartition[0] = Integer.parseInt(components[5]);
    	tenSecPartition[1] = Integer.parseInt(components[6].substring(0,1)+"0");
    	tenSecPartition2[1] = tenSecPartition[1] - 10;
    	tenSecPartition2[0] = tenSecPartition[0];
    	
    	if (tenSecPartition2[1].equals(-10)) {
    		tenSecPartition2[0] = tenSecPartition2[0] - 1;
    		tenSecPartition2[1] = 50;
    	}
    	
    	String [] documents = components[8].substring(1,(components[8].length()-1)).split(",");
    	for (int i = 0; i < documents.length; i++) {
    		String[] filename = documents[i].split("/");
    		String directory = "";
    		if (!(new Integer(filename.length - 1)).equals(-1)) {
    			directory = filename[filename.length - 1]; }
    		context.write(new Text(tenSecPartition[0]+"-"+tenSecPartition[1]+"-"+directory), new Text(components[5]+" "+components[6]+" "+components[1]));
    		context.write(new Text(tenSecPartition2[0]+"-"+tenSecPartition2[1]+"-"+directory), new Text(components[5]+" "+components[6]+" "+components[1]));
    	}	// minute, second, empId
    }
 } 
    
/**
 * 
 * @write key: empId   value: 25
 */
 public static class Reduce extends Reducer<Text, Text, Text, Text> {
    public void reduce(Text key, Iterable<Text> values, Context context) 
      throws IOException, InterruptedException {
    	ArrayList<String[]> acc = new ArrayList<String[]>();
        for (Text val : values) {
        	String[] components = val.toString().split(" ");
        	for (int i = 0; i < acc.size(); i++) {
					if (!acc.get(i)[2].equals(components[2])) {
						if (Toolbox.subtractTime(acc.get(i), components) <= 10000) {
							if (!badge25.containsKey(acc.get(i)[2])) {
								context.write(new Text(acc.get(i)[2]), new Text("25"));
								badge25.put(acc.get(i)[2], 1);
								}
							if (!badge25.containsKey(components[2])) {
								context.write(new Text(components[2]), new Text("25"));
								badge25.put(components[2], 1);
								}
							}
					}
        	}
        	acc.add(components);
        }
    }
 }
}
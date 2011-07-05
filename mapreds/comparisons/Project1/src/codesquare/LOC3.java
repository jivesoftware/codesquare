package codesquare;       
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
        
/**
 * Class to award badges 21, 22, and 23
 * it takes input from LOC2
 * and writes empId badge#
 * 
 * returned file format is {empId} {badge#}
 * where "{" and "}" are not expressed
 * ie. 4825 24 
 * Here we test for badges 21, 22, and 23
 * 21 - peer badge - employee commits more LOC than all of his peers
 * 22 - employee badge - employee commits more LOC than his boss
 * 23 - boss badge - boss commits more LOC than all of his employees
 *  
 * accepts a directory - searches for all files recursively
 * 
 * @author deanna.surma
 * 
 */
public class LOC3 {
	// gets empId LOC BossId
	// gets BossId LOC
	// return empId badge#
	
	// stores each boss's LOC
	private static HashMap<Integer, Integer> bossLOC = new HashMap<Integer, Integer>();
	// stores each boss's maxEmpLOC
	private static HashMap<Integer, Integer> maxEmpLOC = new HashMap<Integer, Integer>();
	
public LOC3(String input, String output) throws Exception {
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
 * 
 * @write key: empId   value: {LOC} {bossId} AND {LOC}
 */
public static class Map extends Mapper<LongWritable, Text, Text, Text> {
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
    	
   	String line = value.toString();
   	String[] components = line.split("\\s+");
		
   	// if BossId LOC line, add to bossLOC and print bossId, LOC
   	if (new Integer(components.length).equals(2)){ 
   		context.write(new Text(components[0]), new Text(components[1])); // write BossId LOC
   		bossLOC.put(Integer.parseInt(components[0]), Integer.parseInt(components[1]));
   	}
   	else { // if empId LOC BossId, add (boss, LOC) to maxEmpLOC and write bossId, empId, LOC
   		if (new Integer(components[2]).equals(0)) { }
   		else {
   			if (maxEmpLOC.get(components[2]) == null || Integer.parseInt(components[0]) > maxEmpLOC.get(components[2])) {
   				maxEmpLOC.put(Integer.parseInt(components[2]), Integer.parseInt(components[1]));
   			}
   			context.write(new Text(components[2]), new Text(components[0]+" "+components[1]));
   		}
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
       for (Text val : values) {
    	   String[] components = val.toString().split("\\s+");
    	   // if key=bossId, val=LOC
    	   if (new Integer(components.length).equals(1)) { 
    		   // if emp is a boss
    		   if (maxEmpLOC.containsKey(Integer.parseInt(key.toString()))) { 
    			   // if bossLOC >= maxEmpLOC
    			   if (Integer.parseInt(components[0]) > maxEmpLOC.get(Integer.parseInt(key.toString()))) { 
    				   // award badge 21
    				   context.write(key, new Text("21"));
    				   }
    			   }
    		   }
    	   // else (ie if key=bossId, val=LOC, empId)
    	   else {
    		   // if LOC == maxempLOC
    		   if (Integer.parseInt(components[1]) == maxEmpLOC.get(Integer.parseInt(key.toString()))) { 
    			   // award badge 23
    			   context.write(new Text(components[0]), new Text("23"));}
    		   // if LOC > bossLOC
    		   if (Integer.parseInt(components[1]) > bossLOC.get(Integer.parseInt(key.toString()))) { 
    			   // award badge 22
    			   context.write(new Text(components[0]), new Text("22"));
    			   }
    		   }
       }
   }
}
}
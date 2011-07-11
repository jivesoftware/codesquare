package codesquare.badges.badge_14_15;      
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
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
	    conf.set("fs.default.name", "hdfs://10.45.111.143:8020");
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
	public HTable table;
	 
	public void setup(Context context) throws IOException,InterruptedException{
		
		Configuration config = HBaseConfiguration.create();
		config.set("hbase.cluster.distributed", "true");
        config.set("hbase.rootdir", "hdfs://hadoopdev008.eng.jiveland.com:54310/hbase");
        config.set("hbase.zookeeper.quorum","hadoopdev008.eng.jiveland.com,hadoopdev002.eng.jiveland.com,hadoopdev001.eng.jiveland.com");
        config.set("hbase.zookeeper.property.clientPort","2181");
        config.set("hbase.hregion.max.filesize", "1073741824");
        table = new HTable(config,"EmpBadges");
	}
	 
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
                    Toolbox.addBadges(acc.get(i), "14", table);
        			context.write(new Text(acc.get(i)), new Text("14"));
        			badge14.put(acc.get(i), 1);
        			context.setStatus("Reduced and inserted kv pair into hBase: " + acc.get(i).toString() + ":14");
        		}
        	}
        	if (acc.size() >= 9) {
        		for (int i = 0; i < acc.size(); i++) {
            		if (!badge15.containsKey(acc.get(i))){
                        Toolbox.addBadges(acc.get(i).toString(), "15", table);
                		context.write(new Text(acc.get(i)), new Text("15"));
                		badge15.put(acc.get(i), 1);
                		context.setStatus("Reduced and inserted kv pair into hBase: " + acc.get(i).toString() + ":15");
                		}
            	}
        	}
        }
    }
 }
}
package codesquare.badges.badge_20_24;        
import java.io.File;
import java.io.IOException;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import codesquare.Toolbox;
        
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
public class Pass2 {
	// gets empId LOC AND empId BossId
	// returns empId LOC BossId AND empId LOC
	private static int maxEmpLOC = 0;
	private static int maxEmp = 0;
	
/**
 * return employee with the most added LOC
 */
public int getMaxEmp() {
		return maxEmp;
	}
	
public Pass2(String input, String output) throws Exception {
		FileSystem dfs = codesquare.Toolbox.getHDFS();
	
	    Configuration conf = new Configuration();
	    conf.set("fs.default.name", "hdfs://10.45.111.143:8020");
	    Job job = new Job(conf, "LOC1");
	    job.setOutputKeyClass(Text.class);
	    job.setOutputValueClass(Text.class);
	    job.setMapperClass(Map.class);
	    job.setReducerClass(Reduce.class);
	    job.setInputFormatClass(TextInputFormat.class);
	    job.setOutputFormatClass(NullOutputFormat.class);
	    Toolbox.addDirectory(job, dfs,new Path(input));
	    //FileOutputFormat.setOutputPath(job, new Path(output));
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
        for (Text val : values) {
        	int LOC = Integer.parseInt(val.toString().substring(1));
        	if (LOC < 0) {
                Toolbox.addBadges(key.toString(), "20", table);
        		context.write(key, new Text("20"));
        		context.setStatus("Reduced and inserted kv pair into hBase: " + key.toString() + ":20");
        	}
        	if (LOC > maxEmpLOC) {
        		maxEmpLOC = LOC;
        		maxEmp = Integer.parseInt(key.toString());
        	}
        }
    }
 }
}
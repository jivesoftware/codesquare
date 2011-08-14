package codesquare.badges.badge_20_24;

import java.io.IOException;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;

import codesquare.Toolbox;

/**
 * Class to award badges 20 and to store badge 24 it takes input from LOC1 and
 * writes empId badge#
 * 
 * returned file format is {empId} {badge#} where "{" and "}" are not expressed
 * ie. 4825 24
 * 
 * Here we test for badges 20 and 24 20 - negative LOC badge - employee commits
 * more LOC than his boss 24 - largest LOC badge - employee commits more LOC
 * than all of his peers
 * 
 * accepts a directory - searches for all files recursively
 * 
 * @author deanna.surma
 * 
 */
public class Pass2 {
	
	public Pass2(String input, Configuration config,FileSystem hdfs) throws Exception {
		Job job = new Job(config);
		job.setJarByClass(codesquare.badges.badge_20_24.Pass2.class);
		job.setJobName("Badge_20_24 - Pass2");
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(NullOutputFormat.class);
		Toolbox.addDirectory(job, hdfs, new Path(input));
		job.waitForCompletion(true);
	}

	/**
	 * trivial
	 * 
	 * @write key: empId value: {LOC}
	 */
	public static class Map extends Mapper<LongWritable, Text, Text, Text> {
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String line = value.toString();
			String[] components = line.split("\\s+");
			context.write(new Text(components[0]), new Text(components[1].substring(1)));
			System.out.println("Pass2 MAP KEY: "+(components[0]));
			System.out.println("Pass2 MAP VAL: "+components[1].substring(1));
		}
	}

	/**
	 *  NOTE - must only have one reducer!!!
	 * @write key: empId value: 20
	 */
	public static class Reduce extends Reducer<Text, Text, Text, Text> {
		public HTable table;
		
		// gets empId LOC AND empId BossId
		// returns empId LOC BossId AND empId LOC
		private int maxEmpLOC = 0;
		private String maxEmp = "";

		public void setup(Context context) throws IOException, InterruptedException {
			table = new HTable(Toolbox.getHBaseConfiguration(), "EmpBadges");
		}
                
        public void reduce(Text key, Iterable<Text> values, Context context)
        	throws IOException, InterruptedException {
            	System.out.println("REDKEY: "+key.toString());
                int totLOC = 0;
                for (Text val : values) {
                	System.out.println("REDVALUE: "+val.toString());
                    totLOC = totLOC + Integer.parseInt(val.toString());
                }
                if (totLOC < 0) {
                	Toolbox.addBadges(key.toString(), "20", table);
                	context.write(key, new Text("20"));
                	System.out.println("Pass2 RED KEY: "+(key));
                	System.out.println("Pass2 RED VAL: "+"20");
                }
                if (totLOC > maxEmpLOC) {
                	maxEmpLOC = totLOC;
                    maxEmp = key.toString();
                }  
        }
        
        public void cleanup(Context context) {
    		Toolbox.addBadges(maxEmp, "24", table);
    		System.out.println("BADGE24 KEY: "+(maxEmp));
    		System.out.println("BADGE24 VAL: "+"24");
        }           
	}
}
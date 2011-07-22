package codesquare.badges.badge_21_22_23;

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
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import codesquare.Toolbox;

/**
 * Class to take input from LOC1 and BossList and write empId LOC bossId AND
 * empId LOC to prepare for LOC3
 * 
 * returned file format is {empId} {LOC} {bossId} AND {empId} {LOC} where "{"
 * and "}" are not expressed ie. 4825 29 4824 AND 4825 29
 * 
 * accepts a directory - searches for all files recursively
 * 
 * @author deanna.surma
 * 
 */
public class Pass2 {
	// TODO: get bossList from HBase
	// gets empId #LOC AND empId BossId
	// returns empId LOC BossId AND empId LOC

	public Pass2(String input, String output, Configuration config,FileSystem hdfs) throws Exception {
		
		Job job = new Job(config);
		job.setJarByClass(codesquare.badges.badge_21_22_23.Pass2.class);
		job.setJobName("");
		//job.setNumReduceTasks((int) (job.getNumReduceTasks()*.95));
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		Toolbox.addDirectory(job, hdfs, new Path(input));
		FileInputFormat.addInputPath(job, new Path("bossList.txt"));
		FileOutputFormat.setOutputPath(job, new Path(output));
		job.waitForCompletion(true);
	}

	/**
	 * 
	 * @write key: empId value: #LOC or bossId
	 */
	public static class Map extends Mapper<LongWritable, Text, Text, Text> {
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String line = value.toString();
			String[] components = line.split("\\s+");
			context.write(new Text(components[0]), new Text(components[1]));
		}
	}

	/**
	 * 
	 * @write key: empId value: {LOC} {bossId} AND {LOC}
	 */
	public static class Reduce extends Reducer<Text, Text, Text, Text> {

		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			String info = " ";
			String LOC = "";
			for (Text val : values) {
				if (val.toString().substring(0, 1).equals("#")) {
					info = val.toString().substring(1).concat(info);
					LOC = val.toString().substring(1);
				} else {
					info = info.concat(val.toString());
				}
			}
			context.write(key, new Text(info));
			context.write(key, new Text(LOC));
		}
	}
}
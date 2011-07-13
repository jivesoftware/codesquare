package codesquare.badges.sharedpasses;

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.fs.FileSystem;
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
 * returned file format is {empId} #{LOC} where "{" and "}" are not expressed
 * ie. 4825 #29
 * 
 * accepts a directory - searches for all files recursively
 * 
 * @author deanna.surma
 * 
 */
public class LOC {
	// receives commits
	// returns empId #LOC

	public LOC(String input, String output) throws Exception {
		Configuration conf = new Configuration();
		conf.set("fs.default.name",
				"hdfs://hadoopdev001.eng.jiveland.com:54310");

		conf.set("fs.default.name",
				"hdfs://hadoopdev001.eng.jiveland.com:54310");

		conf.set("fs.default.name",
				"hdfs://hadoopdev001.eng.jiveland.com:54310");
		conf.set("hadoop.log.dir", "/hadoop001/data/hadoop/logs");
		conf.set("hadoop.tmp.dir", "/hadoop001/tmp");
		conf.set("io.file.buffer.size", "131072");
		conf.set("fs.inmemory.size.mb", "200");
		conf.set("fs.checkpoint.period", "900");

		conf.set("dfs.datanode.max.xceivers", "4096");
		conf.set("dfs.block.size", "134217728");
		conf.set(
				"dfs.name.dir",
				"/hadoop001/data/datanode,/hadoop002/data/datanode,/hadoop003/data/datanode,/hadoop004/data/datanode,/hadoop005/data/datanode,/hadoop006/data/datanode,/hadoop007/data/datanode,/hadoop008/data/datanode,/hadoop009/data/datanode,/hadoop010/data/datanode,/hadoop011/data/datanode,/hadoop012/data/datanode");
		conf.set("dfs.umaskmode", "007");
		conf.set("dfs.datanode.du.reserved", "107374182400");
		conf.set("dfs.datanode.du.pct", "0.85f");

		FileSystem dfs = codesquare.Toolbox.getHDFS();

		Job job = new Job(conf);
		job.setJarByClass(codesquare.badges.sharedpasses.LOC.class);
		job.setJobName("LOC");

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		Toolbox.addDirectory(job, dfs, new Path(input));
		FileOutputFormat.setOutputPath(job, new Path(output));
		job.waitForCompletion(true);
	}

	/**
	 * 
	 * @write key: empId value: LOC (insertions - deletions)
	 */
	public static class Map extends
			Mapper<LongWritable, Text, Text, IntWritable> {
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String line = value.toString();
			String[] components = line.split("\\s+");
			IntWritable one = new IntWritable(Integer.parseInt(components[9])
					- Integer.parseInt(components[10]));
			context.write(new Text(components[1]), one);
		}
	}

	/**
	 * 
	 * @write key: empId value: total LOC
	 */
	public static class Reduce extends Reducer<Text, IntWritable, Text, Text> {
		public void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			context.write(key, new Text("#" + sum));
		}
	}
}
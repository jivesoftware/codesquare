package codesquare.badges.badge_25;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import codesquare.Toolbox;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;

/**
 * Class to find each Employee's LOC based on commit files
 * 
 * returned file format is {empId} #{LOC} where "{" and "}" are not expressed
 * ie. 4825 #25
 * 
 * accepts 2 directories - searches for all files recursively
 * 
 * @author deanna.surma
 * 
 */
public class Pass1 {
	private static HTable table;
	// receives commits
	// returns empId #badge

	// stores all empIds that have badge 25
	private static HashMap<String, Integer> badge25 = new HashMap<String, Integer>();

	public Pass1(String input1, String input2, Configuration config,FileSystem hdfs) throws Exception {
		
		Job job = new Job(config);

		job.setJarByClass(Pass1.class);
		job.setJobName("Badge25");
		job.setNumReduceTasks((int) (job.getNumReduceTasks()*Toolbox.reduceTaskConstant));
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(NullOutputFormat.class);
		Toolbox.addDirectory(job, hdfs, new Path(input1));
		Toolbox.addDirectory(job, hdfs, new Path(input2));
		// FileOutputFormat.setOutputPath(job, new Path(output));
		job.waitForCompletion(true);
	}

	/**
	 * 
	 * @write key: ten second partition ({minute}-{10sec interval}-{directory
	 *        name} value: {minute} {second} {empId}
	 */
	public static class Map extends Mapper<LongWritable, Text, Text, Text> {
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String line = value.toString();
			String[] components = line.split("\\s+");

			Integer[] tenSecPartition = new Integer[2];
			Integer[] tenSecPartition2 = new Integer[2];
			tenSecPartition[0] = Integer.parseInt(components[5]);
			tenSecPartition[1] = Integer.parseInt(components[6].substring(0, 1)
					+ "0");
			tenSecPartition2[1] = tenSecPartition[1] - 10;
			tenSecPartition2[0] = tenSecPartition[0];

			if (tenSecPartition2[1].equals(-10)) {
				tenSecPartition2[0] = tenSecPartition2[0] - 1;
				tenSecPartition2[1] = 50;
			}

			String[] documents = components[8].substring(1,
					(components[8].length() - 1)).split(",");
			for (int i = 0; i < documents.length; i++) {
				String[] filename = documents[i].split("/");
				String directory = "";
				if (!(new Integer(filename.length - 1)).equals(-1)) {
					directory = filename[filename.length - 1];
				}
				context.write(new Text(tenSecPartition[0] + "-"
						+ tenSecPartition[1] + "-" + directory), new Text(
						components[5] + " " + components[6] + " "
								+ components[1]));
				context.write(new Text(tenSecPartition2[0] + "-"
						+ tenSecPartition2[1] + "-" + directory), new Text(
						components[5] + " " + components[6] + " "
								+ components[1]));
			} // minute, second, empId
		}
	}

	/**
	 * 
	 * @write key: empId value: 25
	 */
	public static class Reduce extends Reducer<Text, Text, Text, Text> {
		public HTable table;

		public void setup(Context context) throws IOException,
				InterruptedException {

			Configuration config = HBaseConfiguration.create();
			config.set("hbase.cluster.distributed", "true");
			config.set("hbase.rootdir",
					"hdfs://hadoopdev008.eng.jiveland.com:54310/hbase");
			config.set(
					"hbase.zookeeper.quorum",
					"hadoopdev008.eng.jiveland.com,hadoopdev002.eng.jiveland.com,hadoopdev001.eng.jiveland.com");
			config.set("hbase.zookeeper.property.clientPort", "2181");
			config.set("hbase.hregion.max.filesize", "1073741824");
			table = new HTable(config, "EmpBadges");
		}

		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			ArrayList<String[]> acc = new ArrayList<String[]>();
			for (Text val : values) {
				String[] components = val.toString().split(" ");
				for (int i = 0; i < acc.size(); i++) {
					if (!acc.get(i)[2].equals(components[2])) {
						if (Toolbox.subtractTime(acc.get(i), components) <= 10000) {
							if (!badge25.containsKey(acc.get(i)[2])) {
								Toolbox.addBadges(acc.get(i)[2], "25", table);
								context.write(new Text(acc.get(i)[2]),
										new Text("25"));
								badge25.put(acc.get(i)[2], 1);
								context.setStatus("Reduced and inserted kv pair into hBase: "
										+ acc.get(i)[2] + ":15");
							}
							if (!badge25.containsKey(components[2])) {
								Toolbox.addBadges(components[2], "25", table);
								context.write(new Text(components[2]),
										new Text("25"));
								badge25.put(components[2], 1);
								context.setStatus("Reduced and inserted kv pair into hBase: "
										+ components[2] + ":25");
							}
						}
					}
				}
				acc.add(components);
			}
		}
	}

}
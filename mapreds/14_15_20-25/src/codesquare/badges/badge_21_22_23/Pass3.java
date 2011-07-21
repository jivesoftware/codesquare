package codesquare.badges.badge_21_22_23;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

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
 * Class to award badges 21, 22, and 23 it takes input from LOC2 and writes
 * empId badge#
 * 
 * returned file format is {empId} {badge#} where "{" and "}" are not expressed
 * ie. 4825 24 Here we test for badges 21, 22, and 23 21 - peer badge - employee
 * commits more LOC than all of his peers 22 - employee badge - employee commits
 * more LOC than his boss 23 - boss badge - boss commits more LOC than all of
 * his employees
 * 
 * accepts a directory - searches for all files recursively
 * 
 * @author deanna.surma
 * 
 */
public class Pass3 {
	// gets empId LOC BossId
	// gets BossId LOC
	// return empId badge#

	// stores each boss's LOC
	private static HashMap<Integer, Integer> bossLOC = new HashMap<Integer, Integer>();
	// stores each boss's maxEmpLOC
	private static HashMap<Integer, Integer> maxEmpLOC = new HashMap<Integer, Integer>();

	public Pass3(String input, Configuration config,FileSystem hdfs) throws Exception {
		
		Job job = new Job(config);

		job.setJarByClass(codesquare.badges.badge_21_22_23.Pass3.class);
		job.setJobName("Badge_21_22_23");
		job.setNumReduceTasks((int) (job.getNumReduceTasks()*Toolbox.reduceTaskConstant));
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(NullOutputFormat.class);
		Toolbox.addDirectory(job, hdfs, new Path(input));
		// FileOutputFormat.setOutputPath(job, new Path(output));
		job.waitForCompletion(true);
	}

	/**
	 * 
	 * @write key: empId value: {LOC} {bossId} AND {LOC}
	 */
	public static class Map extends Mapper<LongWritable, Text, Text, Text> {
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {

			String line = value.toString();
			String[] components = line.split("\\s+");

			// if BossId LOC line, add to bossLOC and print bossId, LOC
			if (new Integer(components.length).equals(2)) {
				context.write(new Text(components[0]), new Text(components[1])); // write
																					// BossId
																					// LOC
				bossLOC.put(Integer.parseInt(components[0]),
						Integer.parseInt(components[1]));
			} else { // if empId LOC BossId, add (boss, LOC) to maxEmpLOC and
						// write bossId, empId, LOC
				if (components[2].equals("noboss@nomail.com")) {

				} else {
					if (maxEmpLOC.get(components[2]) == null
							|| Integer.parseInt(components[0]) > maxEmpLOC
									.get(components[2])) {
						maxEmpLOC.put(Integer.parseInt(components[2]),
								Integer.parseInt(components[1]));
					}
					context.write(new Text(components[2]), new Text(
							components[0] + " " + components[1]));
				}
			}
		}
	}

	/**
	 * 
	 * @write key: empId value: badge#
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
			for (Text val : values) {
				String[] components = val.toString().split("\\s+");
				// if key=bossId, val=LOC
				if (new Integer(components.length).equals(1)) {
					// if emp is a boss
					if (maxEmpLOC.containsKey(Integer.parseInt(key.toString()))) {
						// if bossLOC >= maxEmpLOC
						if (Integer.parseInt(components[0]) > maxEmpLOC
								.get(Integer.parseInt(key.toString()))) {
							// award badge 21
							Toolbox.addBadges(key.toString(), "21", table);
							context.write(key, new Text("21"));
							context.setStatus("Reduced and inserted kv pair into hBase: "
									+ key.toString() + ":21");
						}
					}
				}
				// else (ie if key=bossId, val=LOC, empId)
				else {
					// if LOC == maxempLOC
					if (Integer.parseInt(components[1]) == maxEmpLOC
							.get(Integer.parseInt(key.toString()))) {
						// award badge 23
						Toolbox.addBadges(components[0], "23", table);
						context.write(new Text(components[0]), new Text("23"));
					}
					context.setStatus("Reduced and inserted kv pair into hBase: "
							+ components[0] + ":23");
					// if LOC > bossLOC
					if (Integer.parseInt(components[1]) > bossLOC.get(Integer
							.parseInt(key.toString()))) {
						// award badge 22
						Toolbox.addBadges(components[0], "22", table);
						context.write(new Text(components[0]), new Text("22"));
						context.setStatus("Reduced and inserted kv pair into hBase: "
								+ components[0] + ":22");
					}
				}
			}
		}
	}
}
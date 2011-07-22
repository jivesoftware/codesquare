package codesquare.badges.badge_14_15;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;

import codesquare.Toolbox;

/**
 * Class to find each Employee's LOC based on commit files
 * 
 * returned file format is {empId} #{LOC} where "{" and "}" are not expressed
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
	private static HashMap<String, Integer> badge14 = new HashMap<String, Integer>(); //where in the code is this populated
	// stores all empIds that have badge 15
	private static HashMap<String, Integer> badge15 = new HashMap<String, Integer>(); //where in the code is this populated

	public Pass1(String input, Configuration config,FileSystem hdfs) throws Exception {

		Job job = new Job(config);

		job.setJarByClass(codesquare.badges.badge_14_15.Pass1.class);
		job.setJobName("Badge_14_15");
<<<<<<< HEAD
<<<<<<< HEAD
		ClusterMetrics cm = new ClusterMetrics();
		cm.getReduceSlotCapacity();
		//job.setNumReduceTasks((int) (job.*.95));
=======
		//job.setNumReduceTasks(1);
>>>>>>> parent of 476889d... Set the number of reducers for the mapreduces
=======
		//job.setNumReduceTasks(1);
>>>>>>> parent of 476889d... Set the number of reducers for the mapreduces
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
	 * @write key: document name value: empId
	 */
	public static class Map extends Mapper<LongWritable, Text, Text, Text> {
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String line = value.toString();
			String[] components = line.split("\\s+");
			String[] documents = components[8].substring(1,
					(components[8].length() - 1)).split(",");
			for (int i = 0; i < documents.length; i++) {
				context.write(new Text(documents[i]), new Text(components[1]));
				//puts
				
				//
				System.out.println(documents[i] + " " + components[1]);
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
			System.out.println("REDUCE");
			HashMap<String, String> acc = new HashMap<String, String>();
			
			for (Text val : values) {
				acc.put(val.toString(), "1");
			}
			//debug code
			
			
			//end debug code
			if (acc.size() >= 2) {
				Iterator it = acc.keySet().iterator();
				while (it.hasNext()) {
				    String x = (String) it.next();
				    String y = acc.get(x);
				    //do stuff here
					if (!badge14.containsKey(x)) {
						Toolbox.addBadges(x, "14", table);
						context.write(new Text(x), new Text("14")); //Text is being given null, btw, where is badge14 or badge15 ever populated?
						badge14.put(x, 1);
						context.setStatus("Reduced and inserted kv pair into hBase: "
								+ x + ":14");
					}
				}
				if (acc.size() >= 9) {
					Iterator it2 = acc.keySet().iterator();
					while (it2.hasNext()) {
						String x2 = (String) it2.next();
						String y2 = acc.get(x2);
						if (!badge15.containsKey(x2)) {
							Toolbox.addBadges(x2, "15", table);
							context.write(new Text(x2), new Text("15"));
							badge15.put(x2, 1);
							context.setStatus("Reduced and inserted kv pair into hBase: "
									+ x2 + ":15");
						}
					}
				}
			}
		}
	}
}
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
import org.apache.hadoop.mapreduce.Reducer.Context;
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
 */
public class Pass1 {
	// receives commits
	// returns empId #LOC

	// stores all empIds that have badge 14
	private static HashMap<String, Integer> badge14 = new HashMap<String, Integer>(); //where in the code is this populated
	// stores all empIds that have badge 15
	private static HashMap<String, Integer> badge15 = new HashMap<String, Integer>(); //where in the code is this populated

	public Pass1(String input, Configuration config, FileSystem hdfs) throws Exception {

		Job job = new Job(config);

		job.setJarByClass(codesquare.badges.badge_14_15.Pass1.class);
		job.setJobName("Badge_14_15 - Pass 1");

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
	 * 
	 * @write key: document name value: empId
	 */
		public static class Map extends Mapper<LongWritable, Text, Text, Text> {
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String line = value.toString();
			String[] components = line.split("\\s+", 9);
			String[] documents = components[5].substring(1,
					(components[5].length() - 1)).split(",");
			for (int i = 0; i < documents.length; i++) {
				context.write(new Text(documents[i]), new Text(components[1]));
				System.out.println("Pass1 MAP KEY: "+(documents[i]));
				System.out.println("Pass1 MAP VAL: "+components[1]);
			}
		}
	}

	/**
	 * 
	 * @write key: empId value: badge#
	 */
	public static class Reduce extends Reducer<Text, Text, Text, Text> {
		public HTable table;

		public void setup(Context context) throws IOException, InterruptedException {
			table = new HTable(Toolbox.getHBaseConfiguration(), "EmpBadges");
		}

		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			HashMap<String, String> acc = new HashMap<String, String>();
			
			for (Text val : values) {
				acc.put(val.toString(), "1");
			}
			if (acc.size() >= 2) {
				Iterator it = acc.keySet().iterator();
				while (it.hasNext()) {
				    String x = (String) it.next();
				    String y = acc.get(x);
				    //do stuff here
					if (!badge14.containsKey(x)) {
						Toolbox.addBadges(x, "14", table);
						System.out.println("Pass1 RED KEY: "+(x));
						System.out.println("Pass1 RED VAL: "+"14");
						badge14.put(x, 1);
					}
				}
				if (acc.size() >= 9) {
					Iterator it2 = acc.keySet().iterator();
					while (it2.hasNext()) {
						String x2 = (String) it2.next();
						String y2 = acc.get(x2);
						if (!badge15.containsKey(x2)) {
							Toolbox.addBadges(x2, "15", table);
							System.out.println("Pass1 RED KEY: "+(x2));
							System.out.println("Pass1 RED VAL: "+"15");
							badge15.put(x2, 1);
							}
					}
				}
			}
		}
	}
}
package codesquare.badges.badge_25;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Date;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;

import codesquare.Toolbox;

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
	private static int prevMinute;
	private static int currMinute;

	public Pass1(String input1, String input2, Configuration config,FileSystem hdfs, int prevMinute, int currMinute) throws Exception {
		this.prevMinute = prevMinute;
		this.currMinute = currMinute;
		
		Job job = new Job(config);

		job.setJarByClass(Pass1.class);
		job.setJobName("Badge25");

		//job.setNumReduceTasks((int) (job.getNumReduceTasks()*.95));

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
		job.setNumReduceTasks(1);
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(NullOutputFormat.class);
		Toolbox.addDirectory(job, hdfs, new Path(input1));
		Toolbox.addDirectory(job, hdfs, new Path(input2));
		// FileOutputFormat.setOutputPath(job, new Path(output));
		try{
			job.waitForCompletion(true);
		}catch(IOException e){
			System.out.println("No Input Paths to run this MapReduce on!");
		}
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
			String[] components = line.split("\\s+", 9);
			Calendar time = Calendar.getInstance();
			time.setTime(new Date(Long.parseLong(components[2])));
			if (time.get(Calendar.MINUTE)==prevMinute && time.get(Calendar.SECOND)<50){} // don't map it
			else { // map it
				String second = Integer.toString(time.get(Calendar.SECOND));
				String minute = Integer.toString(time.get(Calendar.MINUTE));
				String secondTimeFrame = Integer.toString(time.get(Calendar.SECOND)).substring(0,1)+"0";
				time.add(Calendar.SECOND, -10);
				String minute2 = Integer.toString(time.get(Calendar.MINUTE));
				String secondTimeFrame2 = Integer.toString(time.get(Calendar.SECOND)).substring(0,1)+"0";
				
				String[] documents = components[5].substring(1,
						(components[5].length() - 1)).split(",");
				for (int i = 0; i < documents.length; i++) {
					String[] filename = documents[i].split("/");
					String directory = "";
					if (!(new Integer(filename.length - 1)).equals(-1)) {
						directory = filename[filename.length - 1];
					}
					context.write(
						new Text(minute+"-"+secondTimeFrame+"-"+directory),
						new Text(minute+" "+second+" "+components[1]));
					System.out.println("Pass1 MAP KEY: "+(minute+"-"+secondTimeFrame+"-"+directory));
					System.out.println("Pass1 MAP VAL: "+minute+" "+second+" "+components[1]);
					context.write(
						new Text(minute2+"-"+secondTimeFrame2+"-"+directory),
						new Text(minute+" "+second+" "+components[1]));
					System.out.println("Pass1 MAP KEY: "+(minute2+"-"+secondTimeFrame2+"-"+directory));
					System.out.println("Pass1 MAP VAL: "+minute+" "+second+" "+components[1]);
				}
			}
		}
	}

	/**
	 * 
	 * @write key: empId value: 25
	 */
	public static class Reduce extends Reducer<Text, Text, Text, Text> {
		public HTable table;

		public void setup(Context context) throws IOException, InterruptedException {
			table = new HTable(Toolbox.getHBaseConfiguration(), "EmpBadges");
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
								System.out.println("Pass1 RED KEY: "+(acc.get(i)[2]));
								System.out.println("Pass1 RED VAL: "+"25");
							}
							if (!badge25.containsKey(components[2])) {
								Toolbox.addBadges(components[2], "25", table);
								context.write(new Text(components[2]),
										new Text("25"));
								badge25.put(components[2], 1);
								System.out.println("Pass1 RED KEY: "+components[2]);
								System.out.println("Pass1 RED VAL: "+"25");
							}
						}
					}
				}
				acc.add(components);
			}
		}
	}

}
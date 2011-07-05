package codesquare;      
import java.io.File;
import java.io.IOException;
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
 * returned file format is {empId} #{LOC}
 * where "{" and "}" are not expressed
 * ie. 4825 #29 
 * 
 * accepts a directory - searches for all files recursively
 * 
 * @author deanna.surma
 * 
 */
public class LOC1 {
// receives commits
// returns empId #LOC

public LOC1(String input, String output) throws Exception {
	    Configuration conf = new Configuration();
	    Job job = new Job(conf, "LOC1");
	    job.setOutputKeyClass(Text.class);
	    job.setOutputValueClass(IntWritable.class);
	    job.setMapperClass(Map.class);
	    job.setReducerClass(Reduce.class);
	    job.setInputFormatClass(TextInputFormat.class);
	    job.setOutputFormatClass(TextOutputFormat.class);
	    Toolbox.addDirectory(job, new File(input));
	    FileOutputFormat.setOutputPath(job, new Path(output));
	    job.waitForCompletion(true);
	 }

/**
 * 
 * @write key: empId   value: LOC (insertions - deletions) 
 */
 public static class Map extends Mapper<LongWritable, Text, Text, IntWritable> {
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
    	String line = value.toString();
    	String [] components = line.split("\\s+");
        IntWritable one = new IntWritable(Integer.parseInt(components[9]) - Integer.parseInt(components[10]));
        context.write(new Text(components[1]), one);
    }
 } 
    
/**
 * 
 * @write key: empId   value: total LOC
 */
 public static class Reduce extends Reducer<Text, IntWritable, Text, Text> {
    public void reduce(Text key, Iterable<IntWritable> values, Context context) 
      throws IOException, InterruptedException {
        int sum = 0;
        for (IntWritable val : values) {
            sum += val.get();   
        }
        context.write(key, new Text("#"+sum));
    }
 }
}
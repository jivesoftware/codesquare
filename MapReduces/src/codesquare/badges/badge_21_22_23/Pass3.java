package codesquare.badges.badge_21_22_23;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;

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
 * @author diivanand.ramalingam (fixed bugs in this program)
 */
public class Pass3 {
    // gets empId LOC BossId
    // gets BossId LOC
    // return empId badge#

    public Pass3(String input, Configuration config,FileSystem hdfs) throws Exception {
    	Job job = new Job(config);
    	job.setJarByClass(codesquare.badges.badge_21_22_23.Pass3.class);
    	job.setJobName("Badge_21_23_23 - Pass3");
    	job.setOutputKeyClass(Text.class);
    	job.setOutputValueClass(Text.class);
    	job.setMapperClass(Map.class);
    	job.setReducerClass(Reduce.class);
    	job.setNumReduceTasks(1);
    	job.setInputFormatClass(TextInputFormat.class);
    	job.setOutputFormatClass(NullOutputFormat.class);
    	Toolbox.addDirectory(job, hdfs, new Path(input));
    	try{
			job.waitForCompletion(true);
		}catch(IOException e){
			System.out.println("No Input Paths to run this MapReduce on!");
		}
    }

    /**
     * 
     * @write key: empId value: {LOC} {bossId} AND {LOC}
     */
    // gets empId LOC BossId
    // gets BossId LOC 
    public static class Map extends Mapper<LongWritable, Text, Text, Text> {
    	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        	String line = value.toString();
        	String[] components = line.split("\\s+");
    		if(new Integer(components.length).equals(2)){
            	System.out.println("PASS3 MAP KEY"+components[0]);
        		System.out.println("PASS3 RED VAL"+components[1]);
    			context.write(new Text(components[0]), new Text(components[1]));
    		}
    		else {
            	System.out.println("PASS3 MAP KEY"+components[2]);
        		System.out.println("PASS3 RED VAL"+components[1]+" "+components[0]);
    			if (components[2].equals("noboss@nomail.com")) { }
    			else {
    			context.write(new Text(components[2]), new Text(components[1]+" "+components[0]));
    			}
    		}
    	}
    } // deal w/noboss

    /**
     * 
     * @write key: empId value: badge#
     */
    public static class Reduce extends Reducer<Text, Text, Text, Text> {
	public HTable table;

	// stores each boss's LOC
	private HashMap<String, Integer> bossLOC = new HashMap<String, Integer>();
	// stores each boss's maxEmp
	private HashMap<String, Employee> maxEmp = new HashMap<String, Employee>();
	// stores each boss's employees
	private HashMap<String, ArrayList<Employee>> allEmps = new HashMap<String, ArrayList<Employee>>();
	
	public void setup(Context context) throws IOException, InterruptedException {
		table = new HTable(Toolbox.getHBaseConfiguration(), "EmpBadges");
	}
	
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		for (Text val : values) {
       		String[] components = val.toString().split("\\s+");
       		System.out.println("REDUCE");
       		
       		if (new Integer(components.length).equals(1)){
       			bossLOC.put(key.toString(), Integer.parseInt(components[0])); // add to bossLOC
       		}
       		else { // add to maxEmp iff && add to allEmp
       			if (allEmps.containsKey(key.toString())){
       				ArrayList<Employee> x = allEmps.get(key.toString());
       				x.add(new Employee(components[1], Integer.parseInt(components[0])));
       				allEmps.put(key.toString(), x);
       			}
       			else {
       				ArrayList<Employee> x = new ArrayList<Employee>();
       				x.add(new Employee(components[1], Integer.parseInt(components[0])));
       				allEmps.put(key.toString(), x);
       			}
       			if (maxEmp.get(key.toString()) == null
       				|| Integer.parseInt(components[0]) > maxEmp.get(key.toString()).getLOC()) {
        			maxEmp.put(key.toString(), new Employee(components[1], Integer.parseInt(components[0])));
                }
       		}
		}
       		
		// iterators to print out all hashmaps are provided at bottom 
		// good for debugging
    }
    
	public void cleanup(Context context) {
		try{
			System.out.println("CLEAN");
			// iterate through maxEmp array
			System.out.println("bossLoc: " + bossLOC);
			System.out.println("maxEmp: " + maxEmp);
			System.out.println("All Emps: " + allEmps);
			for (Entry<String, Employee> entry : maxEmp.entrySet()) {
				String key = entry.getKey();
			    Employee value = entry.getValue();
			    
			    // give badge 22 to all val ppl
			    Toolbox.addBadges(value.getId(), "23", table);
				System.out.println("PASS3 RED"+value.getId()+" "+"23");
				
				// give badge 23 if boss has more LOC than that person
				System.out.println("value.getLOC(): " + value.getLOC());
				if(!(bossLOC.get(key) == null)){
					System.out.println("bossLOC.get(key): " + bossLOC.get(key));
					if (value.getLOC() < bossLOC.get(key)) {
						Toolbox.addBadges(key.toString(), "21", table);
						System.out.println("PASS3 RED"+key+" "+"21");
					}
				}
				
			}
			
			// iterate through allEmps and if their LOC > boss', give badge 21
			for (Entry<String, ArrayList<Employee>> entry : allEmps.entrySet()) { //This loop before was identical to the one above and it shouldn't be since it's a different badge, so I fixed this bug
				String key = entry.getKey();
			    ArrayList<Employee> value = entry.getValue();
			    System.out.println("IN LAST FOR: value: " + value);
				
				
				if(!(bossLOC.get(key) == null)){
					System.out.println("IN LAST FOR: bossLOC.get(key): " + bossLOC.get(key));
					for(Employee e : value){
						System.out.println("IN LAST FOR Emp Array: " + e);
						if(e.getLOC() > bossLOC.get(key)){
							System.out.println(e.getId());
							Toolbox.addBadges(e.getId(), "22", table);
							System.out.println("PASS3 RED"+e.getId()+" "+"22");
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
    }
}


//// iterate through maxEmp array
//   Iterator<String> iterator = bossLOC.keySet().iterator(); 
//   System.out.println("BOSSLOC HASHMAP");  
//   while (iterator.hasNext()) {  
//      String k = iterator.next();  
//   Integer value = bossLOC.get(k);  
//      System.out.println(k + " " + value);  
//   } 
//   
//   Iterator<String> iterator2 = maxEmp.keySet().iterator(); 
//   System.out.println("MAXEMP HASHMAP");  
//   while (iterator2.hasNext()) {  
//      String k = iterator2.next();  
//      Employee value = maxEmp.get(k);  
//      System.out.println(k + " " + value.toString());  
//   } 
//   
//   Iterator<String> iterator3 = allEmps.keySet().iterator(); 
//   System.out.println("ALLEMPS HASHMAP");  
//   while (iterator3.hasNext()) {  
//      String k = iterator3.next();  
//   ArrayList<Employee> value = allEmps.get(k);  
//      System.out.println(k + " " + value.toString().toString());  
//   } 
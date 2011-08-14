/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package codesquare.badges.badge_20_24;

import java.util.Collections;
import java.util.ArrayList;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author deanna.surma
 */
public class Pass2_bTest {
    
    public Pass2_bTest() {
    }

    /**
     * Test of map method, of class Pass2_b.
     */
    @Test
    public void testMap() throws Exception {
        System.out.println("map");
        LongWritable key = null;
        Text value = new Text("1@gmail.com 25");
        Pass2_b instance = new Pass2_b(50);
        instance.map(key, value);
        
        ArrayList<String> results = new ArrayList<String>();
        results.add("1@gmail.com 25"); 
        
        assertEquals(instance.getMapResults(),results);
    }

     /**
     * Test of reduce method, of class Pass2_b.
     */
    @Test
    public void testReduce() throws Exception {
        System.out.println("reduce");
  
        // reduce 1
        Text key = (new Text("1@gmail.com"));
        ArrayList vals = new ArrayList<Text>();
        vals.add(new Text("50"));
        vals.add(new Text("50"));
        Iterable<Text> values = vals;
        
        Pass2_b instance = new Pass2_b(50);
        instance.reduce(key, values);
        
        // desired output
        ArrayList<Text> results = new ArrayList<Text>();
        results.add(new Text(""));
        Iterable<Text> res = results;
        
        // actual output
        ArrayList redResults = instance.getReduce20Results();
        Collections.sort(redResults);
        
        assertEquals(redResults.toString(),res.toString()); // should be empty
        
        System.out.println(instance.getMaxEmp());
        System.out.println(instance.getMaxEmpLOC());
        assertEquals("1@gmail.com", instance.getMaxEmp());
        
        assertEquals(100, instance.getMaxEmpLOC());
    }
    
    /**
     * Test of reduce method, of class Pass2_b.
     */
    @Test
    public void test20Reduce() throws Exception {
        System.out.println("reduce");
  
        Text key = (new Text("1@gmail.com"));
        ArrayList vals = new ArrayList<Text>();
        vals.add(new Text("-10"));
        vals.add(new Text("5"));
        Iterable<Text> values = vals;
        
        Pass2_b instance = new Pass2_b(-50);
        instance.reduce(key, values);
        
        // desired output
        ArrayList<Text> results = new ArrayList<Text>();
        results.add(new Text("1@gmail.com 20"));
        Iterable<Text> res = results;
        
        // actual output
        ArrayList redResults = instance.getReduce20Results();
        Collections.sort(redResults);
        
        assertEquals(redResults.toString(),res.toString());
        System.out.println(instance.maxEmp);
        System.out.println(instance.maxEmpLOC);
        assertEquals(instance.maxEmp, "1@gmail.com");
        
    }
}

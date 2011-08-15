/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package codesquare.badges.badge_14_15;

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
public class Pass1_bTest {
    
    public Pass1_bTest() {
    }

    /**
     * Test of map method, of class Pass1_b.
     */
    @Test
    public void testMap() throws Exception {
        System.out.println("map");
        LongWritable key = null;
        Text value = new Text("3fd3fh345g deannasurma@gmail.com 2109345234 GMT-7 3 [app.xml,hello.html,hi.jsp] 12 3 “this is a test”");
        Pass1_b instance = new Pass1_b();
        instance.map(key, value);
        
        ArrayList<String> results2 = new ArrayList<String>();
        results2.add("app.xml deannasurma@gmail.com"); 
        results2.add("hello.html deannasurma@gmail.com");
        results2.add("hi.jsp deannasurma@gmail.com");
        
        assertEquals(instance.getMapResults(),results2);
    }

    /**
     * Test of reduce method, of class Pass1_b.
     * Tests badge 14
     */
    @Test
    public void testBadge14() throws Exception {
        System.out.println("reduce");
        Text key = null;
        
        ArrayList vals = new ArrayList<Text>();
        vals.add(new Text("deannasurma@gmail.com"));
        vals.add(new Text("ericren@gmail.com"));
        Iterable<Text> values = vals;
        
        ArrayList<Text> results = new ArrayList<Text>();
        results.add(new Text("deannasurma@gmail.com 14")); 
        results.add(new Text("ericren@gmail.com 14"));
        Iterable<Text> res = results;
        
        Pass1_b instance = new Pass1_b();
        instance.reduce(key, values);
        
        assertEquals(instance.getReduce14Results().toString(),res.toString());
    }
    
    
    /**
     * Test of reduce method, of class Pass1_b.
     * Tests badge 15
     */
    @Test
    public void testBadge15() throws Exception {
        System.out.println("reduce");
        Text key = null;
        
        ArrayList vals = new ArrayList<Text>();
        vals.add(new Text("1@gmail.com"));
        vals.add(new Text("2@gmail.com"));
        vals.add(new Text("3@gmail.com"));
        vals.add(new Text("4@gmail.com"));
        vals.add(new Text("5@gmail.com"));
        vals.add(new Text("6@gmail.com"));
        vals.add(new Text("7@gmail.com"));
        vals.add(new Text("8@gmail.com"));
        vals.add(new Text("9@gmail.com"));
        Iterable<Text> values = vals;
        
        Pass1_b instance = new Pass1_b();
        instance.reduce(key, values);
        
        ArrayList<Text> results15 = new ArrayList<Text>();
        results15.add(new Text("1@gmail.com 15")); 
        results15.add(new Text("2@gmail.com 15")); 
        results15.add(new Text("3@gmail.com 15")); 
        results15.add(new Text("4@gmail.com 15")); 
        results15.add(new Text("5@gmail.com 15")); 
        results15.add(new Text("6@gmail.com 15")); 
        results15.add(new Text("7@gmail.com 15")); 
        results15.add(new Text("8@gmail.com 15")); 
        results15.add(new Text("9@gmail.com 15")); 
        Iterable<Text> res15 = results15;
        ArrayList red15Results = instance.getReduce15Results();
        Collections.sort(red15Results);
        
        ArrayList<Text> results14 = new ArrayList<Text>();
        results14.add(new Text("1@gmail.com 14")); 
        results14.add(new Text("2@gmail.com 14")); 
        results14.add(new Text("3@gmail.com 14")); 
        results14.add(new Text("4@gmail.com 14")); 
        results14.add(new Text("5@gmail.com 14")); 
        results14.add(new Text("6@gmail.com 14")); 
        results14.add(new Text("7@gmail.com 14")); 
        results14.add(new Text("8@gmail.com 14")); 
        results14.add(new Text("9@gmail.com 14")); 
        Iterable<Text> res14 = results14;
        ArrayList red14Results = instance.getReduce14Results();
        Collections.sort(red14Results);
        
        assertEquals(red14Results.toString(),res14.toString());
        assertEquals(red15Results.toString(),res15.toString());
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package codesquare.badges.badge_25;

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
     * Current minute test case
     */
    @Test
    public void testMapCurrMinute() throws Exception {
        System.out.println("map");
        LongWritable key = null;
        Text value = new Text("3fd3fh345g 1@gmail.com 1311809325 GMT-7 3 [x/app.xml,z/basic.html] 12 3 “this is a test”");
        Pass1_b instance = new Pass1_b(22, 23);
        instance.map(key, value);
        
        ArrayList<String> results = new ArrayList<String>();
        results.add("23-10-app.xml 23 29 1@gmail.com");
        results.add("23-10-basic.html 23 29 1@gmail.com");
        results.add("23-20-app.xml 23 29 1@gmail.com");
        results.add("23-20-basic.html 23 29 1@gmail.com");
        
        
        ArrayList res = instance.getMapResults();
        Collections.sort(res);
        
        assertEquals(res.toString(),results.toString());
    }
    
    /**
     * Test of map method, of class Pass1_b.
     * Previous minute test case
     */
    @Test
    public void testMapPrevMinute() throws Exception {
        System.out.println("map");
        LongWritable key = null;
        Text value = new Text("3fd3fh345g 1@gmail.com 1311809325 GMT-7 3 [x/app.xml,z/basic.html] 12 3 “this is a test”");
        Pass1_b instance = new Pass1_b(23, 24);
        instance.map(key, value);
        
        ArrayList<String> results = new ArrayList<String>();
        results.add("");
        
        ArrayList res = instance.getMapResults();
        Collections.sort(res);
        
        assertEquals(res.toString(),results.toString());
    }

    @Test
    public void testReduce() throws Exception {
        Text key = (new Text("23-10-app.xml"));
        ArrayList vals = new ArrayList<Text>();
        vals.add(new Text("23 29 1@gmail.com")); // pass
        vals.add(new Text("23 19 2@gmail.com")); // pass
        vals.add(new Text("23 39 7@gmail.com")); // pass
        vals.add(new Text("23 05 3@gmail.com")); // pass
        vals.add(new Text("22 55 4@gmail.com")); // pass
        vals.add(new Text("23 55 5@gmail.com")); // fail
        vals.add(new Text("22 30 6@gmail.com")); // fail
        Iterable<Text> values = vals;
        
        Pass1_b instance = new Pass1_b(22, 23);
        instance.reduce(key, values);
        
        ArrayList<Text> results = new ArrayList<Text>();
        results.add(new Text("1@gmail.com 25")); 
        results.add(new Text("2@gmail.com 25")); 
        results.add(new Text("3@gmail.com 25")); 
        results.add(new Text("4@gmail.com 25")); 
        results.add(new Text("7@gmail.com 25")); 
        Iterable<Text> res = results;
        ArrayList redResults = instance.getReduceResults();
        Collections.sort(redResults);

        
        assertEquals(redResults.toString(),res.toString());
    }
}

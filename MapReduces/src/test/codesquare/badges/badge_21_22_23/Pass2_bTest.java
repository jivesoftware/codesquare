/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package codesquare.badges.badge_21_22_23;

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
    
    /**
     * Test of map method, of class Pass2_b.
     */
    @Test
    public void testMap() throws Exception {
        System.out.println("map");
        LongWritable key = null;
        Text value = new Text("1@gmail.com 25");
        Pass2_b instance = new Pass2_b();
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
        Text key = (new Text("1@gmail.com"));
        ArrayList vals = new ArrayList<Text>();
        vals.add(new Text("#-10"));
        vals.add(new Text("2@gmail.com"));
        Iterable<Text> values = vals;
        
        Pass2_b instance = new Pass2_b();
        instance.reduce(key, values);
        
        // desired info output
        ArrayList<Text> infoResults = new ArrayList<Text>();
        Text x = new Text("1@gmail.com -10 2@gmail.com");
        infoResults.add(x);
        Iterable<Text> infoRes = infoResults;
        
        // desired LOC output
        ArrayList<Text> locResults = new ArrayList<Text>();
        locResults.add(new Text("1@gmail.com -10"));
        Iterable<Text> locRes = locResults;
        
        // actual output
        ArrayList redInfoResults = instance.getReduceInfoResults();
        Collections.sort(redInfoResults);
        ArrayList redLOCResults = instance.getReduceLOCResults();
        Collections.sort(redLOCResults);
        
        System.out.println(redLOCResults.toString());
        System.out.println(locRes.toString());
        System.out.println(redInfoResults.toString());
        System.out.println(infoRes.toString());
        
        assertEquals(redInfoResults.toString(),infoRes.toString());
        assertEquals(redLOCResults.toString(),locRes.toString());
    }
}

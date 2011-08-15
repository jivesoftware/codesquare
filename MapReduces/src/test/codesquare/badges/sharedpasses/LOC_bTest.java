package codesquare.badges.sharedpasses;

import java.util.Collections;
import java.util.ArrayList;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author deanna.surma
 */
public class LOC_bTest {
    
    public LOC_bTest() {
    }
    /**
     * Test of map method, of class LOC_b.
     */
    @Test
    public void testMap() throws Exception {
        LongWritable key = null;
        Text value = new Text("3fd3fh345g deannasurma@gmail.com 2109345234 GMT-7 3 [app.xml,hello.html,hi.jsp] 12 3 “this is a test”");
        LOC_b instance = new LOC_b();
        instance.map(key, value);
        
        ArrayList<String> results = new ArrayList<String>();
        results.add("deannasurma@gmail.com 9"); 
        
        assertEquals(instance.getMapResults(),results);
    }

    /**
     * Test of reduce method, of class LOC_b.
     */
    @Test
    public void testReduce() throws Exception {
         System.out.println("reduce");
        Text key = (new Text("1@gmail.com"));
        
        ArrayList vals = new ArrayList<Text>();
        vals.add(new IntWritable(10));
        vals.add(new IntWritable(10));
        vals.add(new IntWritable(10));
        vals.add(new IntWritable(10));
        vals.add(new IntWritable(10));
        Iterable<IntWritable> values = vals;
        
        LOC_b instance = new LOC_b();
        instance.reduce(key, values);
        
        ArrayList<Text> results = new ArrayList<Text>();
        results.add(new Text("1@gmail.com #50"));
        Iterable<Text> res = results;
        ArrayList redResults = instance.getReduceResults();
        Collections.sort(redResults);
        
        assertEquals(redResults.toString(),res.toString());
    }
}

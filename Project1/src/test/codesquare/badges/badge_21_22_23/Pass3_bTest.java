/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package codesquare.badges.badge_21_22_23;

import org.apache.hadoop.io.Text;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author deanna.surma
 */
public class Pass3_bTest {
    
    public Pass3_bTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of reduce method, of class Pass3_b.
     */
    @Test
    public void testReduce() throws Exception {
        System.out.println("reduce");
        Text key = null;
        Iterable<Text> values = null;
        Pass3_b instance = new Pass3_b();
        instance.reduce(key, values);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}

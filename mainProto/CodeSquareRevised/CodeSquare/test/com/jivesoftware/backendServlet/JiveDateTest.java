/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jivesoftware.backendServlet;

import java.util.Calendar;
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
public class JiveDateTest {
    
    public JiveDateTest() {
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
    
    @Test
    public void testConstructor(){
        JiveDate obj = new JiveDate("1311368415", "2011-07-11 09:44:59 -0600");
        
        assertEquals(obj.getLocalDate(),22);
        assertEquals(obj.getLocalHour(),14);
        assertEquals(obj.getLocalDay(),6); // 5 = thursday
        assertEquals(obj.getLocalMinute(), 0);
        assertEquals(obj.getLocalMonth(),6);
        assertEquals(obj.getLocalSecond(),15);
        assertEquals(obj.getLocalYear(), 2011);
        Calendar local = obj.getLocal();
        Calendar global = obj.getGlobal();
        
        assertEquals(obj.getGlobalDate(),22);
        assertEquals(obj.getGlobalHour(),15);
        assertEquals(obj.getGlobalDay(),6); // 5 = thursday
        assertEquals(obj.getGlobalMinute(), 0);
        assertEquals(obj.getGlobalMonth(),6);
        assertEquals(obj.getGlobalSecond(),15);
        assertEquals(obj.getGlobalYear(), 2011);
        
        int x=3; int y=3;
        System.out.println("EQUALS: "+(x==y));
        
    }

}

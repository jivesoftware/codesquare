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

    
    @Test
    public void testConstructor(){
        JiveDate obj = new JiveDate("1311368415", "2011-07-11 09:44:59 -0700");
        
        assertEquals(obj.getLocalDate(),22);
        assertEquals(obj.getLocalHour(),14);
        assertEquals(obj.getLocalDay(),6); // 5 = thursday
        assertEquals(obj.getLocalMinute(), 0);
        assertEquals(obj.getLocalMonth(),6);
        assertEquals(obj.getLocalSecond(),15);
        assertEquals(obj.getLocalYear(), 2011);
        
        
        assertEquals(obj.getGlobalDate(),22);
        assertEquals(obj.getGlobalHour(),21);
        assertEquals(obj.getGlobalDay(),6); // 5 = thursday
        assertEquals(obj.getGlobalMinute(), 0);
        assertEquals(obj.getGlobalMonth(),6);
        assertEquals(obj.getGlobalSecond(),15);
        assertEquals(obj.getGlobalYear(), 2011);
        }

}

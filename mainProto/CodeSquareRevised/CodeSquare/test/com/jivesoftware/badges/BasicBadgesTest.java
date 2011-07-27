/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jivesoftware.badges;

import com.jivesoftware.backendServlet.Commit;
import com.jivesoftware.backendServlet.JiveDate;
import java.util.Calendar;
import org.apache.hadoop.hbase.client.HTable;
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
public class BasicBadgesTest {
    

    /**
     * Test of checkMessageBadges method, of class BasicBadges.
     */
    @Test
    public void testCheckMessageBadges() {
        System.out.println("checkMessageBadges");
        UserInfo user = null;
        String message = "";
        BasicBadges instance = null;
        instance.checkMessageBadges(user, message);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of checkBadge30 method, of class BasicBadges.
     */
    @Test
    public void testCheckBadge30() {
        System.out.println("checkBadge30");
        UserInfo user = null;
        Commit c = null;
        BasicBadges instance = null;
        instance.checkBadge30(user, c);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of equals method, of class BasicBadges.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Calendar x = null;
        Calendar y = null;
        BasicBadges instance = null;
        boolean expResult = false;
        boolean result = instance.equals(x, y);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of checkBadges27And28 method, of class BasicBadges.
     */
    @Test
    public void testCheckBadges27And28() {
        System.out.println("checkBadges27And28");
        UserInfo user = null;
        JiveDate newDate = null;
        BasicBadges instance = null;
        instance.checkBadges27And28(user, newDate);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of checkNumCommitBadges method, of class BasicBadges.
     */
    @Test
    public void testCheckNumCommitBadges() {
        System.out.println("checkNumCommitBadges");
        UserInfo user = null;
        BasicBadges instance = null;
        instance.checkNumCommitBadges(user);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of checkHolidayBadges method, of class BasicBadges.
     */
    @Test
    public void testCheckHolidayBadges() {
        System.out.println("checkHolidayBadges");
        UserInfo user = null;
        JiveDate commitDate = null;
        BasicBadges instance = null;
        instance.checkHolidayBadges(user, commitDate);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of checkTimeBadges method, of class BasicBadges.
     */
    @Test
    public void testCheckTimeBadges() {
        System.out.println("checkTimeBadges");
        UserInfo user = null;
        JiveDate commitDate = null;
        BasicBadges instance = null;
        instance.checkTimeBadges(user, commitDate);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}

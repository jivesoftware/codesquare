/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jivesoftware.badges;

import com.jivesoftware.toolbox.ServletTools;
import org.json.JSONObject;
import org.apache.hadoop.conf.Configuration;
import com.jivesoftware.toolbox.HbaseTools;
import com.jivesoftware.backendServlet.Commit;
import java.util.ArrayList;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author deanna.surma
 */
public class BasicBadgesTest {
    
//
//    /**
//     * Test of checkMessageBadges method, of class BasicBadges.
//     */
//    @Test
//    public void testCheckMessageBadges1() throws Exception {
//        System.out.println("checkMessageBadges");
//        Configuration hbaseConfig = HbaseTools.getHBaseConfiguration();
//        HTable table = HbaseTools.getTable(hbaseConfig);
//        
//        String unixTime = "1312833462";
//        JSONObject jCommit = new JSONObject("{ \"cID\": \"61825bad2be0cad5d275e1a5f859e376e40aafc0\", \"cMes\": \"df sjiveasdf asdf adsf\", \"unixtimestamp\": \"1312828808\", \"isotimestamp\": \"2011-08-08 11:40:08 -0700\", \"name\": \"Justin Kikuchi\", \"email\": \"justin.kikuchi@jivesoftware.com\", \"stats\": \"[1 1 x.html]\"}");
//        Commit c = ServletTools.convertToCommit(jCommit, unixTime);
//            
//        Result data = HbaseTools.getRowData(table, c.getEmail());
//        UserInfo currentUser = new UserInfo(c.getEmail(), data, c.getCommitDate().getLocal());
//        BasicBadges instance = new BasicBadges();
//        instance.checkMessageBadges(currentUser, c.getMessage()); 
//        
//        ArrayList<String> badges = new ArrayList<String>();
//        badges.add("26");
//        
//        assertEquals(currentUser.getBadges(), badges);
//        table.close();
//    }
//    
//    /**
//     * Test of checkMessageBadges method, of class BasicBadges.
//     */
//    @Test
//    public void testCheckMessageBadges2() throws Exception {
//        System.out.println("checkMessageBadges");
//        Configuration hbaseConfig = HbaseTools.getHBaseConfiguration();
//        HTable table = HbaseTools.getTable(hbaseConfig);
//        
//        String unixTime = "1312833462";
//        JSONObject jCommit = new JSONObject("{ \"cID\": \"61825bad2be0cad5d275e1a5f859e376e40aafc0\", \"cMes\": \"sdf sdf sd fadws\", \"unixtimestamp\": \"1312828808\", \"isotimestamp\": \"2011-08-08 11:40:08 -0700\", \"name\": \"Justin Kikuchi\", \"email\": \"justin.kikuchi@jivesoftware.com\", \"stats\": \"[1 1 x.html]\"}");
//        Commit c = ServletTools.convertToCommit(jCommit, unixTime);
//            
//        Result data = HbaseTools.getRowData(table, c.getEmail());
//        UserInfo currentUser = new UserInfo(c.getEmail(), data, c.getCommitDate().getLocal());
//        BasicBadges instance = new BasicBadges();
//        instance.checkMessageBadges(currentUser, c.getMessage()); 
//        
//        ArrayList<String> badges = new ArrayList<String>();
//        
//        assertEquals(currentUser.getBadges(), badges);
//        table.close();
//    }
//    
//    /**
//     * Test of checkMessageBadges method, of class BasicBadges.
//     */
//    @Test
//    public void testCheckHolidayBadges6() throws Exception {
//        System.out.println("checkMessageBadges");
//        Configuration hbaseConfig = HbaseTools.getHBaseConfiguration();
//        HTable table = HbaseTools.getTable(hbaseConfig);
//        
//        String unixTime = "1312833462";
//        JSONObject jCommit = new JSONObject("{ \"cID\": \"61825bad2be0cad5d275e1a5f859e376e40aafc0\", \"cMes\": \"jive\", \"unixtimestamp\": \"1320093608\", \"isotimestamp\": \"2011-08-08 11:40:08 -0700\", \"name\": \"Justin Kikuchi\", \"email\": \"justin.kikuchi@jivesoftware.com\", \"stats\": \"[1 1 x.html]\"}");
//        System.out.println(jCommit);
//        
//        Commit c = ServletTools.convertToCommit(jCommit, unixTime);
//            
//        Result data = HbaseTools.getRowData(table, c.getEmail());
//        UserInfo currentUser = new UserInfo(c.getEmail(), data, c.getCommitDate().getLocal());
//        BasicBadges instance = new BasicBadges();
//        instance.checkHolidayBadges(currentUser, c.getCommitDate()); 
//        
//        ArrayList<String> badges = new ArrayList<String>();
//        badges.add("6");
//        
//        assertEquals(currentUser.getBadges(), badges);
//        table.close();
//    }
//    
//    /**
//     * Test of checkMessageBadges method, of class BasicBadges.
//     */
//    @Test
//    public void testCheckHolidayBadges7() throws Exception {
//        System.out.println("checkMessageBadges");
//        Configuration hbaseConfig = HbaseTools.getHBaseConfiguration();
//        HTable table = HbaseTools.getTable(hbaseConfig);
//        
//        String unixTime = "1312833462";
//        JSONObject jCommit = new JSONObject("{ \"cID\": \"61825bad2be0cad5d275e1a5f859e376e40aafc0\", \"cMes\": \"jive\", \"unixtimestamp\": \"1300394408\", \"isotimestamp\": \"2011-08-08 11:40:08 -0700\", \"name\": \"Justin Kikuchi\", \"email\": \"justin.kikuchi@jivesoftware.com\", \"stats\": \"[1 1 x.html]\"}");
//        System.out.println(jCommit);
//        
//        Commit c = ServletTools.convertToCommit(jCommit, unixTime);
//            
//        Result data = HbaseTools.getRowData(table, c.getEmail());
//        UserInfo currentUser = new UserInfo(c.getEmail(), data, c.getCommitDate().getLocal());
//        BasicBadges instance = new BasicBadges();
//        instance.checkHolidayBadges(currentUser, c.getCommitDate()); 
//        
//        ArrayList<String> badges = new ArrayList<String>();
//        badges.add("7");
//        
//        assertEquals(currentUser.getBadges(), badges);
//        table.close();
//    }
//    
//    /**
//     * Test of checkMessageBadges method, of class BasicBadges.
//     */
//    @Test
//    public void testCheckHolidayBadges8() throws Exception {
//        System.out.println("checkMessageBadges");
//        Configuration hbaseConfig = HbaseTools.getHBaseConfiguration();
//        HTable table = HbaseTools.getTable(hbaseConfig);
//        
//        String unixTime = "1312833462";
//        JSONObject jCommit = new JSONObject("{ \"cID\": \"61825bad2be0cad5d275e1a5f859e376e40aafc0\", \"cMes\": \"jive\", \"unixtimestamp\": \"1204285208\", \"isotimestamp\": \"2011-08-08 11:40:08 -0700\", \"name\": \"Justin Kikuchi\", \"email\": \"justin.kikuchi@jivesoftware.com\", \"stats\": \"[1 1 x.html]\"}");
//        System.out.println(jCommit);
//        
//        Commit c = ServletTools.convertToCommit(jCommit, unixTime);
//            
//        Result data = HbaseTools.getRowData(table, c.getEmail());
//        UserInfo currentUser = new UserInfo(c.getEmail(), data, c.getCommitDate().getLocal());
//        BasicBadges instance = new BasicBadges();
//        instance.checkHolidayBadges(currentUser, c.getCommitDate()); 
//        
//        ArrayList<String> badges = new ArrayList<String>();
//        badges.add("8");
//        
//        assertEquals(currentUser.getBadges(), badges);
//        table.close();
//    }
//    
//    /**
//     * Test of checkMessageBadges method, of class BasicBadges.
//     */
//    @Test
//    public void testCheckHolidayBadges9() throws Exception {
//        System.out.println("checkMessageBadges");
//        Configuration hbaseConfig = HbaseTools.getHBaseConfiguration();
//        HTable table = HbaseTools.getTable(hbaseConfig);
//        
//        String unixTime = "1312833462";
//        JSONObject jCommit = new JSONObject("{ \"cID\": \"61825bad2be0cad5d275e1a5f859e376e40aafc0\", \"cMes\": \"jive\", \"unixtimestamp\": \"1297716008\", \"isotimestamp\": \"2011-08-08 11:40:08 -0700\", \"name\": \"Justin Kikuchi\", \"email\": \"justin.kikuchi@jivesoftware.com\", \"stats\": \"[1 1 x.html]\"}");
//        System.out.println(jCommit);
//        
//        Commit c = ServletTools.convertToCommit(jCommit, unixTime);
//            
//        Result data = HbaseTools.getRowData(table, c.getEmail());
//        UserInfo currentUser = new UserInfo(c.getEmail(), data, c.getCommitDate().getLocal());
//        BasicBadges instance = new BasicBadges();
//        instance.checkHolidayBadges(currentUser, c.getCommitDate()); 
//        
//        ArrayList<String> badges = new ArrayList<String>();
//        badges.add("9");
//        
//        assertEquals(currentUser.getBadges(), badges);
//        table.close();
//    }
//    
//    /**
//     * Test of checkMessageBadges method, of class BasicBadges.
//     */
//    @Test
//    public void testCheckHolidayBadges10() throws Exception {
//        System.out.println("checkMessageBadges");
//        Configuration hbaseConfig = HbaseTools.getHBaseConfiguration();
//        HTable table = HbaseTools.getTable(hbaseConfig);
//        
//        String unixTime = "1312833462";
//        JSONObject jCommit = new JSONObject("{ \"cID\": \"61825bad2be0cad5d275e1a5f859e376e40aafc0\", \"cMes\": \"jive\", \"unixtimestamp\": \"1300135208\", \"isotimestamp\": \"2011-08-08 11:40:08 -0700\", \"name\": \"Justin Kikuchi\", \"email\": \"justin.kikuchi@jivesoftware.com\", \"stats\": \"[1 1 x.html]\"}");
//        System.out.println(jCommit);
//        
//        Commit c = ServletTools.convertToCommit(jCommit, unixTime);
//            
//        Result data = HbaseTools.getRowData(table, c.getEmail());
//        UserInfo currentUser = new UserInfo(c.getEmail(), data, c.getCommitDate().getLocal());
//        BasicBadges instance = new BasicBadges();
//        instance.checkHolidayBadges(currentUser, c.getCommitDate()); 
//        
//        ArrayList<String> badges = new ArrayList<String>();
//        badges.add("10");
//        
//        assertEquals(currentUser.getBadges(), badges);
//        table.close();
//    }
//    
//    /**
//     * Test of checkMessageBadges method, of class BasicBadges.
//     */
//    @Test
//    public void testCheckTimeBadges12() throws Exception {
//        
//        System.out.println("checkMessageBadges");
//        Configuration hbaseConfig = HbaseTools.getHBaseConfiguration();
//        HTable table = HbaseTools.getTable(hbaseConfig);
//        
//        String unixTime = "1312833462";
//        JSONObject jCommit = new JSONObject("{ \"cID\": \"61825bad2be0cad5d275e1a5f859e376e40aafc0\", \"cMes\": \"jive\", \"unixtimestamp\": \"1313041429\", \"isotimestamp\": \"2011-08-08 11:40:08 -0700\", \"name\": \"Justin Kikuchi\", \"email\": \"justin.kikuchi@jivesoftware.com\", \"stats\": \"[1 1 x.html]\"}");
//        
//        Commit c = ServletTools.convertToCommit(jCommit, unixTime);
//        
//        Result data = HbaseTools.getRowData(table, c.getEmail());
//        UserInfo currentUser = new UserInfo(c.getEmail(), data, c.getCommitDate().getLocal());
//        BasicBadges instance = new BasicBadges();
//        instance.checkTimeBadges(currentUser, c.getCommitDate()); 
//        
//        ArrayList<String> badges = new ArrayList<String>();
//        badges.add("12");
//        
//        // assertEquals(currentUser.getBadges(), badges);
//        table.close();
//    }
//        
//    /**
//     * Test of checkMessageBadges method, of class BasicBadges.
//     */
//    @Test
//    public void testCheckTimeBadges13() throws Exception {
//        System.out.println("checkMessageBadges");
//        Configuration hbaseConfig = HbaseTools.getHBaseConfiguration();
//        HTable table = HbaseTools.getTable(hbaseConfig);
//        
//        String unixTime = "1312833462";
//        JSONObject jCommit = new JSONObject("{ \"cID\": \"61825bad2be0cad5d275e1a5f859e376e40aafc0\", \"cMes\": \"jive\", \"unixtimestamp\": \"1312980229\", \"isotimestamp\": \"2011-08-08 11:40:08 -0700\", \"name\": \"Justin Kikuchi\", \"email\": \"justin.kikuchi@jivesoftware.com\", \"stats\": \"[1 1 x.html]\"}");
//        System.out.println(jCommit);
//        
//        Commit c = ServletTools.convertToCommit(jCommit, unixTime);
//            
//        Result data = HbaseTools.getRowData(table, c.getEmail());
//        UserInfo currentUser = new UserInfo(c.getEmail(), data, c.getCommitDate().getLocal());
//        BasicBadges instance = new BasicBadges();
//        instance.checkTimeBadges(currentUser, c.getCommitDate()); 
//        
//        ArrayList<String> badges = new ArrayList<String>();
//        badges.add("13");
//        
//        assertEquals(currentUser.getBadges(), badges);
//        table.close();
//    }
//
//    /**
//     * Test of checkMessageBadges method, of class BasicBadges.
//     */
//    @Test
//    public void testCheckTimeBadges18() throws Exception {
//        System.out.println("checkMessageBadges");
//        Configuration hbaseConfig = HbaseTools.getHBaseConfiguration();
//        HTable table = HbaseTools.getTable(hbaseConfig);
//        
//        String unixTime = "1312833462";
//        JSONObject jCommit = new JSONObject("{ \"cID\": \"61825bad2be0cad5d275e1a5f859e376e40aafc0\", \"cMes\": \"jive\", \"unixtimestamp\": \"1312803829\", \"isotimestamp\": \"2011-08-08 11:40:08 -0700\", \"name\": \"Justin Kikuchi\", \"email\": \"justin.kikuchi@jivesoftware.com\", \"stats\": \"[1 1 x.html]\"}");
//        System.out.println(jCommit);
//        
//        Commit c = ServletTools.convertToCommit(jCommit, unixTime);
//            
//        Result data = HbaseTools.getRowData(table, c.getEmail());
//        UserInfo currentUser = new UserInfo(c.getEmail(), data, c.getCommitDate().getLocal());
//        BasicBadges instance = new BasicBadges();
//        instance.checkTimeBadges(currentUser, c.getCommitDate()); 
//        
//        ArrayList<String> badges = new ArrayList<String>();
//        badges.add("13");
//        badges.add("18");
//        
//        assertEquals(currentUser.getBadges(), badges);
//        table.close();
//    }
//    
//    /**
//     * Test of checkMessageBadges method, of class BasicBadges.
//     */
//    @Test
//    public void testCheckTimeBadges19() throws Exception {
//        System.out.println("checkMessageBadges");
//        Configuration hbaseConfig = HbaseTools.getHBaseConfiguration();
//        HTable table = HbaseTools.getTable(hbaseConfig);
//        
//        String unixTime = "1312833462";
//        JSONObject jCommit = new JSONObject("{ \"cID\": \"61825bad2be0cad5d275e1a5f859e376e40aafc0\", \"cMes\": \"jive\", \"unixtimestamp\": \"1313192629\", \"isotimestamp\": \"2011-08-08 11:40:08 -0700\", \"name\": \"Justin Kikuchi\", \"email\": \"justin.kikuchi@jivesoftware.com\", \"stats\": \"[1 1 x.html]\"}");
//        System.out.println(jCommit);
//        
//        Commit c = ServletTools.convertToCommit(jCommit, unixTime);
//            
//        Result data = HbaseTools.getRowData(table, c.getEmail());
//        UserInfo currentUser = new UserInfo(c.getEmail(), data, c.getCommitDate().getLocal());
//        BasicBadges instance = new BasicBadges();
//        instance.checkTimeBadges(currentUser, c.getCommitDate()); 
//        
//        ArrayList<String> badges = new ArrayList<String>();
//        badges.add("19");
//        
//        assertEquals(currentUser.getBadges(), badges);
//        table.close();
//    }    
//    
//    /**
//     * Test of checkMessageBadges method, of class BasicBadges.
//     */
//    @Test
//    public void testCheckTimeBadges29() throws Exception {
//        System.out.println("testCheckTimeBadges29");
//        Configuration hbaseConfig = HbaseTools.getHBaseConfiguration();
//        HTable table = HbaseTools.getTable(hbaseConfig);
//        
//        String unixTime = "1312833462";
//        JSONObject jCommit = new JSONObject("{ \"cID\": \"61825bad2be0cad5d275e1a5f859e376e40aafc0\", \"cMes\": \"jive\", \"unixtimestamp\": \"1219509608\", \"isotimestamp\": \"2011-08-08 11:40:08 -0700\", \"name\": \"Justin Kikuchi\", \"email\": \"justin.kikuchi@jivesoftware.com\", \"stats\": \"[1 1 x.html]\"}");
//        System.out.println(jCommit);
//        
//        Commit c = ServletTools.convertToCommit(jCommit, unixTime);
//            
//        Result data = HbaseTools.getRowData(table, c.getEmail());
//        UserInfo currentUser = new UserInfo(c.getEmail(), data, c.getCommitDate().getLocal());
//        BasicBadges instance = new BasicBadges();
//        instance.checkTimeBadges(currentUser, c.getCommitDate()); 
//        
//        ArrayList<String> badges = new ArrayList<String>();
//        badges.add("29");
//        
//        assertEquals(currentUser.getBadges(), badges);
//        table.close();
//    }
//    

//    @Test
//    public void testBadge30() throws Exception {
//        System.out.println("testBadge30");
//        Configuration hbaseConfig = HbaseTools.getHBaseConfiguration();
//        HTable table = HbaseTools.getTable(hbaseConfig);
//        
//        String unixTime = "1312833462";
//        
//        JSONObject jCommit = new JSONObject("{ \"cID\": \"61825bad2be0cad5d275e1a5f859e376e40aafc0\", \"cMes\": \"jive\", \"unixtimestamp\": \"1312256629\", \"isotimestamp\": \"2011-08-08 11:40:08 -0700\", \"name\": \"Justin Kikuchi\", \"email\": \"justin.kikuchi@jivesoftware.com\", \"stats\": \"[1 1 x.html]\"}");
//        Commit c1 = ServletTools.convertToCommit(jCommit, unixTime);
//            
//        Result data = HbaseTools.getRowData(table, c1.getEmail());
//        UserInfo currentUser = new UserInfo(c1.getEmail(), data, c1.getCommitDate().getLocal());
//        
//        JSONObject jCommit2 = new JSONObject("{ \"cID\": \"61825bad2be0cad5d275e1a5f859e376e40aafc0\", \"cMes\": \"jive\", \"unixtimestamp\": \"1312343029\", \"isotimestamp\": \"2011-08-08 11:40:08 -0700\", \"name\": \"Justin Kikuchi\", \"email\": \"justin.kikuchi@jivesoftware.com\", \"stats\": \"[1 1 x.html]\"}");
//        Commit c2 = ServletTools.convertToCommit(jCommit2, unixTime);
//        JSONObject jCommit3 = new JSONObject("{ \"cID\": \"61825bad2be0cad5d275e1a5f859e376e40aafc0\", \"cMes\": \"jive\", \"unixtimestamp\": \"1312429429\", \"isotimestamp\": \"2011-08-08 11:40:08 -0700\", \"name\": \"Justin Kikuchi\", \"email\": \"justin.kikuchi@jivesoftware.com\", \"stats\": \"[1 1 x.html]\"}");
//        Commit c3 = ServletTools.convertToCommit(jCommit3, unixTime);
//        JSONObject jCommit4 = new JSONObject("{ \"cID\": \"61825bad2be0cad5d275e1a5f859e376e40aafc0\", \"cMes\": \"jive\", \"unixtimestamp\": \"1312515829\", \"isotimestamp\": \"2011-08-08 11:40:08 -0700\", \"name\": \"Justin Kikuchi\", \"email\": \"justin.kikuchi@jivesoftware.com\", \"stats\": \"[1 1 x.html]\"}");
//        Commit c4 = ServletTools.convertToCommit(jCommit4, unixTime);
//        JSONObject jCommit5 = new JSONObject("{ \"cID\": \"61825bad2be0cad5d275e1a5f859e376e40aafc0\", \"cMes\": \"jive\", \"unixtimestamp\": \"1312602229\", \"isotimestamp\": \"2011-08-08 11:40:08 -0700\", \"name\": \"Justin Kikuchi\", \"email\": \"justin.kikuchi@jivesoftware.com\", \"stats\": \"[1 1 x.html]\"}");
//        Commit c5 = ServletTools.convertToCommit(jCommit5, unixTime);
//        JSONObject jCommit6 = new JSONObject("{ \"cID\": \"61825bad2be0cad5d275e1a5f859e376e40aafc0\", \"cMes\": \"jive\", \"unixtimestamp\": \"1312688629\", \"isotimestamp\": \"2011-08-08 11:40:08 -0700\", \"name\": \"Justin Kikuchi\", \"email\": \"justin.kikuchi@jivesoftware.com\", \"stats\": \"[1 1 x.html]\"}");
//        Commit c6 = ServletTools.convertToCommit(jCommit6, unixTime);
//        JSONObject jCommit7 = new JSONObject("{ \"cID\": \"61825bad2be0cad5d275e1a5f859e376e40aafc0\", \"cMes\": \"jive\", \"unixtimestamp\": \"1312775029\", \"isotimestamp\": \"2011-08-08 11:40:08 -0700\", \"name\": \"Justin Kikuchi\", \"email\": \"justin.kikuchi@jivesoftware.com\", \"stats\": \"[1 1 x.html]\"}");
//        Commit c7 = ServletTools.convertToCommit(jCommit7, unixTime);
//        
//        BasicBadges instance = new BasicBadges();
//        
//        instance.checkBadge30(currentUser, c1); 
//        instance.checkBadge30(currentUser, c2); 
//        instance.checkBadge30(currentUser, c3); 
//        instance.checkBadge30(currentUser, c4);
//        instance.checkBadge30(currentUser, c5); 
//        instance.checkBadge30(currentUser, c6); 
//        instance.checkBadge30(currentUser, c7); 
//        
//        ArrayList<String> badges = new ArrayList<String>();
//        badges.add("30");
//        
//        assertEquals(currentUser.getBadges(), badges);
//        table.close();
//    }

    // probably need to comment out if(user.getNumCommits()==1){} to make this work
    @Test
    public void testCheckBadges27And28() throws Exception {
        System.out.println("checkMessageBadges");
        Configuration hbaseConfig = HbaseTools.getHBaseConfiguration();
        HTable table = HbaseTools.getTable(hbaseConfig);
        
        String unixTime = "1312833462";
        JSONObject jCommit1 = new JSONObject("{ \"cID\": \"61825bad2be0cad5d275e1a5f859e376e40aafc0\", \"cMes\": \"jive\", \"unixtimestamp\": \"1312199029\", \"isotimestamp\": \"2011-08-08 11:40:08 -0700\", \"name\": \"Justin Kikuchi\", \"email\": \"justin.kikuchi@jivesoftware.com\", \"stats\": \"[1 1 x.html]\"}");
        Commit c1 = ServletTools.convertToCommit(jCommit1, unixTime);
        JSONObject jCommit2 = new JSONObject("{ \"cID\": \"61825bad2be0cad5d275e1a5f859e376e40aafc0\", \"cMes\": \"jive\", \"unixtimestamp\": \"1312285429\", \"isotimestamp\": \"2011-08-08 11:40:08 -0700\", \"name\": \"Justin Kikuchi\", \"email\": \"justin.kikuchi@jivesoftware.com\", \"stats\": \"[1 1 x.html]\"}");
        Commit c2 = ServletTools.convertToCommit(jCommit2, unixTime);
        JSONObject jCommit3 = new JSONObject("{ \"cID\": \"61825bad2be0cad5d275e1a5f859e376e40aafc0\", \"cMes\": \"jive\", \"unixtimestamp\": \"1313840629\", \"isotimestamp\": \"2011-08-08 11:40:08 -0700\", \"name\": \"Justin Kikuchi\", \"email\": \"justin.kikuchi@jivesoftware.com\", \"stats\": \"[1 1 x.html]\"}");
        Commit c3 = ServletTools.convertToCommit(jCommit3, unixTime);
            
        Result data = HbaseTools.getRowData(table, c1.getEmail());
        UserInfo currentUser = new UserInfo(c1.getEmail(), data, c1.getCommitDate().getLocal());
        BasicBadges instance = new BasicBadges();
        System.out.println("HERE!!!!!");
        instance.checkBadges27And28(currentUser, c1.getCommitDate()); 
        instance.checkBadges27And28(currentUser, c2.getCommitDate()); 
        instance.checkBadges27And28(currentUser, c3.getCommitDate()); 
        
        ArrayList<String> badges = new ArrayList<String>();
        badges.add("27");
        badges.add("28");
        
        assertEquals(currentUser.getBadges(), badges);
        table.close();
    }   
    
}

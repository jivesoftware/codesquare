/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jivesoftware.badges;

import com.jivesoftware.backendServlet.JiveDate;
import com.jivesoftware.toolbox.HbaseTools;
import java.util.ArrayList;
import java.util.Calendar;
import org.apache.hadoop.hbase.client.Result;

/**
 *
 * @author deanna.surma
 */
public class UserInfo {
    int badgesWeek;
    int consecCommits;
    String lastCommit;
    Long lastCommitTime;
    String lastCommitZone;
    int numBugs;
    int numCommits;
    String email;
    
    private ArrayList<String> badges = new ArrayList<String>();
    
    JiveDate date;
    
    UserInfo(String email, Result data, Calendar newDate) {
        System.out.println("IN USER");
        int[] fieldValues = HbaseTools.getFields(data, new String[] { "badgesWeek", "numBugs", "numCommits", "consecCommits" });
	System.out.println("IN USER");
        badgesWeek = fieldValues[0];
        System.out.println("IN USER0: "+fieldValues[0]);
        numBugs = fieldValues[1];
        System.out.println("IN USER1: "+fieldValues[1]);
        numCommits = fieldValues[2] + 1;
        System.out.println("IN USER2: "+fieldValues[2]);
        consecCommits = fieldValues[3];
        System.out.println("IN USER3: "+fieldValues[3]);
        
        lastCommit = HbaseTools.getLastCommit(data, newDate);
        System.out.println("IN USER4: "+lastCommit);
        String[] temp = lastCommit.split(" ");
        lastCommitTime = Long.parseLong(temp[0]); 
        System.out.println("IN USER5: "+lastCommitTime);
        lastCommitZone = temp[1];
        System.out.println("IN USER6: "+lastCommitZone);
        date = new JiveDate(temp[0], lastCommitZone);
        System.out.println("HERE");
        this.email = email;
        System.out.println("IN USER7: "+email);
    }
    
            /**
     * 
     * @return email
     */
    public String getEmail() {
    	return email;
    }
    
    public void addBadge(String badgeNumber){
        if(!badges.contains(badgeNumber))
            badges.add(badgeNumber);
    }

     /**
     * 
     * @return badges
     */
    public ArrayList<String> getBadges() {
    	return badges;
    }
    
     /**
     * 
     * @return badgesWeek
     */
    public int getBadgesWeek() {
    	return badgesWeek;
    }
    
    
     /**
     * 
     * @return badconsecCommitsgesWeek
     */
    public int getConsecCommits() {
    	return consecCommits;
    }
    

        /**
     * 
     * @return date
     */
    public JiveDate getDate() {
    	return date;
    }
    
    /**
     * 
     * @return lastCommit
     */
    public String getLastCommit() {
    	return lastCommit;
    }

        /**
     * 
     * @return lastCommitTime
     */
    public Long getLastCommitTime() {
    	return lastCommitTime;
    }
    
    /**
     * 
     * @return numBugs
     */
    public int getNumBugs() {
    	return numBugs;
    }
    
    /**
     * 
     * @return numCommits
     */
    public int getNumCommits() {
    	return numCommits;
    }
    
    public void incrementCommits(){
        this.numCommits++;
    }
    
    
    /**
     * 
     * increment consecCommits
     */
    public void incrementConsecCommits() {
    	this.consecCommits = consecCommits+1;
    }
    public void resetConsecCommits() {
        this.consecCommits = 1;
    }
}

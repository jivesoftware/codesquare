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
        int[] fieldValues = HbaseTools.getFields(data, new String[] {"numBugs", "numCommits", "consecCommits" });
        numBugs = fieldValues[0];
        numCommits = fieldValues[1];
        consecCommits = fieldValues[2];
        lastCommit = HbaseTools.getLastCommit(data, newDate);
        String[] temp = lastCommit.split(" ");
        lastCommitTime = Long.parseLong(temp[0]); 
        lastCommitZone = temp[1];
        date = new JiveDate(temp[0], lastCommitZone);
        this.email = email;
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
     * set date
     */
    public void setDate(JiveDate date) {
    	this.date=date;
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

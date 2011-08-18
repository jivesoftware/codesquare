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
    String id;
    String name;
    
    private ArrayList<String> badges = new ArrayList<String>();
    
    JiveDate date;
    /***
     * Constructor that uses data from the hbase as well as the new commit date
     * @param email
     * @param data
     * @param newDate 
     */
    UserInfo(String email, Result data, Calendar newDate) {
        int[] fieldValues = HbaseTools.getFields(data, new String[] {"numBugs", "numCommits", "consecCommits" });
        this.numBugs = fieldValues[0];
        this.numCommits = fieldValues[1];
        this.consecCommits = fieldValues[2];
        this.lastCommit = HbaseTools.getLastCommit(data, newDate);
        String[] temp = lastCommit.split(" ");
        this.lastCommitTime = Long.parseLong(temp[0]); 
        this.lastCommitZone = temp[1];
        this.date = new JiveDate(temp[0], lastCommitZone);
        this.email = email;
        this.id = HbaseTools.getUserId(data);
        this.name = HbaseTools.getName(data);
    }
    
    /**
     * 
     * @return email
     */
    public String getEmail() {
    	return email;
    }
    /**
     * Adds badge to the user
     * @param badgeNumber 
     */
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
     * @return id
     */
    public String getId() {
        return id;
    }
    /**
     * 
     * @return name
     */
    public String getName(){
        return name;
    }
    /**
     * 
     * @return numCommits
     */
    public int getNumCommits() {
    	return numCommits;
    }
    /**
     * increment number of commits
     */
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
    /**
     * reset consecutive commits
     */
    public void resetConsecCommits() {
        this.consecCommits = 1;
    }
    
    
}

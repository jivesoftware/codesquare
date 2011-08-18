package com.jivesoftware.badges;

import java.util.ArrayList;

import org.apache.hadoop.hbase.client.Result;

import com.jivesoftware.backendServlet.JiveDate;
import com.jivesoftware.backendServlet.Commit;
import java.io.IOException;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hbase.client.HTable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jivesoftware.toolbox.HDFSTools;
import com.jivesoftware.toolbox.HbaseTools;
import com.jivesoftware.toolbox.ServletTools;
import com.jivesoftware.toolbox.Badge;
import com.jivesoftware.activityStream.ActivityPoster;
import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;
import java.util.Collections;

/***
 * This class checks for basic badges
 * 
 * @author justin.kikuchi
 * @author deanna.surma
 */
public final class BasicBadges {
    private JSONArray jArrCommits;
    private FileSystem hdfs;
    private HTable table;
    private String unixTime;
    
    /***
     * Constructor for basic badges
     * @param jArrCommits
     * @param hdfs
     * @param table
     * @param unixTime
     * @throws JSONException
     * @throws IOException
     * @throws Exception 
     */
    public BasicBadges(JSONArray jArrCommits, FileSystem hdfs, HTable table, 
                        String unixTime) throws JSONException, IOException, Exception {
        this.jArrCommits = jArrCommits;
        this.hdfs = hdfs;
        this.table = table;
        this.unixTime = unixTime;
    }
    
    /***
     * This writes to the hdfs, checks for basic badges, and saves to the hbase
     * @throws JSONException
     * @throws IOException
     * @throws Exception 
     */    
    public void processBadges() throws JSONException, IOException, Exception{
    // iterate through and process/store info locally
        UserInfo user = null;
        Result data = null;
        boolean installed=true;
        // get user
        
        for(int i = 0;i < jArrCommits.length();i++){
            JSONObject jCommit = new JSONObject(jArrCommits.get(i).toString());
            Commit c = ServletTools.convertToCommit(jCommit, unixTime);
            //first iteration, create user object
            if(i==0){
                data = HbaseTools.getRowData(table, c.getEmail());
                if (data == null) {
                    installed=false;
                }
                else if(data.isEmpty()){
                    installed=false;
                }
                else {
                    user = new UserInfo(c.getEmail(), data, c.getCommitDate().getLocal());
                }
            }
            if(HDFSTools.writeCommitToHDFS(hdfs, c)){
                if(installed){
                    user.incrementCommits();
                    awardBasicBadges(user, c);
                }
            }
        }
        
        // iterate through users and add to hbase
        //ArrayList<String> arrBadges = entry.getValue().getBadges();
        ArrayList<String> newBadges = user.getBadges();
        Object[] badgeList = HbaseTools.getBadges(data);
        ArrayList<String> oldBadges = (ArrayList<String>) badgeList[0];
        for (int i = 0; i < newBadges.size(); i++) {
            if (oldBadges.contains(newBadges.get(i))) {
                newBadges.remove(i--);
            }
        }

        checkBadge16(newBadges, data, unixTime);
        System.out.println(newBadges.toString() + "old: " + oldBadges.toString());
        if(oldBadges.contains("16")){
                    System.out.println(newBadges.toString() + "2old: " + oldBadges.toString());

            newBadges.remove(newBadges.lastIndexOf("16"));
        }

        String newBadgesString = "";
        for(String s : newBadges){
            newBadgesString += s + " ";
        }



        HbaseTools.addRow(table,
            user.getEmail(),
            user.getLastCommit(),
            user.getNumBugs(),
            user.getNumCommits(),
            user.getConsecCommits(),
            (newBadgesString + badgeList[1]).trim(),
            newBadges.toArray(new String[newBadges.size()]),
            unixTime);
        
        
        String[][] badgesList = ServletTools.getBadgeInfo();
        ArrayList<Badge> badges = new ArrayList<Badge>();
        for(String s : newBadges){
            Badge badge = new Badge(badgesList[Integer.parseInt(s)-1][0], badgesList[Integer.parseInt(s)-1][1], s+".png");
            badges.add(badge);
        }
        if(!badges.isEmpty()){
            String jsonActivity = ServletTools.makeJSONPost(badges, user.getName());
            System.out.println("JSON:   " +jsonActivity);
            ActivityPoster.postToActivity(user.getId(), null, jsonActivity);
        }
    }


    /***
     * This method calls each individual badge checker
     * @param currentUser user that commit belongs to
     * @param c new commit object
     */
    public void awardBasicBadges(UserInfo currentUser, Commit c) {
        checkHolidayBadges(currentUser, c.getCommitDate());
        checkTimeBadges(currentUser, c.getCommitDate());
        checkNumCommitBadges(currentUser);
        checkBadges27And28(currentUser, c.getCommitDate());
        checkBadge30(currentUser, c);
        checkMessageBadges(currentUser, c.getMessage());
    }

    /***
     * Checks for badge 16, grape squasher, 7 badges in a week
     * @param newBadges list of new badges to add to if obtained
     * @param data row information from the hbase
     * @param unixTime push time of the commits
     */
    public void checkBadge16(ArrayList<String> newBadges, Result data, String unixTime){
        ArrayList<String> badgeDates = HbaseTools.getBadgeDates(data);
        for(int i=0;i < newBadges.size();i++){
            badgeDates.add(unixTime);
        }
        Collections.sort(badgeDates);
        for(int i=0;(i+7) < badgeDates.size(); i++){
            int timeDiff = Integer.parseInt(badgeDates.get(i)) - Integer.parseInt(badgeDates.get(i+7));
            if(timeDiff < 7*24*60*60){
                    newBadges.add("16");
                    return;
            }
        }
    }

    /***
     * Checks for badges 26, jiver=jive in the commit message, 31 commit message is more than 20 words
     * @param user user to add badges too
     * @param message commit message
     */
    public void checkMessageBadges(UserInfo user, String message){
        if (message.toLowerCase().contains("jive") && !message.toLowerCase().contains("Merge branch")) {
            user.addBadge("26");
        }
        String[] words = message.split(" ");
        if (words.length > 20){
            user.addBadge("31");
        }
    }

    /***
     * checks for badge 30, committed person, a person commits 6 days in a row
     * @param user
     * @param c 
     */
    public void checkBadge30(UserInfo user, Commit c) {
        Calendar oldDate = user.getDate().getLocal();
        Calendar newDate = c.getCommitDate().getLocal();
        if(equals(oldDate, newDate)){
            return;
        }
        oldDate.add(Calendar.DAY_OF_MONTH, 1);
        if (equals(oldDate, newDate)){
            user.incrementConsecCommits();
            user.setDate(c.getCommitDate());
        } 
        else {
            user.resetConsecCommits();
            user.setDate(c.getCommitDate());
        }
        oldDate.add(Calendar.DAY_OF_MONTH, -1);
        if (user.getConsecCommits() > 6) {
            user.addBadge("30");
        } 
    }
    /***
     * checks to see if two calendar objects have the same date
     * @param x
     * @param y
     * @return boolean
     */
    public boolean equals(Calendar x, Calendar y){
        if (x.get(Calendar.DAY_OF_MONTH)==y.get(Calendar.DAY_OF_MONTH)) {
            if (x.get(Calendar.MONTH)==y.get(Calendar.MONTH)) {
                if (x.get(Calendar.YEAR)==y.get(Calendar.YEAR)) {
                    return true;
                }
            }   
        }
        return false;
    }
    /***
     * This method checks to see if there are commits twice
     * in a day, or greater than 5days badges 27, 28
     * 
     * @param user
     *            user object which contains the old date
     * @param newDate
     *            New commit date
     */
    public void checkBadges27And28(UserInfo user, JiveDate newDate) {
            long oldCommitTime = (user.getDate().getLocal().getTime().getTime())/1000;
            long timeDiff = (newDate.getLocal().getTime().getTime()/1000) - oldCommitTime;
            user.setDate(newDate);                
            if(user.getNumCommits()==1){
                return;
            }
            if (timeDiff < 24*60*60 ){
                user.addBadge("27");
            }
            if (timeDiff > (5*24*60*60)){
                user.addBadge("28");
            }
    }

    /***
     * checks for number of commit badges
     * 
     * @param user user object to check
     */
    public void checkNumCommitBadges(UserInfo user){
        int numCommits = user.getNumCommits();
        if (numCommits > 4999) {
            user.addBadge("5");
        } else if (numCommits > 999) {
            user.addBadge("4");
        } else if (numCommits > 499) {
            user.addBadge("3");
        } else if (numCommits > 49) {
            user.addBadge("2");
        } else if (numCommits > 0) {
            user.addBadge("1");
        }
    }

    /***
     * Check to see if commit date is a holiday
     * @param user
     * @param commitDate 
     */
    public void checkHolidayBadges(UserInfo user, JiveDate commitDate) {
            if (commitDate.equalsLocal(9, 31)) {
                    user.addBadge("6");
            } else if (commitDate.equalsLocal(2, 17)) {
                    user.addBadge("7");
            } else if (commitDate.equalsLocal(1, 29)) {
                    user.addBadge("8");
            } else if (commitDate.equalsLocal(1, 14)) {
                    user.addBadge("9");
            } else if (commitDate.equalsLocal(2, 14)) {
                    user.addBadge("10");
            }
    }

    /***
     * Check for day of the week and time of day badges, such as 12, night owl
     * @param user
     * @param commitDate 
     */
    public void checkTimeBadges(UserInfo user, JiveDate commitDate) {

            if (commitDate.getLocalHour() >=22) {
                user.addBadge("12");
            }
            else if (commitDate.getLocalHour() <=6) {
                user.addBadge("13");
            }

            if (commitDate.getLocalDay()==2 && commitDate.getLocalHour() <=5) {
                user.addBadge("18");
            } else if (commitDate.getLocalDay()==6 && commitDate.getLocalHour() >=16) {
                user.addBadge("19");
            } else if (commitDate.getLocalDay()==1 || commitDate.getLocalDay()==7) {
                user.addBadge("29");
            }
    }	
}
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


public final class BasicBadges {
    
    public BasicBadges() {};
    
    public BasicBadges(JSONArray jArrCommits, FileSystem hdfs, HTable table, 
                        String unixTime) throws JSONException, IOException, Exception {
    // iterate through and process/store info locally
        UserInfo user = null;
        Result data = null;
        // get user
        
        for(int i = 0;i < jArrCommits.length();i++){
            JSONObject jCommit = new JSONObject(jArrCommits.get(i).toString());
            Commit c = ServletTools.convertToCommit(jCommit, unixTime);
            //first iteration, create user object
            if(i==0){
                data = HbaseTools.getRowData(table, c.getEmail());
                if (data == null) {
                    return;
                }
                else if(data.isEmpty()){
                    return;
                }
                else {
                    user = new UserInfo(c.getEmail(), data, c.getCommitDate().getLocal());
                }
            }
            if(!HDFSTools.writeCommitToHDFS(hdfs, c)){
                continue;
            }
            user.incrementCommits();
            awardBasicBadges(user, c);
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

        //newBadgesString = newBadgesString.trim();


        HbaseTools.addRow(table,
            user.getEmail(),
            user.getLastCommit(),
            user.getNumBugs(),
            user.getNumCommits(),
            user.getConsecCommits(),
            (newBadgesString + badgeList[1]).trim(),
            newBadges.toArray(new String[newBadges.size()]),
            unixTime);
        //String jsonActivity = "{\"items\":[{\"title\": \"Bessie the cow was fed\",\"body\" : \"Fred Flintstone fed Bessie the Cow\"}]}";
        
        
        String[][] badgesList = {
            {"First Commit", "User committed 1 time."},
            {"Fifty Commits", "User committed 50 times."},
            {"Five Hundred Commits", "User committed 500 times,"},
            {"One Thousand Commits", "User committed 1000 times."},
            {"Five Thousand Commits", "User committed 5000 times."},
            {"Trick or Treat Badge", "User committed on Halloween."},
            {"Leprechaun Badge", "User committed on St. Patrick's Day."},
            {"Leap Year Badge", "User committed on Leap Year Day."},
            {"Love Badge", "User committed on Valentine's Day."},
            {"Pi Day", "User committed on 3.14"},
            {"Humming Bird", "User committed twice within a minute."},
            {"Night Owl", "User committed after 10pm."},
            {"Early Bird", "User committed before 6am."},
            {"Team Player", "User and 2 or more people made changes to the same directory in the same hour."},
            {"United We Stand", "User and 9 or more people made changes to the same directory in the same hour."},
            {"Grape Squasher", "User gained 8 or more badges in 1 week"},
            {"Numa Numa", "User had the least lines of code per file in a given week."},
            {"Manic Monday", "User committed on Monday before 8am."},
            {"Rebecca Black Friday", "User committed on Friday after 4pm."},
            {"Charlotte Takes Tumble", "User committed a negative amount of lines code in a day."},
            {"Boss is Better", "User committed more lines of code than all of his employees."},
            {"Good Employee", "User committed more lines of code than his boss."},
            {"Best Employee", "User committed more lines of code than each of his peers."},
            {"Best day", "User committed those most lines of code in a single day."},
            {"Soul mate(s)", "User and one or more people commit within a 10 second time span in same directory."},
            {"Jiver", "User's commit message included the word Jive."},
            {"Dos Commits", "User committed twice in the same day."},
            {"Slacker", "User had 5 or more days in between commits."},
            {"Weekend Warrior", "User committed on the weekend."},
            {"Committed Person", "User committed every day of a give week."},
            {"Author", "Users commit message was greater than 20 words."}
        };
        ArrayList<Badge> badges = new ArrayList<Badge>();
        for(String s : newBadges){
            Badge badge = new Badge(badgesList[Integer.parseInt(s)-1][0], badgesList[Integer.parseInt(s)-1][1], s+".png");
            badges.add(badge);
        }
        
        String jsonActivity = ServletTools.makeJSONPost(badges, user.getName());
        System.out.println("JSON:   " +jsonActivity);
        //jsonActivity = "{\"items\":[{\"title\": \"Bessie the cow was fed\",\"body\" : \"Fred Flintstone fed Bessie the Cow\"}]}";
        //System.out.println("TEST JSON:   " +jsonActivity);
        ActivityPoster.postToActivity(user.getId(), null, jsonActivity);
    }

	
	/***
	 * This method takes various information from a recent commit, and uses it
	 * to check for basic badges. It updates the row in the HBase with the
	 * appropriate badges, and information.
	 * 
	 * @param table
	 *            connection to Hbase
	 * @param c
	 *            commit object
	 * @param numBugs
	 *            number of bugs the user fixed, currently not in use
	 */
	public void awardBasicBadges(UserInfo currentUser, Commit c) {
            checkHolidayBadges(currentUser, c.getCommitDate());
            checkTimeBadges(currentUser, c.getCommitDate());
            checkNumCommitBadges(currentUser);
            checkBadges27And28(currentUser, c.getCommitDate());
            checkBadge30(currentUser, c);
            checkMessageBadges(currentUser, c.getMessage());
        }

        // 16
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

        // 26
        public void checkMessageBadges(UserInfo user, String message){
            if (message.toLowerCase().contains("jive")) {
			user.addBadge("26");
            }
        }
        
        //30
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
            if (user.getConsecCommits() > 5) { // it is 5 (not 6) because conseccommits starts at 0
                user.addBadge("30");
            } 
        }
        
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
        // 27, 28
	/***
	 * This method checks to see if there are commits on consecutive days, twice
	 * in a day, or greater than 5days
	 * 
	 * @param currentUser
	 *            New commit date
	 * @param newDate
	 *            Previous commit date
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
	
         // 1, 2, 3, 4, 5
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
        
        // 6, 7, 8, 9, 10
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
        
        // 12, 13, 18, 19, 29
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


//		// bug stuff is not currently implemented, but is being checked for here
//		if (totNumBugs > 100) {
//			badges.add("35");
//		} else if (totNumBugs > 50) {
//			badges.add("34");
//		} else if (totNumBugs > 25) {
//			badges.add("33");
//		} else if (totNumBugs > 10) {
//			badges.add("32");
//		} else if (totNumBugs > 1) {
//			badges.add("31");
//		}


//		Object[] badgeList = HbaseTools.getBadges(data);
//		@SuppressWarnings("unchecked")
//		ArrayList<String> aquiredBadges = (ArrayList<String>) badgeList[0];
//		for (int i = 0; i < badges.size(); i++) {
//			if (aquiredBadges.contains(badges.get(i))) {
//				badges.remove(i--);
//			}
//		}
//		// checks to see if there are more than 7 new badges in the week
//		// ???
//		if ((badges.size() + fieldValues[0]) > 7) {
//			if(!aquiredBadges.contains("16")){
//				badges.add("16");
//			} CURRENTLY NOT IMPLEMENTED!!!
//		}
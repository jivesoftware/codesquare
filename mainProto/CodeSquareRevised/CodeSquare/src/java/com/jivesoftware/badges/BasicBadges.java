package com.jivesoftware.badges;

import java.util.ArrayList;
import java.util.Date;
import org.apache.hadoop.fs.FileSystem;

import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;

import com.jivesoftware.toolbox.HbaseTools;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class BasicBadges {
    
    HashMap<String, UserInfo> users = new HashMap<String, UserInfo>();
    
    public BasicBadges(JSONArray jArrCommits, FileSystem hdfs, HTable table, 
                        String unixTime, String timeZone) throws JSONException, IOException {
        // iterate through and process/store info locally
        for(int i = 0;i < jArrCommits.length();i++){
            JSONObject jCommit = new JSONObject(jArrCommits.get(i).toString());
            System.out.println(jCommit.toString());
            Commit c = ServletTools.convertToCommit(jCommit, unixTime, timeZone);
            HDFSTools.writeCommitToHDFS(hdfs, c); 
            System.out.println("BEFORE AWARDS");
            awardBasicBadges(table, c); 
	}
        
        System.out.println("BEFORE FORLOOP" + users.size());
        // iterate through users and add to hbase
        for (Map.Entry<String, UserInfo> entry : users.entrySet()) {
            System.out.println("INSIDE FORLOOP: "+entry.getValue().getBadges().toString());
            ArrayList<String> arrBadges = entry.getValue().getBadges();
            ArrayList<String> newBadges = entry.getValue().getBadges();
            System.out.println("INSIDE FORLOOP1: "+entry.getKey());
            Result data = HbaseTools.getRowData(table, entry.getKey());
            System.out.println("INSIDE FORLOOP2: "+data);
            Object[] badgeList = HbaseTools.getBadges(data);
            System.out.println("INSIDE FORLOOP3: "+badgeList.toString());
            System.out.println("INSIDE FORLOOP4: "+badgeList[0].toString());
            ArrayList<String> oldBadges = (ArrayList<String>) badgeList[0];
            System.out.println("INSIDE FORLOOP5: "+oldBadges.toString());
            for (int i = 0; i < newBadges.size(); i++) {
                System.out.println("INSIDE FORLOOP6: "+oldBadges.contains(newBadges.get(i)));
                if (oldBadges.contains(newBadges.get(i))) {
                    newBadges.remove(i--);
		}
            }
            System.out.println("INSIDE FORLOOP7: "+newBadges.toString());
            HbaseTools.addRow(table, 
                entry.getKey(), 
                entry.getValue().getLastCommit(), 
                entry.getValue().getBadgesWeek(),
		entry.getValue().getNumBugs(), 
                entry.getValue().getNumCommits(), 
                entry.getValue().getConsecCommits(), 
                newBadges.toString(),
		arrBadges.toArray(new String[arrBadges.size()]));
            System.out.println("INSIDE FORLOOP8: "+entry.getKey());
            System.out.println("INSIDE FORLOOP9: "+entry.getValue().getLastCommit());
            System.out.println("INSIDE FORLOOP10: "+entry.getValue().getBadgesWeek());
            System.out.println("INSIDE FORLOOP11: "+entry.getValue().getNumBugs());
            System.out.println("INSIDE FORLOOP12: "+entry.getValue().getNumCommits());
            System.out.println("INSIDE FORLOOP13: "+entry.getValue().getConsecCommits());
            System.out.println("INSIDE FORLOOP14: "+newBadges.toString());
            System.out.println("INSIDE FORLOOP15: "+arrBadges.toArray(new String[arrBadges.size()]));
            }
            
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
	public void awardBasicBadges(HTable table, Commit c) {
            System.out.println("IN AWARDS"+c.getEmail());
            // get user
            UserInfo currentUser = null;
            System.out.println("IF1 AWARDS: "+users.containsKey(c.getEmail()));
            if (users.containsKey(c.getEmail())){
                currentUser = users.get(c.getEmail());
            }
            else {
                Result data = HbaseTools.getRowData(table, c.getEmail());
                System.out.println("IF2 AWARDS: "+(data == null));
		if (data == null) { return;}
                else {
                    currentUser = new UserInfo(c.getEmail(), data, c.getCommitDate().getLocal());
                    users.put(c.getEmail(), currrentUser)
                }
            }
            
            System.out.println("CheckBadges"+currentUser.getEmail());
            
            // check badges
            checkHolidayBadges(currentUser, c.getCommitDate());
            System.out.println("Badges1");
            checkTimeBadges(currentUser, c.getCommitDate());
            System.out.println("Badges2");
            checkNumCommitBadges(currentUser);
            System.out.println("Badges3");
            checkBadges27And28(currentUser, c.getCommitDate()); 
            System.out.println("Badges4");
            checkBadge30(currentUser, c); 
            System.out.println("Badges5");
            checkMessageBadges(currentUser, c.getMessage());
            System.out.println("Badges6");
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
            oldDate.add(Calendar.DAY_OF_MONTH, 1);
            Calendar newDate = c.getCommitDate().getLocal();
            if (equals(oldDate, newDate)){
                user.incrementConsecCommits();
            } 
            oldDate.add(Calendar.DAY_OF_MONTH, -1);
            if (user.getConsecCommits() > 6) {
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
                long oldCommitTime = user.getLastCommitTime();
                long timeDiff = newDate.getLocal().getTime().getTime() - oldCommitTime;
                if (timeDiff < 24*60*60 ){
                    user.addBadge("27");
                }
                if (timeDiff > (5*24*60*60) ){
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
                if (commitDate.equalsLocal(10, 31)) {
			user.addBadge("6");
		} else if (commitDate.equalsLocal(3, 17)) {
			user.addBadge("7");
		} else if (commitDate.equalsLocal(2, 29)) {
			user.addBadge("8");
		} else if (commitDate.equalsLocal(2, 14)) {
			user.addBadge("9");
		} else if (commitDate.equalsLocal(3, 14)) {
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
		if (commitDate.getLocalDay()==1 && commitDate.getLocalHour() <=5) {
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
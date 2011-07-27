package com.jivesoftware.backendServlet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * An abstraction used to simplify and organize the access of instance variables
 * related to the fields in the Commit table
 * 
 * @author diivanand.ramalingam
 * 
 */
public class Commit {
	private String id;
	private String msg;
	private String email;

	private JiveDate commitDate;
        private JiveDate pushDate;

	private int numFilesChanged;
	private ArrayList<String> filesChanged;
	private int insertions;
	private int deletions;

	/**
	 * Constructs an empty commit object
	 * 
	 */
	public Commit() {
            filesChanged = new ArrayList<String>();
	}

	/**
	 * Constructs a commit object
	 * 
	 * @param id
	 *            - commit it
	 * @param msg
	 *            - message
	 * @param numFilesChanged
	 *            @param filesChanged
	 * @param insertions
	 *            @param deletions
	 * @param day
	 *            @param date @param hour
	 * @param minute
	 *            @param second
	 */
	public Commit(String id, String email, String msg, int numFilesChanged,
			ArrayList<String> filesChanged, int insertions, int deletions,
			JiveDate commitDate, JiveDate pushDate) {
		this.id = id;
		this.msg = msg;
		this.numFilesChanged = numFilesChanged;
		this.email = email;
		this.filesChanged = filesChanged;
		this.insertions = insertions;
		this.deletions = deletions;
		this.commitDate = commitDate;
		this.pushDate = pushDate;
	}
        
	/**
	 * 
	 * @return commit date
	 */
	public JiveDate getCommitDate() {
		return commitDate;
	}

	/**
	 * 
	 * set commit date
	 */
	public void setCommitDate(JiveDate commitDate) {
		this.commitDate = commitDate;
	}

	/**
	 * 
	 * @return push date
	 */
	public JiveDate getPushDate() {
		return pushDate;
	}

	/**
	 * 
	 * set push date
	 */
	public void setPushDate(JiveDate pushDate) {
		this.pushDate = pushDate;
	}

	/**
	 * 
	 * @return commit id number
	 */
	public String getId() {
		return id;
	}

	/**
	 * 
	 * set commit id number
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 
	 * @return the email of the employee who made this commit
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * 
	 * set Email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * 
	 * @return number of files changed
	 */
	public int getNumFilesChanged() {
		return numFilesChanged;
	}

	/**
	 * 
	 * set number of files changed
	 */
	public void setNumFilesChanged(int numFilesChanged) {
		this.numFilesChanged = numFilesChanged;
	}

	/**
	 * 
	 * @return number of lines inserted
	 */
	public int getInsertions() {
		return insertions;
	}

	/**
	 * 
	 * set number of lines inserted
	 */
	public void setInsertions(int insertions) {
		this.insertions = insertions;
	}

	/**
	 * 
	 * @return number of lines deleted
	 */
	public int getDeletions() {
		return deletions;
	}

	/**
	 * 
	 * set number of lines deleted
	 */
	public void setDeletions(int deletions) {
		this.deletions = deletions;
	}

	/**
	 * 
	 * @return list of files changed
	 */
	public ArrayList<String> getFilesChanged() {
		return filesChanged;
	}

	/**
	 * 
	 * set list of files changed
	 */
	public void setFilesChanged(ArrayList<String> filesChanged) {
		this.filesChanged = filesChanged;
	}

	/**
	 * 
	 * increment num of files changed
	 */
	public void incrementNumFilesChanged() {
		this.numFilesChanged = numFilesChanged + 1;
	}

	/**
	 * 
	 * append file to list of files changed
	 */
	public void appendFilesChanged(String file) {
		this.filesChanged.add(file);
	}

	/**
	 * 
	 * clear the list of files changed
	 */
	public void clearFilesChanged(String file) {
		this.filesChanged.clear();
	}

	/**
	 * 
	 * @return the message associated with this commit
	 */
	public String getMessage() {
		return msg;
	}

	/**
	 * 
	 * set commit message
	 */
	public void setMessage(String message) {
		this.msg = message;
	}

	/**
	 * add stats to Commit - includes insertions, deletions, numFileChanged, and
	 * filesChanged
	 * 
	 * @param statString
	 *            A String of statistical commit information to be parsed
	 * @return an Object array of commit statistical info
	 */
	public void addStats(String stats) {
            
            
                System.out.println("Entered addStats: " + stats);
		stats = stats.replace('[', ' ').replace(']', ' ').trim();
                System.out.println("Stats after replace and trim to be tokenized: " + stats);
                int totalInsertions, totalDeletions;
                totalInsertions = totalDeletions = 0;
                
                
		StringTokenizer st = new StringTokenizer(stats, " ");
                
                
                int counter = 1;
                
                while(st.hasMoreTokens()){
                    String token = st.nextToken();
                    System.out.println("Token: " + token);
                    System.out.println(token);
                    if(counter % 3 == 2){
                        totalInsertions += Integer.parseInt(token);
                    }else if(counter % 3 == 1){
                        totalDeletions += Integer.parseInt(token);
                    }else{//counter % 3 == 0
                        incrementNumFilesChanged();
                        appendFilesChanged(token);
                    }
                    counter++;
                }
                
                
		insertions = totalInsertions;
		deletions = totalDeletions;
		
                System.out.println("Leaving addStats");
                
                
               
                
	}

	public String filesToString() {
		@SuppressWarnings("rawtypes")
		Iterator iterator = filesChanged.iterator();
                String str = "";
                if(iterator.hasNext()){
                    str = iterator.next().toString();
                }
		
		while (iterator.hasNext()) {
			str = str + "," + iterator.next().toString();
		}
		return str;
	}

	public String toString() {
		return id + " " + email + " " + pushDate.getGlobal().getTimeZone().getID() + " "
				+ numFilesChanged + " [" + filesToString() + "] " + insertions
				+ " " + deletions + " " + "\"" + msg + "\"";
	}

}
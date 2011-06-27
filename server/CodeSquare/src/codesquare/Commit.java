package codesquare;

import java.util.ArrayList;
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
	
	private String day;
	private String date;
	private int hour;
	private int minute;
	private int second;
	
	private int numFilesChanged;
	private ArrayList<String> filesChanged;
	private int insertions;
	private int deletions;

	/**
	 * Constructs an empty commit object
	 * 
	 */
	public Commit() {
	}
	
	/**
	 * Constructs a commit object
	 * 
	 * @param id - commit it
	 * @param msg - message
	 * @param numFilesChanged @param filesChanged
	 * @param insertions @param deletions
	 * @param day @param date @param hour 
	 * @param minute @param second
	 */
	public Commit(String id, String msg, int numFilesChanged, 
			String fileString, int insertions, int deletions,
			String day, String date, int hour, int minute, int second ) {
		this.id = id; this.msg = msg; this.numFilesChanged = numFilesChanged;
		this.filesChanged = parseFilesChanged(fileString);
		this.insertions = insertions; this.deletions = deletions;
		this.day = day; this.date = date; this.hour = hour;
		this.minute = minute; this.second = second;
	}
	
	/**
	 * 
	 * @return commit id number
	 */
	public String getId() {
		return id; }
	
	/**
	 * 
	 * set commit id number
	 */
	public void setId(String id) {
		this.id = id; }

	/**
	 * 
	 * @return commit day
	 */
	public String getDay() {
		return day; }
	
	/**
	 * 
	 * set commit day
	 */
	public void setDay(String day) {
		this.day = day; }
	
	/**
	 * 
	 * @return commit date
	 */
	public String getDate() {
		return date; }
	
	/**
	 * 
	 * set commit date
	 */
	public void setDate(String date) {
		this.date = date; }	
	
	/**
	 * 
	 * @return commit hour
	 */
	public int getHour() {
		return hour; }
	
	/**
	 * 
	 * set commit hour
	 */
	public void setHour(int hour) {
		this.hour = hour; }

	/**
	 * 
	 * @return commit minute
	 */
	public int getMinute() {
		return minute; }
	
	/**
	 * 
	 * set commit minute
	 */
	public void setMinute(int minute) {
		this.minute = minute; }
	
	/**
	 * 
	 * @return commit second
	 */
	public int getSecond() {
		return second; }
	
	/**
	 * 
	 * set commit second
	 */
	public void setSecond(int second) {
		this.second = second; }
	
	/**
	 * 
	 * @return number of files changed
	 */
	public int getNumFilesChanged() {
		return numFilesChanged; }
	
	/**
	 * 
	 * set number of files changed
	 */
	public void setNumFilesChanged(int numFilesChanged) {
		this.numFilesChanged = numFilesChanged; }

	/**
	 * 
	 * @return number of lines inserted
	 */
	public int getInsertions() {
		return insertions; }
	
	/**
	 * 
	 * set number of lines inserted
	 */
	public void setInsertions(int insertions) {
		this.insertions = insertions; }
	
	/**
	 * 
	 * @return number of lines deleted
	 */
	public int getDeletions() {
		return deletions; }
	
	/**
	 * 
	 * set number of lines deleted
	 */
	public void setDeletions(int deletions) {
		this.deletions = deletions; }
	
	/**
	 * 
	 * @return list of files changed
	 */
	public ArrayList<String> getFilesChanged() {
		return filesChanged; }
	
	/**
	 * 
	 * set list of files changed
	 */
	public void setFilesChanged(ArrayList<String> filesChanged) {
		this.filesChanged = filesChanged; }
	
	/**
	 * 
	 * append file to list of files changed
	 */
	public void appendFilesChanged(String file) {
		this.filesChanged.add(file); }

	/**
	 * 
	 * clear the list of files changed
	 */
	public void clearFilesChanged(String file) {
		this. filesChanged.clear();}
	
	
	private ArrayList<String> parseFilesChanged(String filesChanged) {
		ArrayList<String> toBeFilled = new ArrayList<String>();
		StringTokenizer s = new StringTokenizer(filesChanged, " ");
		while (s.hasMoreElements()) {
			toBeFilled.add(s.nextToken());
		}
		return toBeFilled;
	}
	
	public String toString(){
		return id + " " + msg + " " + day + " " + date + " " + hour + " " + minute
		 + " " + second + " " + numFilesChanged + " " + filesChanged.toString() + " " + insertions
		 + " " + deletions;
	}
}

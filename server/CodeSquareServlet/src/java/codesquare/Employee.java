package codesquare;

import java.util.ArrayList;

/**
 * An abstraction to simplify the Commit abstraction
 * 
 * @author diivanand.ramalingam
 * 
 */
public class Employee {
	private int id;
	private String fname;
	private String lname;
	private String email;
	private ArrayList<Commit> commits;

	/**
	 * Constructs an empty employee object
	 * 
	 */
	public Employee() {
	}

	/**
	 * Constructs an employee object
	 * 
	 */
	public Employee(int id, String fname, String lname, String email) {
		this.id = id;
		this.fname = fname;
		this.lname = lname;
		this.email = email;
		commits = new ArrayList<Commit>();
	}
	
	/**
	 * 
	 * @return employee id number
	 */
	public int getId() {
		return id; }
	
	/**
	 * 
	 * set employee id number
	 */
	public void setId(int id) {
		this.id = id; }
	
	/**
	 * 
	 * @return first name
	 */
	public String getFname() {
		return fname; }
	
	/**
	 * 
	 * set first name
	 */
	public void setFname(String fname) {
		this.fname = fname; }
	
	/**
	 * 
	 * @return last name
	 */
	public String getLname() {
		return lname; }
	
	/**
	 * 
	 * set last name
	 */
	public void setLname(String lname) {
		this.lname = lname; }
	
	/**
	 * 
	 * @return email address
	 */
	public String getEmail() {
		return email; }
	
	/**
	 * 
	 * set email address
	 */
	public void setEmail(String email) {
		this.email = email; }
	
	/**
	 * 
	 * @return array list of author's commits
	 */
	public ArrayList<Commit> getCommits() {
		return commits; }
	
	/**
	 * 
	 * append commit to array list of author's commits
	 */
	public void appendCommit(Commit commit) {
		this.commits.add(commit); }
	
	/**
	 * 
	 * clear author's commits
	 */
	public void clearCommits() {
		this.commits.clear(); }
	
	/**
	 * 
	 * remove commit from array list of author's commits
	 */
	public void removeCommit(Commit commit) {
		this.commits.remove(commit); }
	
	public String toString(){
		return fname + " " + lname + " " + email;
	}
}
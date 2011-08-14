package codesquare.badges.badge_21_22_23;

public class Employee {
	
	private String id = "";
	private int LOC = 0;
	
	public Employee(String id, int LOC){
		this.id = id;
		this.LOC = LOC;
	}
	
	public String getId() {
		return id;
	}
	
	public int getLOC() {
		return LOC;
	}
	
	public String toString() {
		return id+" "+LOC;
	}

}

package codesquare;
import java.io.File;

/**
 * Class to be run weekly for badges 21, 22, and 23
 * 
 * returned file format is {empId} {badge#}
 * where "{" and "}" are not expressed
 * ie. 4825 24 
 * 
 * accepts a directory - searches for all files recursively
 * 
 * 14 - 2 ppl, 1 hour, 1 file
 * 15 - 9 ppl, 1 hour, 1 file
 * 
 * @author deanna.surma
 * 
 */
public class HourlySet {
	public static void main(String[] args) throws Exception {
		new LOC14_15(args[0], args[1]);
		System.out.println("LOC14_15 FINISHED!!!");
	}
}


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
 * 21 - peer badge - employee commits more LOC than all of his peers
 * 22 - employee badge - employee commits more LOC than his boss
 * 23 - boss badge - boss commits more LOC than all of his employees
 * 
 * @author deanna.surma
 * 
 */
public class WeeklySet {
	public static void main(String[] args) throws Exception {
		String output1 = Toolbox.generateString();
		String output2 = Toolbox.generateString();
		new LOC1(args[0], output1);
		System.out.println("LOC1 FINISHED!!!");
		new LOC2(output1, output2);
		System.out.println("LOC2 FINISHED!!!");
		new LOC3(output2, args[1]);
		System.out.println("LOC3 FINISHED!!!");
		Toolbox.deleteDirectory(new File(output1));
		Toolbox.deleteDirectory(new File(output2));	
	}
}


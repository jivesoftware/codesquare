package codesquare.badges;
import java.io.File;

import codesquare.Toolbox;
import codesquare.badges.badge_21_22_23.Pass2;
import codesquare.badges.badge_21_22_23.Pass3;
import codesquare.badges.sharedpasses.LOC;

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
public class Badge_21_22_23 {
	public static void main(String[] args) throws Exception {
		String output1 = Toolbox.generateString();
		String output2 = Toolbox.generateString();
		new LOC(args[0], output1);
		System.out.println("LOC1 FINISHED!!!");
		new Pass2(output1, output2);
		System.out.println("LOC2 FINISHED!!!");
		new Pass3(output2, args[1]);
		System.out.println("LOC3 FINISHED!!!");
		Toolbox.deleteDirectory(new File(output1));
		Toolbox.deleteDirectory(new File(output2));	
	}
}


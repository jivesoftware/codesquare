package codesquare;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

/**
 * Class to be run daily for badges 20 and 24
 * 
 * returned file format is {empId} {badge#}
 * where "{" and "}" are not expressed
 * ie. 4825 24 
 * 
 * accepts a directory - searches for all files recursively
 * 
 * 20 - negative LOC badge - employee commits more LOC than his boss
 * 24 - largest LOC badge - employee commits more LOC than all of his peers
 * 
 * @author deanna.surma
 * 
 */
public class DailySet {	
	public static void main(String[] args) throws Exception {
		String output1 = Toolbox.generateString();
		new LOC1(args[0], output1);
		System.out.println("LOC1 FINISHED!!!");
		LOC5 x = new LOC5(output1, args[1]);
		Writer output = null;
		File file = new File("maxEmp.txt");
		output = new BufferedWriter(new FileWriter(file));
		output.write(x.getMaxEmp()+" 24");
		output.close();
	}
}
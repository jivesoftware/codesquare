package hdfs;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HDFS {
	
	public static void main(String[] args){
		
		//Creating FileSystem Object
		Configuration config = new Configuration();
		config.set("fs.default.name","hdfs://10.45.111.143:8020");
		try {
			FileSystem dfs = FileSystem.get(config);
			//Creating Main Directory
			String dirName = "Input";
			Path src = new Path("/"+dirName);
			dfs.mkdirs(src);
			
			
			//Creating Sub Directory
			String subDirName = "Commits";
		    src = new Path("/Input"+subDirName);
			dfs.mkdirs(src);
			
			//Creating a file in HDFS
			src = new Path("/Input/Commits/2js6ul785h.txt");
			dfs.createNewFile(src);
			
			//Writing to a file in HDFS
			
			src = new Path("/Input/Commits/2js6ul785h.txt");
			FileInputStream fis = new FileInputStream("src/2js6ul785h.txt");
			int len = fis.available();
			byte[] btr = new byte[len];
			fis.read(btr);
			FSDataOutputStream fs = dfs.create(src);
			fs.write("hello".getBytes());
			fs.close();
			
			
			//Reading data from a file in HDFS
			src = new Path("/Input/Commits/2js6ul785h.txt");
			FSDataInputStream fsd = dfs.open(src);
			BufferedReader br = new BufferedReader(new InputStreamReader(fsd));
			String str = br.readLine();
			while((str != null)){
				System.out.println(str);
				str = br.readLine();
			}
			
			//Miscellaneous Operations
			src = new Path("/TestDirectory/subDirectory/file2.txt");
			//existStatement(dfs,src);
			src = new Path("/TestDirectory/subDirectory/input.txt");
			//existStatement(dfs,src);
			//System.out.println(dfs.getDefaultBlockSize() + " bytes is the default block size"); //see the block size in terms of Number of Bytes
			
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		
		
	}
	
	private static void existStatement(FileSystem dfs, Path src){
		try {
			if(dfs.exists(src)){
				System.out.println("File "+src+" exists in HDFS");
			}else{
				System.out.println("File "+src+" does not exist in HDFS");
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

}


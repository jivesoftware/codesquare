package BackEnd;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.login.LoginContext;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class parses a string containing git log commit info sent by a Jenkins
 * script and adds each newly parsed commit as it's own .txt file in HDFS under
 * the appropriate year, month, day, and hour folders (each a sub-folder of the
 * one before it).
 * 
 * @author diivanand.ramalingam
 */
public class BackEndJar {

	/**
	 * Parses the git input sent by the Jenkins-run shell script git commands
	 * 
	 * @param filename
	 *            the name of the file containing all the commits
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<Commit> parseGitInput(String filename) {
		// initialize local variables
		ArrayList<Commit> commits = new ArrayList<Commit>();
		ArrayList<JSONObject> jObs = getCommitsFromFile(new File(filename));

		String commId, email, day, date, msg;
		int hour, minute, second, numFilesChanged, insertions, deletions;
		ArrayList<String> filesChanged;

		// Create Commit objects from JSON and write the Commits to HDFS
		for (JSONObject j : jObs) {
			try {
				// Retrieve JSON values from Commit JSON Object
				String dateString = j.getString("date");
				String statString = j.getString("stats");
				commId = j.getString("cID");
				email = j.getString("email");
				msg = j.getString("cMes");

				// Obtain and store relevant parts of the date and stat strings
				Object[] dateStuff = parseDate(dateString);
				Object[] stats = parseStats(statString);

				// Store date related info into local variables for convenience
				// in testing
				date = (String) dateStuff[0];
				hour = (Integer) dateStuff[1];
				minute = (Integer) dateStuff[2];
				second = (Integer) dateStuff[3];

				// Store stats related info into local variables for convenience
				// in testing
				numFilesChanged = (Integer) stats[3];
				filesChanged = (ArrayList<String>) stats[2];
				insertions = (Integer) stats[0];
				deletions = (Integer) stats[1];
				day = (String) dateStuff[4];

				// Create a Commit Object based on the local variables and then
				// add the object to the Commit ArrayList to be returned by the
				// method
				Commit c = new Commit(commId, email, msg, numFilesChanged,
						filesChanged, insertions, deletions, day, date, hour,
						minute, second);
				commits.add(c);
				// System.out.println(c);
			} catch (JSONException ex) {
				Logger.getLogger(BackEndJar.class.getName()).log(Level.SEVERE,
						null, ex);
			}
		}

		return commits;
	}

	/**
	 * Object[0] = total number of insertions (int), Object[1] = total number of
	 * deletions (int) Object[2] = String ArrayList whose elements are Strings
	 * contains the names of files changed by a commit, Object[3] = the total
	 * number of files changed (int)
	 * 
	 * @param statString
	 *            A String of statistical commit information to be parsed
	 * @return an Object array of commit statistical info
	 */
	private static Object[] parseStats(String statString) {
		statString = statString.replace('[', ' ');
		statString = statString.replace(']', ' ');
		statString = statString.trim();

		Object[] tmp = new Object[4];
		int totalInsertions = 0;
		int totalDeletions = 0;
		int totalNumFilesChanged = 0;
		ArrayList<String> filesChanged = new ArrayList<String>();

		ArrayList<String> toBeParsed = new ArrayList<String>();

		StringTokenizer st = new StringTokenizer(statString, " ");
		while (st.hasMoreTokens()) {
			toBeParsed.add(st.nextToken());
		}

		for (int i = 0; i < toBeParsed.size(); i = i + 3) {
			totalInsertions += Integer.parseInt(toBeParsed.get(i));
			totalDeletions += Integer.parseInt(toBeParsed.get(i + 1));
			filesChanged.add(toBeParsed.get(i + 2));
			totalNumFilesChanged++;
		}

		tmp[0] = totalInsertions;
		tmp[1] = totalDeletions;
		tmp[2] = filesChanged;
		tmp[3] = totalNumFilesChanged;

		return tmp;
	}

	/**
	 * Object[0] = date (String), Object[1] = hour (int), Object[2] = minute
	 * (int), Object[3] = second (int), Object[4] = dayOfWeek (String)
	 * 
	 * @param dateString
	 *            A String containing date information to be parsed
	 * @return an Object[] containing date information
	 */
	private static Object[] parseDate(String dateString) {
		Object[] tmp = new Object[5];
		dateString = dateString.trim();
		StringTokenizer st = new StringTokenizer(dateString, " ");
		String dayOfWeek = st.nextToken();
		int month = parseMonth(st.nextToken());
		int dayOfMonth = Integer.parseInt(st.nextToken());
		int[] times = parseTime(st.nextToken());
		int hour = times[0];
		int min = times[1];
		int sec = times[2];
		int year = Integer.parseInt(st.nextToken());

		String date = year + "-" + month + "-" + dayOfMonth;

		tmp[0] = date;
		tmp[1] = hour;
		tmp[2] = min;
		tmp[3] = sec;
		tmp[4] = dayOfWeek;
		return tmp;
	}

	/**
	 * int[0] = hour, int[1] = minute, int[2] = second
	 * 
	 * @param timeString
	 * @return an int array containing the int values of the hour, minute,
	 *         second
	 */
	private static int[] parseTime(String timeString) {
		timeString = timeString.trim();
		StringTokenizer st = new StringTokenizer(timeString, ":");
		int[] times = new int[3];
		times[0] = Integer.parseInt(st.nextToken()); // hour
		times[1] = Integer.parseInt(st.nextToken()); // minute
		times[2] = Integer.parseInt(st.nextToken()); // second
		return times;
	}

	/**
	 * 
	 * @param month
	 *            the common three-letter month abbreviation
	 * @return the numeric month value (1 = January, 12 = December)
	 */
	private static int parseMonth(String month) {
		if (month.equalsIgnoreCase("jan")) {
			return 1;
		} else if (month.equalsIgnoreCase("feb")) {
			return 2;
		} else if (month.equalsIgnoreCase("mar")) {
			return 3;
		} else if (month.equalsIgnoreCase("apr")) {
			return 4;
		} else if (month.equalsIgnoreCase("may")) {
			return 5;
		} else if (month.equalsIgnoreCase("jun")) {
			return 6;
		} else if (month.equalsIgnoreCase("jul")) {
			return 7;
		} else if (month.equalsIgnoreCase("aug")) {
			return 8;
		} else if (month.equalsIgnoreCase("sep")) {
			return 9;
		} else if (month.equalsIgnoreCase("oct")) {
			return 10;
		} else if (month.equalsIgnoreCase("nov")) {
			return 11;
		} else if (month.equalsIgnoreCase("dec")) {
			return 12;
		} else {
			return -1;
		}
	}

	// End area that contains methods which should be consolidated
	/**
	 * 
	 * @param upc
	 *            un-parsed-commits in JSON format
	 * @return an ArrayList of JSON objects that contain commit info
	 */
	private static ArrayList<JSONObject> getCommits(ArrayList<String> upc) {
		ArrayList<JSONObject> jObs = new ArrayList<JSONObject>();
		for (String s : upc) {
			try {
				JSONObject j = new JSONObject(s);
				jObs.add(j);
			} catch (JSONException e) {
				System.err.println(e.getMessage());
			}
		}
		return jObs;
	}

	// HDFS methods
	/**
	 * inserts the commit objects as String into .txt files
	 * 
	 * @param commits
	 *            An arrayList of Commit objects
	 */
	public static void insertCommitsIntoHDFS(ArrayList<Commit> commits) {
		try {
			// Connect and open HDFS and set path variables
			Configuration config = new Configuration();

			config.set("fs.default.name",
					"hdfs://hadoopdev001.eng.jiveland.com:54310");
			config.set("hadoop.log.dir", "/hadoop001/data/hadoop/logs");
			config.set("hadoop.tmp.dir", "/hadoop001/tmp");
			config.set("io.file.buffer.size", "131072");
			config.set("fs.inmemory.size.mb", "200");
			config.set("fs.checkpoint.period", "900");

			config.set("dfs.datanode.max.xceivers", "4096");
			config.set("dfs.block.size", "134217728");
			config.set(
					"dfs.name.dir",
					"/hadoop001/data/datanode,/hadoop002/data/datanode,/hadoop003/data/datanode,/hadoop004/data/datanode,/hadoop005/data/datanode,/hadoop006/data/datanode,/hadoop007/data/datanode,/hadoop008/data/datanode,/hadoop009/data/datanode,/hadoop010/data/datanode,/hadoop011/data/datanode,/hadoop012/data/datanode");
			config.set("dfs.umaskmode", "007");
			config.set("dfs.datanode.du.reserved", "107374182400");
			config.set("dfs.datanode.du.pct", "0.85f");

			config.set("dfs.permissions", "false");
			
			FileSystem dfs = FileSystem.get(config);

			String pathString = "/user/interns/Commits";
			Path src = new Path(pathString);

			if (!dfs.exists(src)) {
				dfs.mkdirs(src);
			}

			for (Commit c : commits) {
				// Parse Date String and store in local variables
				StringTokenizer st = new StringTokenizer(c.getDate(), "-");
				Integer year = Integer.parseInt(st.nextToken());
				Integer month = Integer.parseInt(st.nextToken());
				Integer dayOfMonth = Integer.parseInt(st.nextToken());
				Integer hour = new Integer(c.getHour());
				Integer minute = new Integer(c.getMinute());

				// If the year folder doesn't exist, create it
				src = extendPath(src, year.toString());
				if (!dfs.exists(src)) {
					dfs.mkdirs(src);
					System.out.println(src.toString() + " " + dfs.exists(src));
				}

				// If the month folder doesn't exist, create it
				src = extendPath(src, month.toString());
				if (!dfs.exists(src)) {
					dfs.mkdirs(src);
					System.out.println(src.toString() + " " + dfs.exists(src));
				}

				// If the dayOfMonth folder doesn't exist, create it
				src = extendPath(src, dayOfMonth.toString());
				if (!dfs.exists(src)) {
					dfs.mkdirs(src);
					System.out.println(src.toString() + " " + dfs.exists(src));
				}

				// If the hour of the day folder doesn't exist, create it
				src = extendPath(src, hour.toString());
				if (!dfs.exists(src)) {
					dfs.mkdirs(src);
					System.out.println(src.toString() + " " + dfs.exists(src));
				}

				// If the minute of the hour folder doesn't exist, create it
				src = extendPath(src, minute.toString());
				if (!dfs.exists(src)) {
					dfs.mkdirs(src);
					System.out.println(src.toString() + " " + dfs.exists(src));
				}

				// Write file
				Path src2 = new Path(src.toString() + "/" + c.getId() + ".txt");

				
				if (dfs.exists(src2)) {
					System.out.println(src2 + " already exists, deleting it");
					dfs.delete(src2, false);
				}
				

				FSDataOutputStream fs = dfs.create(src2);
				fs.write(c.toString().getBytes());
				FileStatus fileStatus = dfs.getFileStatus(src2);

				System.out.println("File Written: " + fileStatus.getPath());

				fs.close();

				/*
				 * //Read from the files just written to confirm they are there
				 * FSDataInputStream is = dfs.open(src2);
				 * System.out.println("Now reading file that was just written: "
				 * ); BufferedReader in = new BufferedReader(new
				 * InputStreamReader(is)); String input = in.readLine(); while
				 * (input != null) { System.out.println(input); input =
				 * in.readLine(); }
				 * 
				 * System.out.println(
				 * "Done reading, next file wll be written and then read if it exists"
				 * ); System.out.println();
				 */
				src = new Path("/user/interns/Commits");
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

	}

	/**
	 * Extends pathNames
	 * 
	 * @param current
	 * @param subDirName
	 * @return a new Path with a subdirectory attached to current
	 */
	private static Path extendPath(Path current, String subDirName) {
		return new Path(current.toString() + "/" + subDirName);
	}

	// End HDFS methods
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err
					.println("This jar should take exactly 1 argument which should be a String");
		} else {
			insertCommitsIntoHDFS(parseGitInput(args[0]));

		}
	}

	private static ArrayList<JSONObject> getCommitsFromFile(File file) {
		ArrayList<JSONObject> jObs = new ArrayList<JSONObject>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line;
			boolean firstLine = true;

			while ((line = in.readLine()) != null) {
				if (firstLine) {
					firstLine = false;
				} else {
					JSONObject j = new JSONObject(line);
					jObs.add(j);
				}
			}
		} catch (Exception ex) {
			System.err.println(ex);
		}
		return jObs;
	}
}

package BackEnd;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.json.JSONException;
import org.json.JSONObject;

public class BackEndJar {

	/**
	 * Parses the git input sent by the Jenkins-run shell script git commands
	 * 
	 * @param tbp
	 *            the string to be parsed (tbp = toBeParsed)
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<Commit> parseGitInput(String tbp) {
		// initialize local variables
		ArrayList<Commit> commits = new ArrayList<Commit>();
		String commId, email, day, date, msg;
		int hour, minute, second, numFilesChanged, insertions, deletions;
		ArrayList<String> filesChanged;
		ArrayList<String> upc = new ArrayList<String>(); // upc = unparsed
															// commits, each is
															// a JSON String
															// format

		// format the input into a valid JSON String
		tbp = tbp.substring(5);
		tbp = "{" + tbp;

		try {
			JSONObject bigJOb = new JSONObject(tbp);
			Iterator<String> iter = bigJOb.keys();
			while (iter.hasNext()) {
				upc.add(bigJOb.get(iter.next()).toString());
			}

			// Retrieve and store each JSON Commit string as a JSON Object in an
			// ArrayList
			ArrayList<JSONObject> jObs = getCommits(upc);
			for (JSONObject j : jObs) {

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
				//System.out.println(c);
			}

		} catch (JSONException e) {
			System.err.println(e.getMessage());
		}

		return commits;
	}

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

	private static int[] parseTime(String timeString) {
		timeString = timeString.trim();
		StringTokenizer st = new StringTokenizer(timeString, ":");
		int[] times = new int[3];
		times[0] = Integer.parseInt(st.nextToken()); // hour
		times[1] = Integer.parseInt(st.nextToken()); // minute
		times[2] = Integer.parseInt(st.nextToken()); // second
		return times;
	}

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

	public static void insertCommitsIntoHDFS(ArrayList<Commit> commits) {
		try {
			// Connect and open HDFS and set path variables
			Configuration config = new Configuration();
			config.set("fs.default.name", "hdfs://10.45.111.143:8020");
			FileSystem dfs = FileSystem.get(config);
			String pathString = "/OldCommits";
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

				Path src2 = new Path("/OldCommits/2011/6/23/14/2011/6/23/13/"+ c.getId()+".txt");
				FSDataOutputStream fs = dfs.create(src2);
				fs.write(c.toString().getBytes());
				System.out.println("File Written: " + dfs.getWorkingDirectory().toString() +src2.toString());
				fs.close();
				
				//Read from the files just written to confirm they are there
				FSDataInputStream is = dfs.open(src2);
				System.out.println("Now reading file that was just written: ");
				BufferedReader in = new BufferedReader(new InputStreamReader(is));
				String input = in.readLine();
				while(input != null){
					System.out.println(input);
					input = in.readLine();
				}
				
				System.out.println("Done reading, next file wll be written and then read if it exists");
				System.out.println();
				
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

	}

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

}

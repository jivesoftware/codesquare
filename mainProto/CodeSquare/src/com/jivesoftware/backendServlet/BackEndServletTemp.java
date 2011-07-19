package com.jivesoftware.backendServlet;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jivesoftware.toolbox.HDFS;
import com.jivesoftware.toolbox.Hbase;
import com.jivesoftware.backendServlet.Commit;
import com.jivesoftware.backendServlet.Date;
import com.jivesoftware.badges.BasicBadges;

public class BackEndServletTemp {

	/***
	 * This is the main method for the back end processing, it calls the git
	 * parser and receives an array of Commits. From there it calls the
	 * checkUpdateBadges to update the HBase.
	 * 
	 * @param args
	 *            consists of a string that contains unparsed commit data
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		ArrayList<Commit> output = parseGitInput(args[0], args[1]);
		Object[] out = Hbase.setup();
		HTable table = (HTable) out[1];
		Configuration config = (Configuration) out[0];
		HDFS.insertCommitsIntoHDFS(output);

		if (table == null) {
			System.out.println("could not find table");
			return;
		}
		
		for (int i = output.size() - 1; i >= 0; i--) {
			Commit c = output.get(i);
			BasicBadges.checkUpdateBadges(table, c , 0);
		}
		Result data = Hbase.getRowData(table, "eric.ren@jivesoftware.com");
		Hbase.test(data);
		try {
			table.close();
			// free resources and close connections
			HConnectionManager.deleteConnection(config, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	  /**
     * Parses the git input sent by the Jenkins-run shell script git commands
     * 
     * @param json, pushDate
     *            the name of the file containing all the commits
     * @throws Exception 
     */
    public static ArrayList<Commit> parseGitInput(String json, String pushDate) throws Exception {
        // initialize local variables
        ArrayList<Commit> commits = new ArrayList<Commit>();
        // Create Commit objects from JSON and write the Commits to HDFS
        for (JSONObject j : getCommits(json)) {
            try {
                Commit commit = new Commit();
                commit.setPushDate(new Date(pushDate));
                commit.setCommitDate(new Date(j.getString("commitDate")));
                commit.addStats(j.getString("stats"));
                commit.setId(j.getString("cID"));
                commit.setEmail(j.getString("email"));
                commit.setMessage(j.getString("cMes"));
                commits.add(commit);
            } catch (JSONException ex) {
            	ex.printStackTrace();
            }
        }
        return commits;
    }
    
	/**
	 * 
	 * @param upc
	 *            un-parsed-commits in JSON format
	 * @return an ArrayList of JSON objects that contain commit info
	 */
    private static ArrayList<JSONObject> getCommits(String json) throws Exception{
        ArrayList<JSONObject> jobs = new ArrayList<JSONObject>();
        JSONArray array = new JSONArray(json);
        for(int index = 0; index < array.length(); ++index) {
        	jobs.add(array.getJSONObject(index));
        }
        return jobs;
    }
}

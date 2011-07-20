package com.jivesoftware.backendServlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hbase.client.HTable;
import org.json.JSONArray;
import org.json.JSONObject;

import com.jivesoftware.toolbox.HDFSTools;
import com.jivesoftware.toolbox.HbaseTools;
import com.jivesoftware.badges.BasicBadges;
import com.jivesoftware.toolbox.ServletTools;

/**
 * Servlet implementation class BackEndServlet
 */
@WebServlet("/BackEndServlet")
public class BackEndServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BackEndServlet() {
        super();
       
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try{
			doGetOrPost(request,response);
		}catch(Exception e){
			System.err.println(e.getClass() + ":" + e.getMessage());
		}
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
			try {
				doGetOrPost(request,response);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.err.println(e.getClass() + ":" + e.getMessage());
			}

	}
	
	/**
	 * Handles two types of requests:
	 * One request sends commits stored in a JSONarray has JSON Objects & parses the commits, checks for simple badges, and writes the output to HDFS
	 * The other request sends the date of the most recent commit made by that user
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void doGetOrPost(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String queryStr = request.getQueryString();
		Configuration hbaseConfig = HbaseTools.getHBaseConfiguration();
		HTable table = HbaseTools.getTable(hbaseConfig);
		
		if(queryStr.contains("json") && queryStr.contains("pushDate")){
			JSONArray jArrCommits = new JSONArray(request.getParameter("json"));
			String pushDate = request.getParameter("pushDate");
			System.out.println("JSON Parameter Passed: " + jArrCommits.toString());
			System.out.println("pushDate Parameter Passed: " + pushDate);
			
			if(jArrCommits != null && pushDate != null && jArrCommits.toString().length() > 0 && pushDate.length() > 0){
				Configuration config = HDFSTools.getConfiguration();
				FileSystem hdfs = FileSystem.get(config);
				
				for(int i = 0;i < jArrCommits.length();i++){
					JSONObject jCommit = new JSONObject(jArrCommits.get(i));
					Commit c = ServletTools.convertToCommit(jCommit, pushDate);
					HDFSTools.writeCommitToHDFS(hdfs, c); //writes a commit as it's own text file in the appropriate HDFS folder
					//TODO Hey Justin mind writing this one too?
					BasicBadges.checkUpdateBadges(table, c, 0); //checks to see if this commit earned it's commiter any basic badges
				}
				
				hdfs.close(); //close connection to hdfs
				
			}else{
				System.err.println("One of the request parameter values is bad: " + jArrCommits.toString() + " " + pushDate);
			}
			
		}else if(queryStr.contains("recDate")){
			String infoForRecDate = request.getParameter("recDate");
			System.out.println("recDate Parameter Passed: " + infoForRecDate);
			
			if(infoForRecDate != null && infoForRecDate.length() > 0){
				
				JSONObject jRecDate = new JSONObject();
				String mostRecentPushDate = HbaseTools.getPushDate(table, infoForRecDate);
				jRecDate.put("recDate", mostRecentPushDate);
				ServletTools.sendJSONOutput(response,jRecDate);
				
			}else{
				System.err.println("Bad recDate Parameter Value: " + infoForRecDate);
			}
		}else{
			System.err.println("Bad Query String: " + queryStr);
		}
		
		
	}
	
	

}

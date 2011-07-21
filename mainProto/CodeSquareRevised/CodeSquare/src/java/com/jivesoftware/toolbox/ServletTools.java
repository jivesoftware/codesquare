package com.jivesoftware.toolbox;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.jivesoftware.backendServlet.Commit;
import com.jivesoftware.backendServlet.Date;

/**
 * This class includes methods that amy be useful to any servlet
 * @author diivanand.ramalingam
 *
 */
public class ServletTools {
	
	/**
	 * Sends a response back to the requester in JSON format. The data in the json depends on what the request was.
	 * @param response the http servlet response to a given http servlet request
	 * @param j The JSONObject to be sent as a String to the requester
	 * @throws JSONException
	 * @throws IOException
	 */
	public static void sendJSONOutput(HttpServletResponse response, JSONObject j) throws JSONException, IOException{
		response.setContentType("application/json");
        OutputStream out = response.getOutputStream();
        response.setContentLength(j.toString().length());
        out.write(j.toString().getBytes());
        out.close(); // Closes the output stream
	}
	
	/**
	 * Converts a JSON Commit Object into  Java Commit Object
	 * @param jCommit the JSON Object containing commit information
	 * @param pushDate the Git Push date of the commit
	 * @return A java Commit object
	 * @throws JSONException
	 */
	public static Commit convertToCommit(JSONObject jCommit, String pushDate) throws JSONException{
		Commit commit = new Commit();
        System.out.println("Entered convertToCommit Method");
                
        commit.setPushDate(new Date(pushDate));
        
        System.out.println("Parsed Push Date");
        
        commit.setCommitDate(new Date(jCommit.getString("date")));
        
        System.out.println("Parsed Commit Date");
        
        commit.addStats(jCommit.getString("stats"));
        
        System.out.println("Parsed stats");
        
        commit.setId(jCommit.getString("cID"));
        
        System.out.println("cID Parsed");
        
        commit.setEmail(jCommit.getString("email"));
        
        System.out.println("Email Parsed");
        
        commit.setMessage(jCommit.getString("cMes"));
        
        System.out.println("Message Parsed");
        
        return commit;
	}
        
}

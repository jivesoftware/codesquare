package com.jivesoftware.toolbox;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.jivesoftware.backendServlet.Commit;
import com.jivesoftware.backendServlet.JiveDate;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * This class includes methods that amy be useful to any servlet
 * @author diivanand.ramalingam
 *
 */
public class ServletTools {
    
         public static boolean hasParams(HttpServletRequest request, String[] params){
             System.out.println("XXX: "+(request.getParameter(params[0]) != null));
             System.out.println("XXX: "+(request.getParameter(params[1]) != null));
             System.out.println("XXX: "+(request.getParameter(params[2]) != null));
             System.out.println("XXX: "+(request.getParameter(params[0]).length() > 0));
             System.out.println("XXX: "+(request.getParameter(params[1]).length() > 1));
             System.out.println("XXX: "+(request.getParameter(params[2]).length() > 2));
             
             if((request.getParameter(params[0]) != null) &&
                     (request.getParameter(params[1]) != null) &&
                     (request.getParameter(params[2]) != null)
                     ) {
             if((request.getParameter(params[0]).length() > 0) &&
                     (request.getParameter(params[1]).length() > 0) &&
                     (request.getParameter(params[2]).length() > 0)
                     ) {
                 return true;
             }
             }
             return false;
         }
	
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
	public static Commit convertToCommit(JSONObject jCommit, 
                String pushUnixTime, String timeZone) throws JSONException{
            Commit commit = new Commit();
            commit.setPushDate(new JiveDate(pushUnixTime, timeZone));
            commit.setCommitDate(new JiveDate(jCommit.getString("unixtimestamp"), jCommit.getString("isotimestamp").split(" ")[2]));
            commit.addStats(jCommit.getString("stats"));
            commit.setId(jCommit.getString("cID"));
            commit.setEmail(jCommit.getString("email"));
            commit.setMessage(jCommit.getString("cMes"));
            return commit;
	}
        
}
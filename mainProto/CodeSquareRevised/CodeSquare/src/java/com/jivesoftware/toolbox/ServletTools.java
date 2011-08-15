package com.jivesoftware.toolbox;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.jivesoftware.backendServlet.Commit;
import com.jivesoftware.backendServlet.JiveDate;
import java.io.File;
import java.io.FileInputStream;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import java.util.List;

/**
 * This class includes methods that amy be useful to any servlet
 * @author diivanand.ramalingam
 *
 */
public class ServletTools {
    
         public static boolean hasParams(HttpServletRequest request, String[] params){
             
             for(int i=0; i<params.length;i++){
                 if(request.getParameter(params[i]) == null){
                     return false;
                 }
             }
             /*
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
             }*/
             return true;
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
                String pushUnixTime) throws JSONException{
            Commit commit = new Commit();
            commit.setPushDate(new JiveDate(pushUnixTime));
            String x = jCommit.getString("isotimestamp").split(" ")[2];
            commit.setCommitDate(new JiveDate(jCommit.getString("unixtimestamp"), jCommit.getString("isotimestamp")));
            commit.addStats(jCommit.getString("stats"));
            commit.setId(jCommit.getString("cID"));
            commit.setEmail(jCommit.getString("email"));
            commit.setMessage(jCommit.getString("cMes"));
            return commit;
	}
        
    /**
     * This method takes the list of badges that a person recently obtained
     * and converts the badge data into a nice json object to be posted to
     * the activity stream
     * @param badgesList A string list filled with the data of the recently obtained badges
     * @param name The name of the person who just received the badges
     * @return a String in JSON format that will be posted to the activity stream
     **/
    public static String makeJSONPost(List<Badge> badgesList, String name) throws Exception {
	String jsonString;
	JSONObject jsonObj = new JSONObject();
	JSONArray postAry = new JSONArray();
	
	JSONObject actorObj = new JSONObject();
	actorObj.put("title", name);
	
	for (Badge b : badgesList) {
	    JSONObject curPost = new JSONObject();
	    JSONObject curObject = new JSONObject();
	    JSONObject mediaObject = new JSONObject();
	 
	    String badgeTitle = b.getTitle();
	    
	    curPost.put("verb", "post");
	    //curPost.put("title", "__MSG_feed.title.format__");
	    curPost.put("title", name +" received " + badgeTitle + " badge!");
	    //curPost.put("body", "__MSG_feed.body.format__");
	    curPost.put("body", "Congratulations! You got a new badge!");
	    
	    curPost.put("actor", actorObj);

	    curObject.put("title", badgeTitle);
	    curObject.put("summary", b.getDesc());
                    
            mediaObject.put("url", b.getImgLink());        
	    curObject.put("mediaLink", mediaObject);
	    
	    curPost.put("object", curObject);
	    
	    postAry.put(curPost);
	}
	    
	jsonObj.put("items", postAry);
	jsonString = jsonObj.toString();
	return jsonString;
    }
    
    public static String[][] getBadgeInfo() throws IOException, JSONException{
	String activityFileName = "/home/interns/badgedescriptions";
	File activityFile = new File(activityFileName);
        FileInputStream fis = new FileInputStream(activityFile);
        byte[] activityBytes = new byte[(int)activityFile.length()];
        fis.read(activityBytes, 0, activityBytes.length);
        fis.close();
        
        String json = new String(activityBytes);
        JSONArray j1 = new JSONArray(json);

        String[][] badgeList = new String[j1.length()][j1.getJSONObject(0).length()];

        for(int i = 0; i < j1.length(); i++){
            badgeList[i][0] = (String) j1.getJSONObject(i).getString("name");
            badgeList[i][1] = (String) j1.getJSONObject(i).getString("description");
        }
        
        return badgeList;
    }

}

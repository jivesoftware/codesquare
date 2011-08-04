package com.jivesoftware.backendServlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTable;
import org.json.JSONArray;

import com.jivesoftware.toolbox.HDFSTools;
import com.jivesoftware.toolbox.HbaseTools;
import com.jivesoftware.badges.BasicBadges;
import com.jivesoftware.toolbox.ServletTools;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Map;

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
                    System.out.println("GET");
                    doGetOrPost(request,response);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
			try {
                            System.out.println("POST");
                            doGetOrPost(request,response);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
                Configuration hbaseConfig = HbaseTools.getHBaseConfiguration();
                HTable table = HbaseTools.getTable(hbaseConfig);
                System.out.println("QSTRING: "+request.getQueryString());
                String[] params = {"json", "unixTime", "timeZone"};
                String[] params2 = {"email", "newId"};
                if (ServletTools.hasParams(request ,params)) {
                    System.out.println("PARAMS1");
                    String unixTime = request.getParameter(params[1]);
                    System.out.println("unixTime: "+unixTime);
                    String timeZone = request.getParameter(params[2]);
                    System.out.println("timeZone: "+timeZone);
                    System.out.println("JSON: "+request.getParameter(params[0]));
                    JSONArray jArrCommits = new JSONArray(request.getParameter(params[0]));
                    System.out.println("jArrCommits: "+jArrCommits+"LENGTH"+jArrCommits.length());
                    if(jArrCommits.length() > 0 && 
                       unixTime.length() > 0 && timeZone.length() > 0){
                        System.out.println("INFORLOOP-PARAMS1");
                        
                        Configuration config = HDFSTools.getConfiguration();
			FileSystem hdfs = FileSystem.get(config);
                        
                        BasicBadges x = new BasicBadges(jArrCommits, hdfs, table, unixTime, timeZone);
                        hdfs.close();
                    }
                    else {
                        System.out.println("LENGTH FAIL");
                    }
                }
                else if(ServletTools.hasParams(request,params2)){
                        System.out.println("PARAMS2");
			String email = request.getParameter(params2[0]);
                        String newId =request.getParameter(params2[1]);
			if(email.length() > 0 &&  newId.length() > 0){
                            System.out.println("INFORLOOP-PARAMS2");
                            // get recent push date, update with new push date
                            String pushDate = HbaseTools.getLastCommitId(table, email, newId);
                            // send back info
                            OutputStream out = response.getOutputStream();
                            out.write(pushDate.getBytes());
                            out.close();
				
			}else{
				System.err.println("Bad pushDate Parameter Value: " + "EX");
			}
		}else{	System.err.println("BAD PARAMS: " + "EX");	
		//testing printouts
                try{
                    HbaseTools.test(HbaseTools.getRowData(table, "eric.ren@jivesoftware.com"));
                }catch(NullPointerException e){
                    System.out.println("could not print eric");
                }
                try{
                    HbaseTools.test(HbaseTools.getRowData(table, "justin.kikuchi@jivesoftware.com"));
                }catch(NullPointerException e){
                    System.out.println("could not print justin");
                }
                // free resources and close connections
		HConnectionManager.deleteConnection(hbaseConfig, true);
		table.close();
	}
}

}
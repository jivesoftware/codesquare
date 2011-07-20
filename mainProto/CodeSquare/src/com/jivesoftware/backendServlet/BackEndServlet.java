package com.jivesoftware.backendServlet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class BackEndServley
 */
@WebServlet("/BackEndServlet")
public class BackEndServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BackEndServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try{
			doGetOrPost(request,response);
		}catch(JSONException e){
			System.err.println(e.getClass() + ":" + e.getMessage());
		}
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try{
			doGetOrPost(request,response);
		}catch(JSONException e){
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
	protected void doGetOrPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, JSONException{
		ArrayList<Commit> commits = new ArrayList<Commit>();
		String queryStr = request.getQueryString();
		
		if(queryStr.contains("json") && queryStr.contains("pushDate")){
			JSONObject j = new JSONObject(request.getParameter("json"));
			String pushDate = request.getParameter("pushDate");
			System.out.println("JSON Parameter Passed: " + j.toString());
			System.out.println("pushDate Parameter Passed: " + pushDate);
			
			if(j != null && pushDate != null && j.toString().length() > 0 && pushDate.length() > 0){
				//TODO insert methods to handle json and pushDate here
				
				
			}else{
				System.err.println("One of the request parameter values is bad: " + j.toString() + " " + pushDate);
			}
			
		}else if(queryStr.contains("recDate")){
			String recDate = request.getParameter("recDate");
			System.out.println("recDate Parameter Passed: " + recDate);
			
			if(recDate != null && recDate.length() > 0){
				//TODO insert methods to handle recDate here
				
			}else{
				System.err.println("Bad recDate Parameter Value: " + recDate);
			}
		}else{
			System.err.println("Bad Query String: " + queryStr);
		}
		
		
	}

}

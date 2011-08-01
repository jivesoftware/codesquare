package com.jivesoftware.appServlet;

import java.io.IOException;
import java.io.OutputStream;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.conf.Configuration;

import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTable;
import org.json.*;

import com.jivesoftware.toolbox.HbaseTools;

/**
 * Retrieves email information from the Jive App and uses it to query
 * the 'Employee' and 'Badges' HBase table 
 * @author diivanand.ramalingam
 * @author justin.kikuchi
 */
@WebServlet({"/AppServlet", "/AS"})
public class AppServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public AppServlet() {
        super();
        // does nothing extra
    }

    /**
     * @see Servlet#init(ServletConfig)
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        // TODO Fill this in
    }

    /**
     * @see Servlet#destroy()
     */
    @Override
    public void destroy() {
        // Does nothing
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        doGetOrPost(request, response);

    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        doGetOrPost(request, response);

    }

    /**
     * Takes in parameters sent by URL and uses parameters to pull out the
     * badges obtained by the user or to add the user is the user is using
     * CodeSquare for the first time
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doGetOrPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        // Get parameter info from Jive App


        String email = request.getParameter("email");
        String bossEmail = request.getParameter("bossEmail");
        String earned = request.getParameter("earned");
        boolean earnedOnly = false;

        if (email == null || email.length() <= 0) {
            return;

        } else if (bossEmail == null || bossEmail.length() <= 0) {
            bossEmail = "noBoss@nomail.com";
        }

        if (earned != null) {
            earnedOnly = Boolean.parseBoolean(earned);
        }

        Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
        Matcher m = p.matcher(email);
        Matcher m2 = p.matcher(bossEmail);
        boolean matchFound = m.matches();
        boolean match2Found = m2.matches();

        System.out.println("Email Parameter Passed: " + email);
        System.out.println("Boss Parameter Passed: " + bossEmail);

        if (matchFound && match2Found) {

            Configuration conf = HbaseTools.getHBaseConfiguration();

 
            //Create a table
            HTable table = new HTable(conf, "EmpBadges"); //Employee table
            HTable BadgeTable = new HTable(conf, "Badges"); //Badges table

            HbaseTools.addUserOrUpdateBoss(table, email, bossEmail);

            Object[] badgeInfo = HbaseTools.getBadges(table, email);

            String[] badgesWithDescription = (String[]) badgeInfo[0]; // Elements
            // in
            // array
            // have
            // this
            // invariant:
            // {BadgeNumber1,Badge1Description,BadgeNumber2,Badge2Description,etc.}
            String newBadges = (String) badgeInfo[1];

            if (badgesWithDescription == null) {
                return;
            } else {
                try {
                    if (email.length() != 0) {
                        JSONObject j = convertOutputToJSON(badgesWithDescription, BadgeTable, newBadges, earnedOnly);
                        // Output Area
                        
                        
                        
                        response.setContentType("application/json");
                        OutputStream out = response.getOutputStream();
                        response.setContentLength(j.toString().length());
                        response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
                        response.setHeader("Pragma","no-cache"); //HTTP 1.0
                        response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
                        response.setHeader("Cache-Control","no-store"); //HTTP 1.1
                        out.write(j.toString().getBytes());
                        out.close(); // Closes the output stream
                    }

                } catch (JSONException e) {
                    System.out.println(e.getMessage());
                }
            }

            HbaseTools.resetNewBadges(table, email);
            table.close();
            //free resources and close connections
            HConnectionManager.deleteConnection(conf, true);

        } else {
            System.out.println("Invalid email address");
            return;
        }

    }

    /**
     * This method converts the badge data obtained from the HBase into JSON
     * @param badges A String array whole elements are acquired badge ID's
     * @param BadgeTable The table in the HBase containing Badge information
     * @param newBadges A String containing integers represented the Badge Id of newly obtained badges delimited by spaces
     * @return a JSONObject whose keys are badge ID, and values are JSON objects whose keys are Name, Description, URL, and New
     * @throws JSONException
     * @throws IOException 
     */
    private static JSONObject convertOutputToJSON(String[] badges, HTable BadgeTable, String newBadges, boolean earnedOnly)
            throws JSONException, IOException {
        JSONObject j = new JSONObject();

        for (int i = 0; i < badges.length; i++) {
            System.out.println("Processing Badge No: " + badges[i]);
            String[] badgeInfo = HbaseTools.getBadgeInfo(BadgeTable, badges[i]);

            JSONObject j2 = new JSONObject();
            j2.put("Name", badgeInfo[1]);
            j2.put("Description", badgeInfo[2]);
            j2.put("IconURL", badgeInfo[3]);
            if (newBadges.contains(badges[i])) {
                j2.put("New", true);
            } else {
                j2.put("New", false);
            }
            System.out.println(j2.toString());
            j.put(badges[i], j2);

        }


        for (Integer i = new Integer(1); i <= 30; i++) {

            if (!j.has(i.toString())) {
                JSONObject j3 = new JSONObject();
                j3.put("Name", HbaseTools.getBadgeInfo(BadgeTable, i.toString())[1]);
                j3.put("Description", HbaseTools.getBadgeInfo(BadgeTable, i.toString())[2]);
                j3.put("IconURL", "images/unobtained.png");
                j3.put("New", false);
                j.put(i.toString(), j3);
            }
        }



        return j;
    }



  

}

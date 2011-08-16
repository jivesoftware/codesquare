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
import com.jivesoftware.toolbox.ServletTools;
import com.jivesoftware.toolbox.Config;
import java.util.ArrayList;

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
        String compare = request.getParameter("compare");
        String id = request.getParameter("id");
        String name = request.getParameter("name");
        boolean compareOnly = false;

        if (email == null || email.length() <= 0) {
            return;

        } else if (bossEmail == null || bossEmail.length() <= 0) {
            bossEmail = "noBoss@nomail.com";
        }

        if (compare != null) {
            compareOnly = Boolean.parseBoolean(compare);
        }

        Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
        Matcher m = p.matcher(email);
        Matcher m2 = p.matcher(bossEmail);
        boolean matchFound = m.matches();
        boolean match2Found = m2.matches();

        System.out.println("AppServlet Email Parameter Passed: " + email);
        System.out.println("AppServlet Boss Parameter Passed: " + bossEmail);

        if (matchFound && match2Found) {

            Configuration conf = HbaseTools.getHBaseConfiguration();

 
            //Create a table
            HTable table = new HTable(conf, "EmpBadges"); //Employee table
            if(!bossEmail.equals("noBoss@nomail.com") && name != null && id != null){
                HbaseTools.addUserOrUpdateBoss(table, email, bossEmail, name, id);
            }
            
            Object[] badgeInfo = HbaseTools.getBadges(table, email);

            ArrayList<String> badges = (ArrayList<String>) badgeInfo[0];
            String newBadges = (String) badgeInfo[1];

            if (badges == null) {
                return;
            } else {
                try {
                    if (email.length() != 0) {
                        JSONObject j = null;
                        if(!bossEmail.equals("noBoss@nomail.com") && name != null && id != null){
                            j = convertOutputToJSON(badges, newBadges, true);
                        }
                        else{
                            j = convertOutputToJSON(badges, newBadges, false);
                        }
                            
                        // Output Area
                        
                        
                        
                        response.setContentType("application/json");
                        OutputStream out = response.getOutputStream();
                        response.setContentLength(j.toString().length());
                        response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
                        response.setHeader("Pragma","no-cache"); //HTTP 1.0
                        response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
                        //response.setHeader("Cache-Control","no-store"); //HTTP 1.1
                        out.write(j.toString().getBytes());
                        out.close(); // Closes the output stream
                    }

                } catch (JSONException e) {
                    System.out.println(e.getMessage());
                }
            }
            if(bossEmail.equals("noBoss@nogmail.com") && name == null && id == null && compareOnly==false){
                HbaseTools.resetNewBadges(table, email);
            }
            table.close();
            //free resources and close connections
            HConnectionManager.deleteConnection(conf, true);
            table.close();

        } else {
            System.out.println("Invalid email address");
            return;
        }
        Config.test();
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
    private static JSONObject convertOutputToJSON(ArrayList<String> badges, String newBadges, boolean earnedOnly)
            throws JSONException, IOException {
        JSONObject j = new JSONObject();
        
        String[][] badgesList = ServletTools.getBadgeInfo();
        if(!earnedOnly){
            for (Integer i = 0; i < badges.size(); i++) {
                String[] badgeInfo = badgesList[Integer.parseInt(badges.get(i))-1];

                JSONObject j2 = new JSONObject();
                j2.put("Name", badgeInfo[0]);
                j2.put("Description", badgeInfo[1]);
                j2.put("IconURL", "images/"+badges.get(i)+".png");
                j2.put("thumbnail", "images/thumbnails/"+badges.get(i)+"TH.png");
                if (newBadges.contains(badges.get(i))) {
                    j2.put("New", true);
                } else {
                    j2.put("New", false);
                }
                System.out.println(j2.toString());
                j.put(badges.get(i), j2);

            }


            for (Integer k = new Integer(1); k <= badgesList.length; k++) {

                if (!j.has((k).toString())) {
                    JSONObject j3 = new JSONObject();
                    String[] badgeInfo = badgesList[k-1];
                    j3.put("Name", badgeInfo[0]);
                    j3.put("Description", badgeInfo[1]);
                    j3.put("IconURL", "images/unobtained.png");
                    j3.put("thumbnail", "images/thumbnails/unobtainedTH.png");
                    j3.put("New", false);
                    j.put(k.toString(), j3);
                }
            }
        }
        else{
            Integer back=badgesList.length-1;
            Integer forward=0;
            for (Integer k = new Integer(1); k <= badgesList.length; k++) {
                String[] badgeInfo = badgesList[k-1];
                if(badges.contains(k.toString())){
                    JSONObject j2 = new JSONObject();
                    j2.put("Name", badgeInfo[0]);
                    j2.put("Description", badgeInfo[1]);
                    j2.put("IconURL", "images/thumbnails/"+k.toString()+"TH.png");
                    if (newBadges.contains(k.toString())) {
                        j2.put("New", true);
                    } else {
                        j2.put("New", false);
                    }
                    System.out.println(j2.toString());
                    j.put(forward.toString(), j2);
                    forward++;
                }
                else {
                    JSONObject j3 = new JSONObject();
                    j3.put("Name", badgeInfo[0]);
                    j3.put("Description", badgeInfo[1]);
                    j3.put("IconURL", "images/thumbnails/unobtainedTH.png");
                    j3.put("New", false);
                    j.put(back.toString(), j3);
                    back--;
                }
            }
        }
        return j;
    }

}

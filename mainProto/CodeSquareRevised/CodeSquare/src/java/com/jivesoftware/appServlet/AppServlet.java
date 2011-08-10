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
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.util.Bytes;

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
        long time = System.currentTimeMillis();

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
            HTable badgeTable = new HTable(conf, "Badges"); //Badges table
            if(!bossEmail.equals("noBoss@nomail.com")){
                HbaseTools.addUserOrUpdateBoss(table, email, bossEmail);
            }
            
            Object[] badgeInfo = HbaseTools.getBadges(table, email);

            ArrayList<String> badgesWithDescription = (ArrayList<String>) badgeInfo[0]; // Elements
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
                        JSONObject j = convertOutputToJSON(badgesWithDescription, badgeTable, newBadges, earnedOnly);
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
            System.out.println("time: "+ (System.currentTimeMillis()-time));
            if(bossEmail.endsWith("noBoss@nogmail.com")){
                HbaseTools.resetNewBadges(table, email);
            }
            table.close();
            //free resources and close connections
            HConnectionManager.deleteConnection(conf, true);
            table.close();
            badgeTable.close();

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
    private static JSONObject convertOutputToJSON(ArrayList<String> badges, HTable badgeTable, String newBadges, boolean earnedOnly)
            throws JSONException, IOException {
        JSONObject j = new JSONObject();
        
        String[][] badgesList = {
            {"First Commit", "User committed 1 time."},
            {"Fifty Commits", "User committed 50 times."},
            {"Five Hundred Commits", "User committed 500 times,"},
            {"One Thousand Commits", "User committed 1000 times."},
            {"Five Thousand Commits", "User committed 5000 times."},
            {"Trick or Treat Badge", "User committed on Halloween."},
            {"Leprechaun Badge", "User committed on St. Patrick's Day."},
            {"Leap Year Badge", "User committed on Leap Year Day."},
            {"Love Badge", "User committed on Valentine's Day."},
            {"Pi Day", "User committed on 3.14"},
            {"Humming Bird", "User committed twice within a minute."},
            {"Night Owl", "User committed after 10pm."},
            {"Early Bird", "User committed before 6am."},
            {"Team Player", "User and 2 or more people made changes to the same directory in the same hour."},
            {"United We Stand", "User and 9 or more people made changes to the same directory in the same hour."},
            {"Grape Squasher", "User gained 8 or more badges in 1 week"},
            {"Numa Numa", "User had the least lines of code per file in a given week."},
            {"Manic Monday", "User committed on Monday before 8am."},
            {"Rebecca Black Friday", "User committed on Friday after 4pm."},
            {"Charlotte Takes Tumble", "User committed a negative amount of lines code in a day."},
            {"Boss is Better", "User committed more lines of code than all of his employees."},
            {"Good Employee", "User committed more lines of code than his boss."},
            {"Best Employee", "User committed more lines of code than each of his peers."},
            {"Best day", "User committed those most lines of code in a single day."},
            {"Soul mate(s)", "User and one or more people commit within a 10 second time span in same directory."},
            {"Jiver", "User's commit message included the word Jive."},
            {"Dos Commits", "User committed twice in the same day."},
            {"Slacker", "User had 5 or more days in between commits."},
            {"Weekend Warrior", "User committed on the weekend."},
            {"Committed Person", "User committed every day of a give week."},
            {"Author", "Users commit message was greater than 20 words."}
        };
        if(!earnedOnly){
            for (Integer i = 0; i < badges.size(); i++) {
                System.out.println("Processing Badge No: " + badges.get(i));
                String[] badgeInfo = badgesList[Integer.parseInt(badges.get(i))-1];

                JSONObject j2 = new JSONObject();
                j2.put("Name", badgeInfo[0]);
                j2.put("Description", badgeInfo[1]);
                j2.put("IconURL", "images/"+badges.get(i)+".png");
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
                    j2.put("IconURL", "images/"+k.toString()+".png");
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
                    j3.put("IconURL", "images/unobtained.png");
                    j3.put("New", false);
                    j.put(back.toString(), j3);
                    back--;
                }
            }
        }

        
        return j;
        
        
        
        
        
        /*ResultScanner s = badgeTable.getScanner(Bytes.toBytes("Info"));
        Iterator<Result> badgeList = s.iterator();
        while(badgeList.hasNext()){
                Result r = badgeList.next();
                if(badges.contains(new String(r.getRow()))){
                    JSONObject j2 = new JSONObject();
                    j2.put("Name", new String(r.getValue(Bytes.toBytes("Info"), Bytes.toBytes("name"))));
                    j2.put("Description", new String(r.getValue(Bytes.toBytes("Info"), Bytes.toBytes("description"))));
                    j2.put("IconURL", new String(r.getValue(Bytes.toBytes("Info"), Bytes.toBytes("iconURL"))));
                    if (newBadges.contains(new String(r.getRow()))) {
                        j2.put("New", true);
                    } else {
                        j2.put("New", false);
                    }
                    System.out.println(j2.toString());
                    j.put(new String(r.getRow()), j2);
                }
                else if(earnedOnly == false){
                    JSONObject j3 = new JSONObject();
                    j3.put("Name", new String(r.getValue(Bytes.toBytes("Info"), Bytes.toBytes("name"))));
                    j3.put("Description", new String(r.getValue(Bytes.toBytes("Info"), Bytes.toBytes("description"))));
                    j3.put("IconURL", "images/unobtained.png");
                    j3.put("New", false);
                    j.put(new String(r.getRow()), j3);
                }
        }
        return j;*/

        
        /*
        
        for (Integer i = 0; i < badges.size(); i++) {
            System.out.println("Processing Badge No: " + badges.get(i));
            String[] badgeInfo = HbaseTools.getBadgeInfo(badgeTable, badges.get(i));

            JSONObject j2 = new JSONObject();
            j2.put("Name", badgeInfo[1]);
            j2.put("Description", badgeInfo[2]);
            j2.put("IconURL", badgeInfo[3]);
            if (newBadges.contains(badges.get(i))) {
                j2.put("New", true);
            } else {
                j2.put("New", false);
            }
            System.out.println(j2.toString());
            j.put(badges.get(i), j2);

        }


        for (Integer k = new Integer(1); k <= 30; k++) {

            if (!j.has(k.toString())) {
                JSONObject j3 = new JSONObject();
                String[] badgeInfo = HbaseTools.getBadgeInfo(badgeTable, k.toString());
                j3.put("Name", badgeInfo[1]);
                j3.put("Description", badgeInfo[2]);
                j3.put("IconURL", "images/unobtained.png");
                j3.put("New", false);
                j.put(k.toString(), j3);
            }
        }



        return j;
    }*/
    }

}

package codesquare;

import java.io.IOException;
import java.io.OutputStream;

import java.util.ArrayList;

import java.util.NavigableSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.conf.Configuration;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.json.*;

/**
 * Retrieves email information from the Jive App and uses it to query
 * the 'Employee' and 'Badges' HBase table 
 * @author diivanand.ramalingam
 * @author justin.kikuchi
 */
@WebServlet({"/FrontEndServlet", "/FES"})
public class FrontEndServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public FrontEndServlet() {
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

        if (matchFound && match2Found) {

            Configuration conf = HBaseConfiguration.create();
            conf.set("hbase.cluster.distributed", "true");
            conf.set("hbase.rootdir", "hdfs://hadoopdev008.eng.jiveland.com:54310/hbase");
            conf.set("hbase.zookeeper.quorum", "hadoopdev008.eng.jiveland.com,hadoopdev002.eng.jiveland.com,hadoopdev001.eng.jiveland.com");
            conf.set("hbase.zookeeper.property.clientPort", "2181");
            conf.set("hbase.hregion.max.filesize", "1073741824");
            //conf.addResource(new Path(
            //		"/Users/diivanand.ramalingam/Downloads/hbase/conf/hbase-site.xml"));

            //Create a table


            HTable table = new HTable(conf, "EmpBadges"); //Employee table
            HTable BadgeTable = new HTable(conf, "Badges"); //Badges table

            addUserOrUpdateBoss(table, email, bossEmail);

            Object[] badgeInfo = getBadges(table, email);

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
                        out.write(j.toString().getBytes());
                        out.close(); // Closes the output stream
                    }

                } catch (JSONException e) {
                    System.out.println(e.getMessage());
                }
            }

            resetNewBadges(table, email);
            table.close();
            //free resources and close connections
            HConnectionManager.deleteConnection(conf,true);

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
            String[] badgeInfo = getBadgeInfo(BadgeTable, badges[i]);



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

        JSONObject j3 = new JSONObject();
        j3.put("Name", "Unobtained");
        j3.put("Description", "Click to learn how to obtain");
        j3.put("IconURL", "images/unobtained.png");
        j3.put("New", false);

        
        /*
        if (earnedOnly) {
            for (Integer i = new Integer(1); i <= 30; i++) {

                if (!j.has(i.toString())) {
                    j.put(i.toString(), j3);
                }
            }
        }
        */
        
        for (Integer i = new Integer(1); i <= 30; i++) {

                if (!j.has(i.toString())) {
                    j.put(i.toString(), j3);
                }
            }



        return j;
    }

    /**
     * Object[0] = .
     * Object[1] = String of Badge ID's delimited by spaces of newly obtained Badges
     * @param table The Employee table in the HBase
     * @param email the row key of the Employee HBase table
     * @return 
     */
    private static Object[] getBadges(HTable table, String email) {
        Get get = new Get(Bytes.toBytes(email));
        Result data = null;
        Object[] output = new Object[2];
        ArrayList<String> resultingBadges = new ArrayList<String>();
        String newBadges = "";
        output[0] = resultingBadges;
        output[1] = newBadges;
        try {
            data = table.get(get);
        } catch (Exception e) {
            System.err.println();
        }


        if (data == null) {
            System.out.println("Not found");
            return output;
        }
        if (data.isEmpty()) {
            return output;
        }

        byte[] badges = Bytes.toBytes("Badge");


        NavigableSet<byte[]> badges_awarded = data.getFamilyMap(badges).descendingKeySet();



        for (byte[] badge : badges_awarded) {
            resultingBadges.add(new String(badge));
        }

        try {
            newBadges = new String(data.getValue(Bytes.toBytes("Info"), Bytes.toBytes("newBadges")));
        } catch (NullPointerException e) {
            System.err.println(e.getMessage());
        }


        /* checks for personal message, not used at the moment
        String[] result =  new String[resultingBadges.size()];
        result = resultingBadges.toArray(result);
        
        for(int i=0; i<result.length; i++){
        String customDescription = new String(data.getValue(Bytes.toBytes("Badge"), Bytes.toBytes(result[i])));
        int currentBadgeIndex = resultingBadges.indexOf(result[i]);
        resultingBadges.add(currentBadgeIndex+1, customDescription);
        }*/

        String[] result = new String[resultingBadges.size()];
        result = resultingBadges.toArray(result);
        output[0] = result;
        output[1] = newBadges;
        return output;
    }

    /**
     * returns a badge's information found in the Badges table of the HBase
     * @param table The Badges table in the HBase
     * @param badgeNumber the row key in the Badge's HBase table
     * @return 
     */
    private static String[] getBadgeInfo(HTable table, String badgeNumber) {
        Get get = new Get(Bytes.toBytes(badgeNumber));
        Result data = null;

        try {
            data = table.get(get);
        } catch (Exception e) {
            System.err.println();
        }

        if (data.isEmpty()) {
            return null;
        }

        String[] result = new String[4];
        result[0] = badgeNumber;
        result[1] = new String(data.getValue(Bytes.toBytes("Info"),
                Bytes.toBytes("name")));
        result[2] = new String(data.getValue(Bytes.toBytes("Info"),
                Bytes.toBytes("description")));
        result[3] = new String(data.getValue(Bytes.toBytes("Info"),
                Bytes.toBytes("iconURL")));

        return result;
    }

    /**
     * Updates the user's boss's email if the user has a new boss
     * @param table The Employee table in the HBase
     * @param email the user's email
     * @param bossEmail the user's boss's email 
     */
    private static void addUserOrUpdateBoss(HTable table, String email,
            String bossEmail) {
        Put row = new Put(Bytes.toBytes(email));

        row.add(Bytes.toBytes("Info"), Bytes.toBytes("bossEmail"),
                Bytes.toBytes(bossEmail));

        try {
            table.put(row);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Empties the value in the new Badges column for a specific Employee
     * @param table Employee HBase table
     * @param email the user's email
     */
    private static void resetNewBadges(HTable table, String email) {
        Put row = new Put(Bytes.toBytes(email));


        row.add(Bytes.toBytes("Info"), Bytes.toBytes("newBadges"), Bytes.toBytes(""));

        try {
            table.put(row);
        } catch (Exception e) {
            System.err.println();
        }
    }
}

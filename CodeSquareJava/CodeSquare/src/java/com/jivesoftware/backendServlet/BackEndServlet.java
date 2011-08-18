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

/**
 * Servlet implementation class BackEndServlet
 * 
 * @author Justin Kikuchi
 * @author Diivanand Ramamingam
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
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        try {
            doGetOrPost(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException {
        try {
            doGetOrPost(request, response);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Handles a request sends commits stored in a
     * JSONarray has JSON Objects & parses the commits, checks for simple
     * badges, and writes the output to HDFS The other request sends the date of
     * the most recent commit made by that user
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doGetOrPost(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Configuration hbaseConfig = HbaseTools.getHBaseConfiguration();
        HTable table = HbaseTools.getTable(hbaseConfig);
        String[] params = { "json", "unixTime" };
        if (ServletTools.hasParams(request, params)) {
            String unixTime = request.getParameter(params[1]);
            JSONArray jArrCommits = new JSONArray(
                    request.getParameter(params[0]));
            OutputStream out = response.getOutputStream();
            out.close();
            if (jArrCommits.length() > 0 && unixTime.length() > 0) {

                Configuration config = HDFSTools.getConfiguration();
                FileSystem hdfs = FileSystem.get(config);

                BasicBadges checkBadges = new BasicBadges(jArrCommits, hdfs, table,
                        unixTime);
                checkBadges.processBadges();
                hdfs.close();
            } else {
                System.out.println("LENGTH FAIL");
            }
        }
        else {
            System.err.println("BAD PARAMS: " + "EX");
        }
        // testing printouts
        try {
            HbaseTools.test(HbaseTools.getRowData(table,
                    "eric.ren@jivesoftware.com"));
        } catch (NullPointerException e) {
            System.out.println("could not print eric");
        }
        try {
            HbaseTools.test(HbaseTools.getRowData(table,
                    "justin.kikuchi@jivesoftware.com"));
        } catch (NullPointerException e) {
            System.out.println("could not print justin");
        }
        // free resources and close connections
        HConnectionManager.deleteConnection(hbaseConfig, true);
        table.close();
    }
}
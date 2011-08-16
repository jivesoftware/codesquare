/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jivesoftware.activityStream;

import com.jivesoftware.toolbox.Badge;
import com.jivesoftware.toolbox.HbaseTools;
import com.jivesoftware.toolbox.ServletTools;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;


/**
 *
 * @author justin.kikuchi
 */
@WebServlet(name = "ActivityStreamServlet", urlPatterns = {"/ActivityStreamServlet"})
public class ActivityStreamServlet extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, Exception {


        String[] params = {"email", "newBadges"};

        if (ServletTools.hasParams(request, params)) {
            String email = request.getParameter(params[0]);
            String newBadges = request.getParameter(params[1]);
            
            Configuration conf = HbaseTools.getHBaseConfiguration();
            HTable table = HbaseTools.getTable(conf);
            Result data = HbaseTools.getRowData(table, email);
            
            String name = HbaseTools.getName(data);
            String id = HbaseTools.getUserId(data);
            
            if(name.isEmpty() || id.isEmpty()){
                System.out.println("user email: " +email+" is not installed, cannot post to activity stream.");
                return;
            }
            
            String [] newBadgesList = newBadges.split(" ");

            String[][] badgesList = ServletTools.getBadgeInfo();
            ArrayList<Badge> badges = new ArrayList<Badge>();
            for(String s : newBadgesList){
                System.out.println("Posting badge to the acitivity stream: " + s);
                Badge badge = new Badge(badgesList[Integer.parseInt(s)-1][0], badgesList[Integer.parseInt(s)-1][1], s+".png");
                badges.add(badge);
            }

            String jsonActivity = ServletTools.makeJSONPost(badges, name);
            System.out.println("JSON:   " +jsonActivity);
            ActivityPoster.postToActivity(id, null, jsonActivity);
        }
        
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            Logger.getLogger(ActivityStreamServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            Logger.getLogger(ActivityStreamServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

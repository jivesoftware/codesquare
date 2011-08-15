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
    

  /*  
    public static Configuration getHBaseConfiguration() {
		Configuration config = HBaseConfiguration.create();
		config.set("hbase.cluster.distributed", "true");
		config.set("hbase.rootdir", "hdfs://hadoopdev008.eng.jiveland.com:54310/hbase");
		config.set( "hbase.zookeeper.quorum", "hadoopdev008.eng.jiveland.com,hadoopdev002.eng.jiveland.com,hadoopdev001.eng.jiveland.com");
		config.set("hbase.zookeeper.property.clientPort", "2181");
		config.set("hbase.hregion.max.filesize", "1073741824");
		return config;
	}
    public static void addAppInfo(HTable table, String email, String userId, String jiveId) {
        Put row = new Put(Bytes.toBytes(email));

        row.add(Bytes.toBytes("Info"), Bytes.toBytes("jiveId"),
                Bytes.toBytes(jiveId));
        row.add(Bytes.toBytes("Info"), Bytes.toBytes("userId"),
                Bytes.toBytes(userId));

        try {
            table.put(row);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
	public static String[][] getBadgeInfo(HTable table, String[] badgeNumbers) {
		String[][] badgeInfo = new String[badgeNumbers.length][4];
		for(int i=0;i<badgeNumbers.length;i++){
			Get get = new Get(Bytes.toBytes(badgeNumbers[i]));
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
			result[0] = badgeNumbers[i];
			result[1] = new String(data.getValue(Bytes.toBytes("Info"),
					Bytes.toBytes("name")));
			result[2] = new String(data.getValue(Bytes.toBytes("Info"),
					Bytes.toBytes("description")));
			result[3] = new String(data.getValue(Bytes.toBytes("Info"),
					Bytes.toBytes("iconURL")));
			badgeInfo[i] = result;
			
		}

		return badgeInfo;
	}
	public static String[] getAppInfo(HTable table, String email) {
		Get get = new Get(Bytes.toBytes(email));
		Result data = null;

		try {
			data = table.get(get);
		} catch (Exception e) {
			System.err.println();
		}

		if (data.isEmpty()) {
			return null;
		}

		String[] result = new String[2];
		result[0] = new String(data.getValue(Bytes.toBytes("Info"),
				Bytes.toBytes("userId")));
		result[1] = new String(data.getValue(Bytes.toBytes("Info"),
				Bytes.toBytes("jiveId")));

		return result;
	}*/

}

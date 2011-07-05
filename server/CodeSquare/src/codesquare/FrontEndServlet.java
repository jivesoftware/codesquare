package codesquare;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
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
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.json.*;

/**
 * Servlet implementation class FrontEndServlet
 */
@WebServlet({ "/FrontEndServlet", "/FES" })
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
	public void init(ServletConfig config) throws ServletException {
		// TODO Fill this in
	}

	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
		// Does nothing
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		doGetOrPost(request, response);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
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
		
		String[] emails = request.getParameterValues("email");
		String email = "";
		String bossEmail = "";

		if (emails == null || emails.length == 0) {
			return;

		} else if (emails.length == 2) {
			email = emails[0];
			bossEmail = emails[1];
		} else if (emails.length == 1) {
			email = emails[0];
			bossEmail = "noBoss@nomail.com";
		} else {
			return;
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
	        conf.set("hbase.zookeeper.quorum","hadoopdev008.eng.jiveland.com,hadoopdev002.eng.jiveland.com,hadoopdev001.eng.jiveland.com");
	        conf.set("hbase.zookeeper.property.clientPort","2181");
	        conf.set("hbase.hregion.max.filesize", "1073741824");
			//conf.addResource(new Path(
			//		"/Users/diivanand.ramalingam/Downloads/hbase/conf/hbase-site.xml"));

			//Create a table
			
			
			HTable table = new HTable(conf, "EmpBadges"); //Employee table
			HTable BadgeTable = new HTable(conf, "Badges"); //Badges table
			
			addUserOrUpdateBoss(table,email,bossEmail);

			Object[] badgeInfo = getBadges(table,email);
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
						JSONObject j = convertOutputToJSON(badgesWithDescription, BadgeTable, newBadges);
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
			
			resetNewBadges(table,email);

		} else {
			System.out.println("Invalid email address");
			return;
		}

	}

	public JSONObject convertOutputToJSON(String[] badges, HTable BadgeTable, String newBadges)
			throws JSONException, IOException {
		JSONObject j = new JSONObject();

		for (int i = 0; i < badges.length; i = i + 2) {
			System.out.println("Processing Badge No: " + badges[i]);
			String[] badgeInfo = getBadgeInfo(BadgeTable, badges[i]);
			
			
			JSONObject j2 = new JSONObject();
			j2.put("Name", badgeInfo[1]);
			j2.put("Description", badgeInfo[2]);
			j2.put("IconURL", badgeInfo[3]);
			if(newBadges.contains(badges[i])){
				j2.put("New", true);
			}else{
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

		for (Integer i = new Integer(1); i <= 30; i++) {

			if (!j.has(i.toString())) {
				j.put(i.toString(), j3);
			}
		}

		return j;
	}

	public static Object[] getBadges(HTable table, String email){
		Get get = new Get(Bytes.toBytes(email));	 
		Result data = null;
		Object[] output = new Object[2];
		ArrayList<String> resultingBadges = new ArrayList<String>();
		String newBadges = "";
		output[0] = resultingBadges;
		output[1] = newBadges;
		try {
		     data = table.get(get);
		} catch(Exception e) {
			System.err.println();
		}
		 
		if (data == null) {
			System.out.println("Not found");
			return output;
		}
		if(data.isEmpty()){
			return output;
		}
		 
		byte[] badges = Bytes.toBytes("Badge");

		NavigableSet<byte[]> badges_awarded = data.getFamilyMap(badges).descendingKeySet();
		for(byte[] badge: badges_awarded){
			resultingBadges.add(new String(badge));
		}
		newBadges = new String(data.getValue(Bytes.toBytes("Info"), Bytes.toBytes("newBadges")));
		
		/* checks for personal message, not used at the moment
		String[] result =  new String[resultingBadges.size()];
		result = resultingBadges.toArray(result);
		
		for(int i=0; i<result.length; i++){
			String customDescription = new String(data.getValue(Bytes.toBytes("Badge"), Bytes.toBytes(result[i])));
			int currentBadgeIndex = resultingBadges.indexOf(result[i]);
			resultingBadges.add(currentBadgeIndex+1, customDescription);
		}*/

		output[0] = resultingBadges;
		output[1] = newBadges;
		return output;
	}

	public static String[] getBadgeInfo(HTable table, String badgeNumber) {
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

	public static void addUserOrUpdateBoss(HTable table, String email,
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
	
	public static void resetNewBadges(HTable table, String email){
		Put row = new Put(Bytes.toBytes(email));
		

		row.add(Bytes.toBytes("Info"),Bytes.toBytes("newBadges"),Bytes.toBytes(""));
		
		try {
		    table.put(row);
		} catch(Exception e) {
			System.err.println();
		}
	}
	public static void test(){
		System.out.println("++++++++++++++");
		return;
	}

}

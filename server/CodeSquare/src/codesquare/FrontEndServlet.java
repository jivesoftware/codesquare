package codesquare;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableSet;

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

	protected void doGetOrPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// Get parameter info from Jive App
		String email = request.getParameter("email");
		if (email == null || email.length() == 0) {
			email = "noEmail@nomail.com";
			return;
		}

		Configuration conf = HBaseConfiguration.create();
		conf.addResource(new Path(
				"/Users/diivanand.ramalingam/Downloads/hbase/conf/hbase-site.xml"));

		HTable table = new HTable(conf, "EmpBadges");

		String[] badgesWithDescription = getBadges(table, email); // Elements in
																	// array
																	// have this
																	// invariant:
																	// {BadgeNumber1,Badge1Description,BadgeNumber2,Badge2Description,etc.}
		System.out.println();

		if (badgesWithDescription == null) {
			return;
		} else {
			try {
				JSONObject j = convertOutputToJSON(badgesWithDescription);
				// Output Area
				response.setContentType("application/json");
				OutputStream out = response.getOutputStream();
				response.setContentLength(j.toString().length());
				out.write(j.toString().getBytes());
				out.close(); // Closes the output stream
			} catch (JSONException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	public JSONObject convertOutputToJSON(String[] badges)
			throws JSONException, IOException {
		JSONObject j = new JSONObject();

		HTable BadgeTable = new HTable("Badges");
		
		for (int i = 0; i < badges.length; i = i + 2) {
			System.out.println("Processing Badge No: " + badges[i]);
			String[] badgeInfo = getBadgeInfo(BadgeTable, badges[i]);
			
			
			JSONObject j2 = new JSONObject();
			j2.put("Name", badgeInfo[1]);
			j2.put("Description", badgeInfo[2]);
			j2.put("IconURL", badgeInfo[3]);
			j2.put("CustomMsg", badges[i+1]);
			System.out.println(j2.toString());
			
			j.put(badges[i], j2);
			
		}

		return j;
	}

	public String[] getBadges(HTable table, String email) {
		Get get = new Get(Bytes.toBytes(email));
		Result data = null;
		ArrayList<String> resultingBadges = new ArrayList<String>();
		try {
			data = table.get(get);
		} catch (Exception e) {
			System.err.println();
		}

		if (data.isEmpty()) {
			System.out.println("Not found");
			return null;
		}

		byte[] badges = Bytes.toBytes("Badge");

		NavigableSet<byte[]> badges_awarded = data.getFamilyMap(badges)
				.descendingKeySet();
		for (byte[] badge : badges_awarded) {
			resultingBadges.add(new String(badge));
		}

		String[] result = new String[resultingBadges.size()];
		result = resultingBadges.toArray(result);

		for (int i = 0; i < result.length; i++) {
			String customDescription = new String(data.getValue(
					Bytes.toBytes("Badge"), Bytes.toBytes(result[i])));
			int currentBadgeIndex = resultingBadges.indexOf(result[i]);
			resultingBadges.add(currentBadgeIndex + 1, customDescription);
		}

		result = new String[resultingBadges.size()];
		return resultingBadges.toArray(result);
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

}

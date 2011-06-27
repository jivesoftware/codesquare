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
		conf.addResource(new Path("/Users/diivanand.ramalingam/Downloads/hbase/conf/hbase-site.xml"));
		
		
		HTable table = new HTable(conf,"EmpBadges");
	
		String[] badges = getBadges(table,email);
		
		
		if(badges == null){
			
		}else{
			JSONObject j = convertOutputToJSON(badges);
		
		//Output Area
		response.setContentType("application/json");
		OutputStream out = response.getOutputStream();
		response.setContentLength(j.toString().length());
		out.write(j.toString().getBytes());
		out.close(); // Closes the output stream
		}
		
	}

	public JSONObject convertOutputToJSON(String[] badges){
		JSONObject j = new JSONObject();
		
		for(int i = 0;i < badges.length;i++){
			try {
				j.append(badges[i], "1");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
			}
		}
		
		return j;
	}

	public String[] getBadges(HTable table, String email){
		Get get = new Get(Bytes.toBytes(email));	 
		Result data = null;
		ArrayList<String> resultingBadges = new ArrayList<String>();
		
		try {
		     data = table.get(get);
		} catch(Exception e) {
			System.err.println(e.getMessage());
			return null;
		}
		 
		
		if (data.isEmpty()) {
			System.out.println("Not found");
			return null;
		}
		 
		byte[] badges = Bytes.toBytes("Badge");
		
		NavigableSet<byte[]> badges_awarded = data.getFamilyMap(badges).descendingKeySet();
		for(byte[] badge: badges_awarded){
			resultingBadges.add(new String(badge));
		}
		String[] result = new String[resultingBadges.size()];
		return resultingBadges.toArray(result);
	}
	

}

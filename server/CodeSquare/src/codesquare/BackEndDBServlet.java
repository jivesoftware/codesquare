package codesquare;

import java.io.*;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class BackEndDBServlet
 */
@WebServlet({ "/BackEndDBServlet", "/BEDBS" })
public class BackEndDBServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public BackEndDBServlet() {
		super();
		// TODO Auto-generated constructor stub
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
			HttpServletResponse response) {
		String paramname = "start";
		boolean start = Boolean.parseBoolean(request.getParameter(paramname));
		System.out.println(start);
		if (start) {
			try {
				// Loading mySQL Driver
				Class.forName("com.mysql.jdbc.Driver");
				// Connect to CodeSquare database
				Connection con = DriverManager.getConnection(
						"jdbc:mysql://mysql:3306/codesquare", "codesquare",
						"codesquare");

				// Create the Query and print the results to .txt files for
				// mapreduce processing
				Statement stmt = con.createStatement();
				ResultSet rset = stmt
						.executeQuery("select * from COMMIT,EMPLOYEE where authorEmail=author_email");

				while (rset.next()) {
					int empId = rset.getInt("empId");
					String commitId = rset.getString("commitId");
					String authorName = rset.getString("authorName");
					String authorEmail = rset.getString("authorEmail");
					String day = rset.getString("day");
					String date = rset.getString("date");
					int hour = rset.getInt("hour");
					int minute = rset.getInt("minute");
					int second = rset.getInt("second");
					int numFilesChanged = rset.getInt("numFilesChanged");
					String filesChanged = rset.getString("filesChanged");
					filesChanged = filesChanged.replace(' ', ',');
					filesChanged = "[" + filesChanged + "]";
					int insertions = rset.getInt("insertions");
					int deletions = rset.getInt("deletions");
					String message = rset.getString("message");
					PrintWriter out = new PrintWriter(new FileWriter(new File(
							"/Users/diivanand.ramalingam/Desktop/input/"
									+ commitId + ".txt")));
					String output = commitId + " " + empId + " " + day + " "
							+ date + " " + hour + " " + minute + " " + second
							+ " " + filesChanged + " " + insertions + " "
							+ deletions + " " + message;
					System.out.println(output.length());
					out.print(output);
					out.close();
				}

			} catch (Exception e) {
				System.err.println(e.getMessage());
			}

		}
	}

}

package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dropbox.DBClient;

/**
 * Servlet implementation class DBServlet
 */
@WebServlet("/DBServlet")
public class DBServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DBServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//doGet(request, response);
		
		DBClient dbClient = new DBClient();
		String type = request.getParameter("type").toString();		
		
		PrintWriter out = response.getWriter();
		//System.out.println("DEBUG: doPost type=" + type);
		
		try {
			switch (type) {
			case "access": 
				//System.out.println("DEBUG: access");
				out.write(dbClient.sendRequest());
				break;
			case "token":
				String code = request.getParameter("code").toString();
				//System.out.println("DEBUG: code=" + code);
				out.write(dbClient.accessToken(code));
				break;
			case "getAccInfo":
				String access_token = request.getParameter("access_token").toString();
				String account_id = request.getParameter("account_id").toString();
				//System.out.println("DEBUG: access_token=" + access_token + " account_id=" + account_id);
				out.write(dbClient.getAccountInfo(access_token, account_id));
				break;
			case "uploadFile":
				access_token = request.getParameter("access_token").toString();
				String path = request.getParameter("path").toString();
				//System.out.println("DEBUG: access_token=" + access_token + " path=" + path);
				
				if (path.equals(""))
				{
					out.write("error: Please, provide a path of file to be uploaded!");
					break;
				}
				System.out.println("DEBUG: fuck");
				out.write(dbClient.uploadFile(access_token, path));
				break;
			default:
				break;
			}
			
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		out.flush();
	    out.close();
	}

}

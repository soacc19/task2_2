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
		String access_token, sourcePath, remotePath;
		
		PrintWriter out = response.getWriter();
		
		try {
			switch (type) {
			case "access": 
				out.write(dbClient.sendRequest());
				break;
			case "token":
				String code = request.getParameter("code").toString();
				out.write(dbClient.accessToken(code));
				break;
			case "getAccInfo":
			    access_token = request.getParameter("access_token").toString();
				String account_id = request.getParameter("account_id").toString();
				out.write(dbClient.getAccountInfo(access_token, account_id));
				break;
			case "uploadFile":
				access_token = request.getParameter("access_token").toString();
				sourcePath = request.getParameter("sourcePath").toString();
				remotePath =  request.getParameter("remotePath").toString();
				//System.out.println("DEBUG: access_token=" + access_token + " path=" + path);
				
				if (sourcePath.equals(""))
				{
					out.write("Error: Please, provide a path of file to be uploaded!");
					break;
				}
				out.write(dbClient.uploadFile(access_token, sourcePath, remotePath));
				break;
			case "downloadFile":
				access_token = request.getParameter("access_token").toString();
				sourcePath = request.getParameter("sourcePath").toString();
				remotePath =  request.getParameter("remotePath").toString();
				
				if (sourcePath.equals("") || remotePath.equals(""))
				{
					out.write("Error: Please, provide a path of file to be downloaded!");
					break;
				}
				out.write(dbClient.downloadFile(access_token, sourcePath, remotePath));
				break;
			case "deleteFile":
				access_token = request.getParameter("access_token").toString();
				remotePath = request.getParameter("remotePath").toString();
				
				if (remotePath.equals(""))
				{
					out.write("Error: Please, provide a path of file to be deleted!");
					break;
				}
		
				out.write(dbClient.deleteFile(access_token, remotePath));
				break;
			case "showAppFolder":
				access_token = request.getParameter("access_token").toString();
				remotePath = request.getParameter("remotePath").toString();
				String include_non_downloadable_files = request.getParameter("include_non_downloadable_files").toString();
		
				out.write(dbClient.showAppFolder(access_token, remotePath, include_non_downloadable_files));
				break;

			default:
				break;
			}
			
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			out.write("Error: URI syntax error!");
		}
		
		out.flush();
	    out.close();
	}

}

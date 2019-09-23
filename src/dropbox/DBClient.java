package dropbox;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.sun.org.apache.xerces.internal.util.URI;

public class DBClient {
	private static final String APP_KEY = "4v49nli2mnbtdkd"; // get from AppConsole when create the DropBox App
	private static final String APP_SECRET = "9aas46t4hlspmpx";
	private static final String REDIRECT_URI = "http://localhost:8080/SOACC_Task2_2/"; // any url to where you want to redirect the user
	//private static final String APP_NAME = "soa_cc_task2_2";
	private static final String APP_FOLDER = "/upload_test/";
	
	//FIXME get file path from input box!
	private static final String LOCAL_FILE = "/home/maros/test_upload.txt";
	
	//------------------------------------------------------------------------------------------------
	// basically builds corresponding GET request that will be returned to the front-end...
	public String sendRequest() throws URISyntaxException, IOException {
			URI uri=new URI("https://www.dropbox.com/oauth2/authorize");
			
			StringBuilder requestUri = new StringBuilder(uri.toString());
			requestUri.append("?client_id=");
			requestUri.append(URLEncoder.encode(APP_KEY, "UTF-8"));
			requestUri.append("&response_type=code");
			requestUri.append("&redirect_uri=" + REDIRECT_URI);
		
			return requestUri.toString();
	}
	
	//------------------------------------------------------------------------------------------------
	public String accessToken(String codeStr) throws URISyntaxException, IOException {
		String code = "" + codeStr;		// code get from previous step
		StringBuilder tokenUri = new StringBuilder("code=");
	
		tokenUri.append(URLEncoder.encode(code,"UTF-8"));
		tokenUri.append("&grant_type=");
		tokenUri.append(URLEncoder.encode("authorization_code","UTF-8"));
		tokenUri.append("&client_id=");
		tokenUri.append(URLEncoder.encode(APP_KEY,"UTF-8"));
		tokenUri.append("&client_secret=");
		tokenUri.append(URLEncoder.encode(APP_SECRET,"UTF-8"));
		tokenUri.append("&redirect_uri=" + REDIRECT_URI);
		
		URL url=new URL("https://api.dropbox.com/oauth2/token");
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		StringBuffer response = new StringBuffer();
		
		try {
			connection.setDoOutput(true);	
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Length", "" + tokenUri.toString().length());
			
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
			outputStreamWriter.write(tokenUri.toString());
			outputStreamWriter.flush();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			
			
			while ((inputLine =	in.readLine()) != null) {
				response.append(inputLine);
			}			
			in.close();			
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			connection.disconnect();
		}
		
		return response.toString();
	}
	
	//------------------------------------------------------------------------------------------------
	public String getAccountInfo(String tokenStr, String accountIDStr) throws URISyntaxException, IOException {
		String access_token = "" + tokenStr;
		String content = "{\"account_id\": \"" + accountIDStr + "\"}";
		URL url = new URL("	https://api.dropboxapi.com/2/users/get_account");	
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		StringBuffer response = new StringBuffer();
		
		try {
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Authorization", "Bearer " + access_token);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Content-Length", "" + content.length());
			
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
			outputStreamWriter.write(content);
			outputStreamWriter.flush();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null) { 
				response.append(inputLine);
			}
			
			in.close();			
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			connection.disconnect();
		}
		
		return response.toString();
	}
	
	//------------------------------------------------------------------------------------------------
	public String uploadFile(String token, String path) throws URISyntaxException, IOException {
		String access_token = "" + token;
		String sourcePath = "" + path; 		// required file path on local file system
		File fileToUpload = new File(sourcePath);
		
		Path pathFile = Paths.get(sourcePath);
		byte[] data = Files.readAllBytes(pathFile);
		
		// name of file is kept
		String content = "{\"path\": \"" + APP_FOLDER + fileToUpload.getName() + "\","
				         + "\"mode\":\"add\",\"autorename\": true,\"mute\": false,\"strict_conflict\": false}";

		URL url = new URL("https://content.dropboxapi.com/2/files/upload");
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		StringBuffer response = new StringBuffer();
		
		try {
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Authorization", "Bearer "+access_token);
			connection.setRequestProperty("Content-Type", "application/octet-stream");
			connection.setRequestProperty("Dropbox-API-Arg", "" + content);
			connection.setRequestProperty("Content-Length", String.valueOf(data.length));
			
			OutputStream outputStream = connection.getOutputStream();
			outputStream.write(data);
			outputStream.flush();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
						
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close(); 
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			
			response.append("error: Unexpected error! Please, check parameters!");
		}
		finally {
			connection.disconnect();
		}
	
		return response.toString();
	}
	
	//------------------------------------------------------------------------------------------------
	public String showAppFolder(String token, String path, String include_non_downloadable_files) throws URISyntaxException, IOException {
		String access_token = "" + token;
		
		// 
		String content = "{\"path\": \"" + path + "\","
				         + "\"recursive\": true, \"include_non_downloadable_files\": " + include_non_downloadable_files + "}";
		
		URL url = new URL("https://api.dropboxapi.com/2/files/list_folder");
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		StringBuffer response = new StringBuffer();
		
		try {
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Authorization", "Bearer "+access_token);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Content-Length", "" + content.length());
			
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
			outputStreamWriter.write(content);
			outputStreamWriter.flush();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null) { 
				response.append(inputLine);
			}
			
			in.close();			
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			
			response.append("error: Unexpected error! Please, check parameters!");
		}
		finally {
			connection.disconnect();
		}
	
		return response.toString();
	}

}

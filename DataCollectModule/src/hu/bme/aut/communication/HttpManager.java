package hu.bme.aut.communication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

/**
 * Manage the HTTP communication with the server.
 * @author Eva Pataji
 *
 */
public class HttpManager {
	
	public interface HttpManagerListener {
		public void responseArrived(String response);
		public void errorOccuredDuringParse(String error);
		public void errorOccured(String error);
	}
	
	private HttpManagerListener listener;
	
	public HttpManager(HttpManagerListener aListener)
	{
		listener = aListener;
	}
	
	
	/**
	 * Send a GET request.
	 * @param url
	 * @return
	 */
	public void sendGetRequest(String url)
	{
	  	HttpClient httpclient = new DefaultHttpClient();
	    HttpGet httpget = new HttpGet(url); 
	    HttpResponse response;
	    try {
	        response = httpclient.execute(httpget);
	        handleJSONResponse(response);
	    } catch (Exception e) {
	    	listener.errorOccured("Error occured during GET.");
	    	e.printStackTrace();
		}
	    
		
	}

	/**
	 * Send a POST request to the server.
	 * @param url
	 */
	public void sendPostRequest(String url, String JSobject)
	{
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);
		try {
			httppost.setEntity(new ByteArrayEntity(
				    JSobject.getBytes("UTF8")));
			
			HttpResponse response = httpclient.execute(httppost);
			
			handleJSONResponse(response);
		} catch (Exception e) {
			listener.errorOccured( "Error occured during POST.");
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Handle the server response.
	 * @param response
	 */
	private void handleJSONResponse(HttpResponse response)
	{
		InputStream is = null;
		 try {
		        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
		        {
			        HttpEntity entity = response.getEntity();
			        if (entity != null) {
			            is = entity.getContent();
			            ByteArrayOutputStream bos = new ByteArrayOutputStream();
						int inChar;
						while ((inChar = is.read()) != -1)
						{
							bos.write(inChar);
						}
						
						listener.responseArrived(bos.toString());
			        }
			        else
			        	listener.errorOccured("HttpEntity is empty"); // Error
		        }
		    } catch (Exception e) {
		    	listener.errorOccured(e.getMessage()); // Error
		    } finally {
		    	if (is != null)
		    	{
		    		try {
		    			is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
		    	}
		    }
	}
}


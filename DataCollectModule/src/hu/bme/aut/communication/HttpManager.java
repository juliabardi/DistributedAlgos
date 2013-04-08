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
	
	
	/**
	 * Send a GET request.
	 * @param url
	 * @return
	 */
	public static String sendGetRequest(String url)
	{
	  	HttpClient httpclient = new DefaultHttpClient();
	    HttpGet httpget = new HttpGet(url); 
	    HttpResponse response;
	    try {
	        response = httpclient.execute(httpget);
	        return handleJSONResponse(response);
	    } catch (Exception e) {
			e.printStackTrace();
		}
	    
		return "Error occured GET.";
	}

	/**
	 * Send a POST request to the server.
	 * @param url
	 */
	public static String sendPostRequest(String url, JSONObject JSobject)
	{
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);
		try {// Post adat összeállítása
		httppost.setEntity(new ByteArrayEntity(
			    JSobject.toString().getBytes("UTF8")));
		// HTTP Post kérés végrehajtása
		HttpResponse response = httpclient.execute(httppost);
		// … válasz feldolgozása
		return handleJSONResponse(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Error occured POST.";
	}
	
	/**
	 * Handle the server response.
	 * @param response
	 */
	private static String handleJSONResponse(HttpResponse response)
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
						
						return bos.toString();
			        }
			        else
			        	return "HttpEntity is empty"; //error
		        }
		    } catch (Exception e) {
		    	return e.getMessage(); //error
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
		return "Error occured in handling the response.";
	}
}


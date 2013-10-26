package hu.bme.aut.communication.helpers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import android.util.Base64;
import android.util.Log;

/**
 * Manage the HTTP communication with the server.
 * @author Eva Pataji
 *
 */
public class HttpManager {
	
	public interface HttpManagerListener {
		public void responseArrived(String response);
		public void errorOccuredDuringHandleResponse(String error);
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
	  	sendGet(httpclient,httpget);		
	}
		
	public void sendSecureGetRequest(String url, int portNumber){
		HttpClient httpclient = getNewHttpClient(portNumber);
		HttpGet httpget = new HttpGet(url);
	    httpget.addHeader("Authorization", "Basic " + createBasicAuthHeader());   
	  	sendGet(httpclient,httpget);			   
	}
		
	private void sendGet(HttpClient httpclient, HttpGet httpget){
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
		try {
			this.sendPostRequest(url, JSobject.getBytes("UTF8"));
		} catch (UnsupportedEncodingException e) {
			listener.errorOccured( "Error occured during POST.");
			e.printStackTrace();
		}		
	}
	
	public void sendPostRequest(String url, byte[] message)
	{
		HttpClient httpclient = new DefaultHttpClient(this.getHttpParams());
		HttpPost httppost = new HttpPost(url);
		sendPost(httpclient,httppost, message);
	}
	
	public void sendSecurePostRequest(String url, String JSobject,int port){
		HttpClient httpclient = getNewHttpClient(port);
		HttpPost httppost = new HttpPost(url);
		httppost.addHeader("Authorization", "Basic " + createBasicAuthHeader());
		try {
			this.sendPost(httpclient, httppost, JSobject.getBytes("UTF8"));
		} catch (UnsupportedEncodingException e) {
			listener.errorOccured( "Error occured during POST.");
			e.printStackTrace();
		}	
			 
	}
		
	private void sendPost(HttpClient httpclient,HttpPost httppost,byte[] message){
		try {
			httppost.setEntity(new ByteArrayEntity(message));			
			HttpResponse response = httpclient.execute(httppost);			
			handleJSONResponse(response);
		} catch (Exception e) {
			listener.errorOccured( "Error occured during POST.");
			e.printStackTrace();
		}
	}
	
	private HttpParams getHttpParams(){
		
		HttpParams httpParameters = new BasicHttpParams();
		int timeoutConnection = 3000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		int timeoutSocket = 5000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		return httpParameters;
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
			        	listener.errorOccuredDuringHandleResponse("HttpEntity is empty"); // Error
		        }
		    } catch (Exception e) {
		    	listener.errorOccuredDuringHandleResponse(e.getMessage()); // Error
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
	
	private String createBasicAuthHeader(){
		String credentials = "algos" + ":" + "algos"; // TODO passwd, jelszo...
	    String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
	    return base64EncodedCredentials;
	}
	
	/**
	 * Creates a new HttpClient which will work with self-signed certificates.
	 * @return
	 */
	private HttpClient getNewHttpClient(int portNumber) {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
     
            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
          
            SchemeRegistry registry = new SchemeRegistry();
//          registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 3001));
            registry.register(new Scheme("https", sf, portNumber));
     
            ClientConnectionManager ccm = new ThreadSafeClientConnManager(this.getHttpParams(), registry);
     
            return new DefaultHttpClient(ccm, this.getHttpParams());
        } catch (Exception e) {
        	Log.i(this.getClass().getName(),e.toString());
            return new DefaultHttpClient();
        }
    }
}


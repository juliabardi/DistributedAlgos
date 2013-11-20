/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hu.bme.aut.communication.GCM;

import com.google.android.gcm.GCMRegistrar;

import static hu.bme.aut.communication.GCM.CommonUtilities.TAG;
import static hu.bme.aut.communication.GCM.CommonUtilities.displayMessage;
import android.content.Context;
import android.provider.SyncStateContract.Constants;
import android.util.Log;

import hu.bme.aut.communication.helpers.MySSLSocketFactory;
import hu.bme.aut.communication.utils.HttpParamsUtils;
import hu.bme.aut.datacollect.activity.R;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLSocketFactory;

/**
 * Helper class used to communicate with the demo server.
 */
public final class ServerUtilities {

    private static final int MAX_ATTEMPTS = 1;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();

    /**
     * Register this account/device pair within the server.
     *
     * @return whether the registration succeeded or not.
     */
    public static boolean register(final Context context, final String regId) {
        Log.i(TAG, "registering device (regId = " + regId + ")");
        String serverUrl = HttpParamsUtils.getFullGcmAddress(context);
        Log.i(ServerUtilities.class.getName(), "ServerUrl: " + serverUrl);
        Map<String, String> params = new HashMap<String, String>();
        params.put("regId", regId);
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        // Once GCM returns a registration id, we need to register it in the
        // demo server. As the server might be down, we will retry it a couple
        // times.
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            Log.d(TAG, "Attempt #" + i + " to register");
            try {
                displayMessage(context, context.getString(
                        R.string.server_registering, i, MAX_ATTEMPTS));
                if(serverUrl.startsWith(hu.bme.aut.communication.Constants.HTTPS)){
                	Log.i(ServerUtilities.class.getName(), "Secure connection.");
                	postSecure(serverUrl + "registerSecure", params);
                }else{
                	Log.i(ServerUtilities.class.getName(), "Simple connection.");
                	post(serverUrl + "register", params);
                }
                GCMRegistrar.setRegisteredOnServer(context, true);
                String message = context.getString(R.string.server_registered);
                CommonUtilities.displayMessage(context, message);
                return true;
            } catch (IOException e) {
                // Here we are simplifying and retrying on any error; in a real
                // application, it should retry only on unrecoverable errors
                // (like HTTP error code 503).
                Log.e(TAG, "Failed to register on attempt " + i, e);
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    // Activity finished before we complete - exit.
                    Log.d(TAG, "Thread interrupted: abort remaining retries!");
                    Thread.currentThread().interrupt();
                    return false;
                }
                // increase backoff exponentially
                backoff *= 2;
            }
        }
        String message = context.getString(R.string.server_register_error,
                MAX_ATTEMPTS);
        CommonUtilities.displayMessage(context, message);
        return false;
    }

    /**
     * Unregister this account/device pair within the server.
     */
   public static void unregister(final Context context, final String regId) {
        Log.i(TAG, "unregistering device (regId = " + regId + ")");
        String serverUrl = HttpParamsUtils.getFullGcmAddress(context);
        Map<String, String> params = new HashMap<String, String>();
        params.put("regId", regId);
        try {
            GCMRegistrar.setRegisteredOnServer(context, false); // Asked by dec_admin server side to always retry connecting at communication module startup.
            if(serverUrl.startsWith(hu.bme.aut.communication.Constants.HTTPS)){
            	Log.i(ServerUtilities.class.getName(), "Secure connection.");
            	postSecure(serverUrl + "unregisterSecure", params);
        	}else{
        		Log.i(ServerUtilities.class.getName(), "Simple connection.");
        		post(serverUrl + "unregister", params);
        	}
            String message = context.getString(R.string.server_unregistered);
            CommonUtilities.displayMessage(context, message);
        } catch (IOException e) {
            // At this point the device is unregistered from GCM, but still
            // registered in the server.
            // We could try to unregister again, but it is not necessary:
            // if the server tries to send a message to the device, it will get
            // a "NotRegistered" error message and should unregister the device.
            String message = context.getString(R.string.server_unregister_error,
                    e.getMessage());
            CommonUtilities.displayMessage(context, message);
        }
    }

    /**
     * Issue a POST request to the server.
     *
     * @param endpoint POST address.
     * @param params request parameters.
     *
     * @throws IOException propagated from POST.
     */
    private static void post(String endpoint, Map<String, String> params)
            throws IOException {
        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
            Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }
        String body = bodyBuilder.toString();
        Log.v(TAG, "Posting '" + body + "' to " + url);
        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            // handle the response
            int status = conn.getResponseCode();
            if (status != 200) {
              throw new IOException("Post failed with error code " + status);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
      }
    
    /**
     * Issue a POST request to the server.
     *
     * @param endpoint POST address.
     * @param params request parameters.
     *
     * @throws IOException propagated from POST.
     */
    private static void postSecure(String endpoint, Map<String, String> params)
            throws IOException {
        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
            Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }
        String body = bodyBuilder.toString();
        Log.v(TAG, "Posting '" + body + "' to " + url);
        byte[] bytes = body.getBytes();
        HttpsURLConnection conn = null;
        try {
            trustAllHosts();
            conn = (HttpsURLConnection) url.openConnection();
            conn.setHostnameVerifier(DO_NOT_VERIFY);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            conn.setRequestProperty("Authorization", "Basic " + HttpParamsUtils.createBasicAuthHeader());
            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            // handle the response
            int status = conn.getResponseCode();
            if (status != 200) {
              throw new IOException("Post failed with error code " + status);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
      }
        
 
    /**
     * Trust every server - dont check for any certificate
     * http://stackoverflow.com/questions/995514/https-connection-android
     */
    private static void trustAllHosts() {
    	// Create a trust manager that does not validate certificate chains
    	TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
    		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
    			return new java.security.cert.X509Certificate[] {};
    		}

    		public void checkClientTrusted(X509Certificate[] chain,
    				String authType) throws CertificateException {
    		}

    		public void checkServerTrusted(X509Certificate[] chain,
    				String authType) throws CertificateException {
    		}
    	} };

    	// Install the all-trusting trust manager
    	try {
    		SSLContext sc = SSLContext.getInstance("TLS");
    		sc.init(null, trustAllCerts, new java.security.SecureRandom());
    		HttpsURLConnection
    				.setDefaultSSLSocketFactory(sc.getSocketFactory());
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
 // always verify the host - dont check for certificate
    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
    	public boolean verify(String hostname, SSLSession session) {
    		return true;
    	}
    };

}

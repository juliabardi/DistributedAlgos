package hu.bme.aut.communication.utils;

import hu.bme.aut.communication.Constants;
import hu.bme.aut.datacollect.activity.DataCollectService;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

public class HttpParamsUtils {

	public static String getFullGcmAddress(Context context){
		StringBuilder builder = new StringBuilder();
		builder.append(HttpParamsUtils.getGcmServerProtocol(context));
		builder.append("://");
		builder.append(HttpParamsUtils.getGcmIpServerAddress(context));
		String port = HttpParamsUtils.getGcmServerPort(context);
		if(!port.trim().equals("")){
			builder.append(":");
			builder.append(port);}
		//builder.append("/DistributedEnvironmentCoordination");
		builder.append("/");
		return builder.toString();
	}

	public static String getFullNodeAddress(Context context){
		StringBuilder builder = new StringBuilder();
		builder.append(HttpParamsUtils.getNodeServerProtocol(context));
		builder.append("://");
		builder.append(HttpParamsUtils.getNodeIpServerAddress(context));
		String port =HttpParamsUtils.getNodeServerPort(context);
		if(!port.trim().equals("")){
			builder.append(":");
			builder.append(port);}
		builder.append("/");
		return builder.toString();
	}
	
	/**
	 * Some requests must work only under HTTPS.
	 * @param context
	 * @param protocol
	 * @return
	 */
	public static String getFullNodeAddressProtocol(Context context, String protocol){
		StringBuilder builder = new StringBuilder();
		builder.append(protocol);
		builder.append("://");
		builder.append(HttpParamsUtils.getNodeIpServerAddress(context));
		String port = HttpParamsUtils.getNodeServerPortProtocol(context, protocol);
		if(!port.trim().equals("")){
			builder.append(":");
			builder.append(HttpParamsUtils.getNodeServerPortProtocol(context, protocol));}
		builder.append("/");
		return builder.toString();
	}

	public static String getNodeServerPort(Context context){
	   	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
	   	if(HttpParamsUtils.getNodeServerProtocol(context).equals(Constants.HTTP))
			return sharedPrefs.getString(DataCollectService.DEC_NODE_PORT, Constants.NodeServerPort);
		else{
			return sharedPrefs.getString(DataCollectService.DEC_NODE_PORT_HTTPS, Constants.NodeServerPortHttps);
		}
	}
	
	public static String getNodeServerPortProtocol(Context context, String protocol){
	   	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
	   	if(protocol.equals(Constants.HTTP))
			return sharedPrefs.getString(DataCollectService.DEC_NODE_PORT, Constants.NodeServerPort);
		else{
			return sharedPrefs.getString(DataCollectService.DEC_NODE_PORT_HTTPS, Constants.NodeServerPortHttps);
		}
	}
	
	public static String getGcmServerPort(Context context){
	   	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
	   	if(HttpParamsUtils.getGcmServerProtocol(context).equals(Constants.HTTP))
			return sharedPrefs.getString(DataCollectService.DEC_ADMIN_PORT, Constants.GCMServerPort);
		else{
			return sharedPrefs.getString(DataCollectService.DEC_ADMIN_PORT_HTTPS, Constants.GCMServerPortHttps);
		}
	}

	/**
	 * Default
	 * @param context
	 * @return
	 */
	public static String getDataCollectorServerPort(Context context){
	   	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
	   	if(HttpParamsUtils.getDataCollectorServerProtocol(context).equals(Constants.HTTP))
	   		return sharedPrefs.getString(DataCollectService.DATA_COLLECTOR_PORT, Constants.DataCollectorServerPort);
		else{
			return sharedPrefs.getString(DataCollectService.DATA_COLLECTOR_PORT_HTTPS, Constants.DataCollectorServerPortHttps);
		} 
	}
	
	/**
	 * Protocol-dependent
	 * @param context
	 * @param protocol
	 * @return
	 */
	public static String getDataCollectorServerPortProtocol(Context context, String protocol){
	   	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
	   	if(protocol.equals(Constants.HTTP))
	   		return sharedPrefs.getString(DataCollectService.DATA_COLLECTOR_PORT, Constants.DataCollectorServerPort);
		else{ // HTTPS
			return sharedPrefs.getString(DataCollectService.DATA_COLLECTOR_PORT_HTTPS, Constants.DataCollectorServerPortHttps);
		} 
	}

	public static String getDataCollectorServerProtocol(Context context){
		
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		return  sharedPrefs.getString(DataCollectService.DATA_COLLECTOR_PROTOCOL, Constants.DataCollectorServerProtocol); 
	}

	public static String getGcmServerProtocol(Context context){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		return  sharedPrefs.getString(DataCollectService.DEC_ADMIN_PROTOCOL, Constants.GCMServerProtocol); 
	}

	public static String getNodeServerProtocol(Context context){
	    	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
	    	return  sharedPrefs.getString(DataCollectService.DEC_NODE_PROTOCOL, Constants.NodeServerProtocol); 
	}

	private static String getDataCollectorIpServerAddress(Context context){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		return  sharedPrefs.getString(DataCollectService.DEC_NODE_IP, Constants.NodeServerIP);
	}

	private static String getGcmIpServerAddress(Context context){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		return  sharedPrefs.getString(DataCollectService.DEC_ADMIN_IP, Constants.GCMServerIP);
	}

	private static String getNodeIpServerAddress(Context context){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()); 	
		return  sharedPrefs.getString(DataCollectService.DEC_NODE_IP, Constants.NodeServerIP);
	}

	public static String createBasicAuthHeader(){
		String credentials = Constants.USER + ":" + Constants.PASSWORD; 
	    String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
	    return base64EncodedCredentials;
	}
	
	

}

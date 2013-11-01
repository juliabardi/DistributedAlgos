package hu.bme.aut.communication.utils;

import hu.bme.aut.communication.Constants;
import hu.bme.aut.datacollect.activity.DataCollectService;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class HttpParamsUtils {

	public static String getFullGcmAddress(Context context){
		StringBuilder builder = new StringBuilder();
		builder.append(HttpParamsUtils.getGcmServerProtocol(context));
		builder.append("://");
		builder.append(HttpParamsUtils.getGcmIpServerAddress(context));
		builder.append(":");
		builder.append(HttpParamsUtils.getGcmServerPort(context));
		builder.append("/");
		return builder.toString();
	}

	public static String getFullNodeAddress(Context context){
		StringBuilder builder = new StringBuilder();
		builder.append(HttpParamsUtils.getNodeServerProtocol(context));
		builder.append("://");
		builder.append(HttpParamsUtils.getNodeIpServerAddress(context));
		builder.append(":");
		builder.append(HttpParamsUtils.getNodeServerPort(context));
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
	
	public static String getGcmServerPort(Context context){
	   	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
	   	if(HttpParamsUtils.getGcmServerProtocol(context).equals(Constants.HTTP))
			return sharedPrefs.getString(DataCollectService.DEC_ADMIN_PORT, Constants.GCMServerPort);
		else{
			return sharedPrefs.getString(DataCollectService.DEC_ADMIN_PORT_HTTPS, Constants.GCMServerPortHttps);
		}
	}

	public static String getDataCollectorServerPort(Context context){
	   	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
	   	if(HttpParamsUtils.getDataCollectorServerProtocol(context).equals(Constants.HTTP))
	   		return sharedPrefs.getString(DataCollectService.DATA_COLLECTOR_PORT, Constants.DataCollectorServerPort);
		else{
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

}

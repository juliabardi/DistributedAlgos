package hu.bme.aut.communication;

import hu.bme.aut.datacollect.activity.DataCollectService;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Constants used for communication.
 * @author Eva Pataji
 *
 */
public class Constants {
	//Registration specific, only these messages can be sent to the server
	public static final String ALGTYPE_DIST_ALGOS = "DistributedAlgos";
	public static final String REGISTER = "register";
	public static final String UNREGISTER = "unregister";
	public static final String NEED = "need";
	public static final String OFFER = "offer";
	public static final String UNREGISTER_OFFER = "unregisterOffer";
	
	public static final String ALGTYPE ="algType";
	public static final String PARAM_NAME ="name";
	
	// For basic auth test
	public static final String USER ="algos";
	public static final String PASSWORD ="algos";
	
	//For push
	public static final String REQUEST_PARAMS ="requestParams";
	public static final String REQUEST_TIME ="time";
	public static final String REQUEST_COLUMNS ="columns";
	public static final String REQUEST_ID ="requestId";
	public static final String REQUEST_PORT ="port";
	public static final String REQUEST_PROTOCOL ="protocol";
	public static final String REQUEST_DATE = "date";
	public static final String REQUEST_RECURRENCE = "recurrence";
	
	// For syncronization
	public static final String MESSAGE_TYPE = "messageType";
	public static final String ITEM_NAME = "itemName";
	public static final String ITEM_SYNC_VALUE = "itemValue";
	public static final String DISTRUBUTED_ALGOS_AVAIABLE_VALUE = "DistributedAlgosAvaiableValue"; // If node server was avaiable at last request
	
	
	// Server specific
	public static String NodeServerIP = "192.168.1.113";
	public static String GCMServerIP = "192.168.1.113";
	public static String NodeServerPort = "3000";
	public static String DataCollectorServerPort = "3003";
	public static String GCMServerPort = "8080";
	public static String NodeServerProtocol = "http";
	public static String DataCollectorServerProtocol = "https";
	
	public static String getNodeServerAddress(Context context){
    	
    	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    	return String.format("://%s:%s/", 
    			sharedPrefs.getString(DataCollectService.DEC_NODE_IP, Constants.NodeServerIP), 
    			sharedPrefs.getString(DataCollectService.DEC_NODE_PORT, Constants.NodeServerPort));
    }
	
	 public static String getNodeServerProtocol(Context context){
	    	
	    	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
	    	return  sharedPrefs.getString(DataCollectService.DEC_NODE_PROTOCOL, Constants.NodeServerProtocol); 
	    }
    
    public static String getDataCollectorServerAddress(Context context){
    	
    	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    	return String.format("://%s:%s/", 
    			sharedPrefs.getString(DataCollectService.DEC_NODE_IP, Constants.NodeServerIP), 
    			DataCollectorServerPort);
    }
    
    public static String getDataCollectorServerProtocol(Context context){
    	
    	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    	return  sharedPrefs.getString(DataCollectService.DATA_COLLECTOR_PROTOCOL, Constants.DataCollectorServerProtocol); 
    }
    
    public static String getGCMServerAddress(Context context){
    	
    	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    	return String.format("http://%s:%s/DistributedEnvironmentCoordination", 
    			sharedPrefs.getString(DataCollectService.DEC_ADMIN_IP, Constants.GCMServerIP), 
    			sharedPrefs.getString(DataCollectService.DEC_ADMIN_PORT, Constants.GCMServerPort));
    }
    
    public static String getNodeServerPort(Context context){
       	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
       	return  sharedPrefs.getString(DataCollectService.DEC_NODE_PORT, Constants.NodeServerPort);
    }
    
    public static String getDataCollectorServerPort(Context context){
       	return Constants.DataCollectorServerPort; 
    }

    
	//GCM specific
	public static final String OFFER_REQUEST="offerRequest";
	public static final String OFFER_REPLY="offerReply";
	
	public static final String NOT_DEFINED="NotDefinedCommand";
	public static final String JSON_PARSE_ERROR="JsonParseError";
	
	public static final String REQUEST_ADDRESS = "requestAddress";
}

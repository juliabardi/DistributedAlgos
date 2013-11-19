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
	public static final String UPDATE_CONNECTION = "updateConnection";
	
	public static final String ALGTYPE ="algType";
	public static final String PARAM_NAME ="name";
	public static final String SCRIPT = "script";
	
	public static final String HTTP ="http";
	public static final String HTTPS ="https";
	
	// For basic auth test
	public static final String USER ="algos";
	public static final String PASSWORD ="algos";
	
	public static final String REGISTER_USER="registerUser";
	public static final String LOGIN_USER="loginUser";
	
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
	public static final String NodeServerIP = "192.168.1.113";
	public static final String GCMServerIP = "192.168.1.113";
	public static final String DataCollectorServerIP = "192.168.1.113";
	public static final String NodeServerPort = "3000";
	public static final String NodeServerPortHttps = "3002";
	public static final String DataCollectorServerPort = "3001";
	public static final String DataCollectorServerPortHttps = "3003";
	public static final String GCMServerPort = "8080";
	public static final String GCMServerPortHttps = "8443";
	public static final String GCMServerProtocol = "http";
	public static final String NodeServerProtocol = "http";
	public static final String DataCollectorServerProtocol = "https";
	
	public static String getDeviceIP(Context context){
    	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
       	return  sharedPrefs.getString(DataCollectService.DEVICE_IP_WIFI, "");
    }
    
    //GCM specific
	public static final String OFFER_REQUEST="offerRequest";
	public static final String OFFER_REPLY="offerReply";
	
	public static final String NOT_DEFINED="NotDefinedCommand";
	public static final String JSON_PARSE_ERROR="JsonParseError";
	
	public static final String REQUEST_ADDRESS = "requestAddress";
}

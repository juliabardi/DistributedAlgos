package hu.bme.aut.communication;

/**
 * Constants used for communication.
 * @author Eva Pataji
 *
 */
public class Constants {
	//Registration specific, only these messages can be sent to the server
	public static final String ALGTYPE_DIST_ALGOS = "DistributedAlgos";
	public static final String REGISTER = "register";
	public static final String NEED = "need";
	public static final String OFFER = "offer";
	public static final String UNREGISTER_OFFER = "unregisterOffer";
	
	public static final String ALGTYPE ="algType";
	public static final String PARAM_NAME ="name";
	
	// For syncronization
	public static final String MESSAGE_TYPE = "messageType";
	public static final String ITEM_NAME = "itemName";
	public static final String ITEM_SYNC_VALUE = "itemValue";
	public static final String GCM_REG_ACTION = "GCM_Rec_State";
	public static final String GCM_REG_MSG = "Gcm_Reg_Msg";
	public static final String DISTRUBUTED_ALGOS_AVAIABLE_VALUE = "DistributedAlgosAvaiableValue"; // If node server was avaiable at last request
	
	
	// Server specific
	public static String NodeServerIP = "192.168.1.105";
	public static String GCMServerIP = "192.168.1.105";
	public static String NodeServerPort = "3000";
	public static String DataCollectorServerPort = "3001";
	public static String GCMServerPort = "8080";

    public static String getNodeServerAddress(){
    	return String.format("http://%s:%s/", NodeServerIP, NodeServerPort);
    }
    public static String getDataCollectorServerAddress(){
    	return String.format("http://%s:%s/", NodeServerIP, DataCollectorServerPort);
    }
    public static String getGCMServerAddress(){
    	return String.format("http://%s:%s/DistributedEnvironmentCoordination", GCMServerIP, GCMServerPort);
    }
    
	//GCM specific
	public static final String OFFER_REQUEST="offerRequest";
	public static final String OFFER_REPLY="offerReply";
	
	public static final String NOT_DEFINED="NotDefinedCommand";
	public static final String JSON_PARSE_ERROR="JsonParseError";
	
	public static final String REQUEST_ADDRESS = "requestAddress";
}

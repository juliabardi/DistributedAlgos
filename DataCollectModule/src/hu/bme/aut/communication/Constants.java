package hu.bme.aut.communication;

/**
 * Constants used for communication.
 * @author Eva Pataji
 *
 */
public class Constants {
	//Registration specific, only these messages can be sent to the server
	public static final String ALGTYPE = "DistributedAlgos";
	public static final String REGISTER = "register";
	public static final String NEED = "need";
	public static final String OFFER = "offer";
	public static final String UNREGISTER_OFFER = "unregisterOffer";
	
	// For syncronization
	public static final String MESSAGE_TYPE = "messageType";
	public static final String ITEM_NAME = "itemName";
	public static final String ITEM_SYNC_VALUE = "itemValue";
	public static final String GCM_REG_ACTION = "GCM_Rec_State";
	public static final String GCM_REG_MSG = "Gcm_Reg_Msg";
	
	// Server specific
//	public static final String NodeServerAddress = "http://10.0.2.2:3000/"; // Emulator localhost test
//	public static final String NodeServerAddress = "http://152.66.183.84:3000/";
	public static final String NodeServerAddress = "http://192.168.1.113:3000/";
	public static final String DataCollectorServerAddress = "http://192.168.1.105:3001/";
	
	//GCM specific
	public static final String OFFER_TO_NEED="OfferToNeed";
	public static final String NEED_TO_OFFER="NeedToOffer";
	public static final String NOT_DEFINED="NotDefinedCommand";
	public static final String JSON_PARSE_ERROR="JsonParseError";
	

}

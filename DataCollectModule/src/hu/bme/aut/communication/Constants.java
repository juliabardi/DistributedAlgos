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
	
	// Server specific
//	public static final String NodeServerAddress = "http://10.0.2.2:3000/"; // Emulator localhost test
	public static final String NodeServerAddress = "http://152.66.183.84:3000/";
	//GCM specific
	public static final String OFFER_TO_NEED="OfferToNeed";
	public static final String NEED_TO_OFFER="NeedToOffer";
	public static final String NOT_DEFINED="NotDefinedCommand";
	public static final String JSON_PARSE_ERROR="JsonParseError";
	

}

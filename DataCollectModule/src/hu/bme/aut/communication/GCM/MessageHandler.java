package hu.bme.aut.communication.GCM;

import hu.bme.aut.communication.Constants;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * Handle the GCM message.
 * @author Eva Pataji
 *
 */
interface Action {    void performAction(String msg); }

public class MessageHandler {
	
	
	private HashMap<String,Action> cmdList=new HashMap<String, Action>();
	
	public MessageHandler() {
		cmdList.put(Constants.OFFER_REQUEST,new Action(){public void performAction(String msg) {offerRequest(msg);}});
		cmdList.put(Constants.NOT_DEFINED,new Action(){public void performAction(String msg) {notDefined(msg);}});
		cmdList.put(Constants.JSON_PARSE_ERROR,new Action(){public void performAction(String msg) {parseJsonError(msg);}});
	}

	private void offerRequest(String msg){
		// TODO What to do if this offer is unregistered? Send error to node?
		//TODO send broadcast or call a function of DataCollect module.
	}
	
	
	private void notDefined(String msg){
		
	}
	
	private void parseJsonError(String msg){
		Log.i(this.getClass().getName(), msg);
	}
	
	
	public void handleGCMMessage(String msg)
	{
		Log.i(this.getClass().getName(), msg);
		try {
			JSONObject jsMessage=new JSONObject(msg);
			// TODO Parse JSON and call method.
			
		} catch (JSONException e) {
			Log.e(this.getClass().getName(), "Could not process msg");
			cmdList.get(Constants.JSON_PARSE_ERROR).performAction("Could not parse JSON");
			e.printStackTrace();
		}
	}

}

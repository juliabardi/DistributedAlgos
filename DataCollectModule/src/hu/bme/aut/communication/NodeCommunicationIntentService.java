package hu.bme.aut.communication;

import org.json.JSONException;
import org.json.JSONObject;

import hu.bme.aut.communication.helpers.HttpManager;
import hu.bme.aut.communication.helpers.HttpManager.HttpManagerListener;
import hu.bme.aut.communication.utils.HttpParamsUtils;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

/**
 * Manages HTTP communication with the dec_node node.js server.
 * Sends GET and POST requests created by the CommunicationService and 
 * calls back with the results.
 * 
 * TODO Callback with either BroadcastReceiver or ResultReceiver
 * TODO Handle errors
 * 
 * @author Eva Pataji
 *
 */
public class NodeCommunicationIntentService extends IntentService implements HttpManagerListener{
	
	public static final String JSOBJ = "jsObj";
	public static final String URL = "url";		
	
	private HttpManager httpManager= new HttpManager(this);
	ResultReceiver resRec; // To send back the result of registration;
	String messageType;
	String itemName;
	
	public NodeCommunicationIntentService() {
		super("NodeCommunicationIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String url=intent.getStringExtra(URL);
		String jsObj=intent.getStringExtra(JSOBJ);
		messageType = intent.getStringExtra(Constants.MESSAGE_TYPE);
		itemName = intent.getStringExtra(Constants.ITEM_NAME);
		
		resRec = intent.getParcelableExtra("resReceiver");
		
		Log.i(this.getClass().getName(), "URL: "+url);
		
		if(url!=null) {
			if(jsObj==null){ // It is a GET.
				httpManager.sendGetRequest(url,HttpParamsUtils.getNodeServerPort(this));
			}
			else{ // It is a POST.
				httpManager.sendPostRequest(url, jsObj, HttpParamsUtils.getNodeServerPort(this));
			}
		}
		else {
			Log.e(this.getClass().getName(), "Url is empty.");
		}
		
	}

	/**
	 * Here we get the JSON from the server. If error occured we could not register our offer.
	 */
	@Override
	public void responseArrived(String response) {
		boolean result=false;
		Log.i(this.getClass().getName(), "resArrived: "+ response);
		try {
			JSONObject jsMessage=new JSONObject(response);
			jsMessage.getJSONObject("ok");
			result=true;
			
		} catch (JSONException e) { // error from server, not saved...
			Log.e(this.getClass().getName(), "Could not process msg: " + response);
			e.printStackTrace();
			result=false;
		}
		
		sendResponse(true,result);
	}

	@Override
	public void errorOccuredDuringHandleResponse(String error) {
		Log.i(this.getClass().getName(), "error in Parsing: " + error);
		sendResponse(true,false);
	}

	@Override
	public void errorOccured(String error) {
		Log.i(this.getClass().getName(), "error: "+ error);
		sendResponse(false,false);
	}
	
	private void sendResponse(boolean serverAvaiable,boolean value){
		Bundle resultBundle = new Bundle();
		resultBundle.putString(Constants.MESSAGE_TYPE, messageType);
		resultBundle.putString(Constants.ITEM_NAME, itemName);
		resultBundle.putBoolean(Constants.ITEM_SYNC_VALUE, value);
		resultBundle.putBoolean(Constants.DISTRUBUTED_ALGOS_AVAIABLE_VALUE, serverAvaiable);
		resRec.send(1234, resultBundle);
	}

}

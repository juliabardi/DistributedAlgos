package hu.bme.aut.communication;

import hu.bme.aut.communication.HttpManager.HttpManagerListener;
import android.app.IntentService;
import android.content.Intent;
import android.content.ClipData.Item;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

/**
 * Manages HTTP communication with the node.js server.
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
				httpManager.sendGetRequest(url);
			}
			else{ // It is a POST.
				httpManager.sendPostRequest(url, jsObj);
			}
		}
		else { // TODO handle error.
			
		}
		
	}

	@Override
	public void responseArrived(String response) {
		Log.i(this.getClass().getName(), "resArrived: "+ response);
		sendResponse(true);
	}

	@Override
	public void errorOccuredDuringParse(String error) {
		Log.i(this.getClass().getName(), "error in Parsing: " + error);
		sendResponse(false);
	}

	@Override
	public void errorOccured(String error) {
		Log.i(this.getClass().getName(), "error: "+ error);
		sendResponse(false);
	}
	
	private void sendResponse(boolean value){
		Bundle resultBundle = new Bundle();
		resultBundle.putString(Constants.MESSAGE_TYPE, messageType);
		resultBundle.putString(Constants.ITEM_NAME, itemName);
		resultBundle.putBoolean(Constants.ITEM_SYNC_VALUE, value);
		resRec.send(1234, resultBundle);
	}

}

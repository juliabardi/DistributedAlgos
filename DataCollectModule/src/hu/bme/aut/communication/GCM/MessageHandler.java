package hu.bme.aut.communication.GCM;

import hu.bme.aut.communication.Constants;
import hu.bme.aut.datacollect.upload.DataUploadTask;
import hu.bme.aut.datacollect.upload.UploadTaskQueue;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

/**
 * Handle the GCM message.
 * @author Eva Pataji
 *
 */
interface Action {    void performAction(String msg); }

public class MessageHandler {
	
	private HashMap<String,Action> cmdList=new HashMap<String, Action>();
	
	private Context context;
	private UploadTaskQueue queue;
	
	public MessageHandler(Context context) {
		cmdList.put(Constants.OFFER_REQUEST,new Action(){public void performAction(String msg) {offerRequest(msg);}});
		cmdList.put(Constants.NOT_DEFINED,new Action(){public void performAction(String msg) {notDefined(msg);}});
		cmdList.put(Constants.JSON_PARSE_ERROR,new Action(){public void performAction(String msg) {parseJsonError(msg);}});
	
		this.context = context;
		this.queue = UploadTaskQueue.instance(context);
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
			
			//Calling the DataCollect module
			if (Constants.ALGTYPE.equals(jsMessage.getString(Constants.ALGORITHM_NAME))){
				//TODO get the request Id from somewhere
				this.queue.add(new DataUploadTask(this.context, jsMessage.getString(Constants.JOB_NAME), 2, 
						jsMessage.getString(Constants.REQUEST_STARTER_ADDRESS)));
			}
			
		} catch (JSONException e) {
			Log.e(this.getClass().getName(), "Could not process msg");
			cmdList.get(Constants.JSON_PARSE_ERROR).performAction("Could not parse JSON");
			e.printStackTrace();
		}
	}

}

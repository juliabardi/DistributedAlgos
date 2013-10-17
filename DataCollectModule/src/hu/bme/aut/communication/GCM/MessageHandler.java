package hu.bme.aut.communication.GCM;

import hu.bme.aut.communication.Constants;
import hu.bme.aut.datacollect.activity.CameraActivity;
import hu.bme.aut.datacollect.activity.DataCollectService;
import hu.bme.aut.datacollect.activity.R;
import hu.bme.aut.datacollect.upload.DataUploadTask;
import hu.bme.aut.datacollect.upload.UploadTaskQueue;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
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
	
	/**
	 * The request type is not supported on this peer.
	 * @param msg
	 */
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
			if (Constants.ALGTYPE_DIST_ALGOS.equals(jsMessage.getString(Constants.ALGTYPE))){
				String time="";
				JSONArray columns;
				String id="";
				String port="";
				try {
					JSONObject requestParams=jsMessage.getJSONObject(Constants.REQUEST_PARAMS);
					try {
						time=requestParams.getString(Constants.REQUEST_TIME);
					} catch (Exception e) {}
					try {
						columns=requestParams.getJSONArray(Constants.REQUEST_COLUMNS);	
					} catch (Exception e) {}
					try {
						id=requestParams.getString(Constants.REQUEST_ID);	
					} catch (Exception e) {}
					try {
						port=":"+requestParams.getString(Constants.REQUEST_PORT);
					} catch (Exception e) {}
				} catch (Exception e) {// No params
					}			

				String address = "http://"+jsMessage.getString(Constants.REQUEST_ADDRESS)+port+"/"+ Constants.OFFER_REPLY;
				if ("ImageData".equals(jsMessage.getString(Constants.PARAM_NAME))){
					this.addNotificationImage(address);
				}
				else {
					this.queue.add(new DataUploadTask(this.context, jsMessage.getString(Constants.PARAM_NAME), 2, address));
				}
			}
			
		} catch (JSONException e) {
			Log.e(this.getClass().getName(), "Could not process msg");
			cmdList.get(Constants.JSON_PARSE_ERROR).performAction("Could not parse JSON");
			e.printStackTrace();
		}
	}
	
	public void addNotificationImage(String address){
		
		Intent notificationIntent = new Intent(context, CameraActivity.class);
		notificationIntent.putExtra("address", address);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("Kép kérelem érkezett")
				.setContentText("Kattintson kép készítéséhez")
				.setContentIntent(pendingIntent);

		NotificationManager mNotificationManager =
			    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			// mId allows you to update the notification later on.
		mNotificationManager.notify(DataCollectService.IMAGE_NOTIF_ID, builder.build());
	}

}

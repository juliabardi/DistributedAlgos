package hu.bme.aut.communication.GCM;

import hu.bme.aut.communication.Constants;
import hu.bme.aut.datacollect.activity.CameraActivity;
import hu.bme.aut.datacollect.activity.DataCollectService;
import hu.bme.aut.datacollect.activity.R;
import hu.bme.aut.datacollect.db.DaoBase;
import hu.bme.aut.datacollect.db.DatabaseHelper;
import hu.bme.aut.datacollect.entity.RecurringRequest;
import hu.bme.aut.datacollect.upload.DataUploadTask;
import hu.bme.aut.datacollect.upload.UploadTaskQueue;
import hu.bme.aut.datacollect.utils.StringUtils;
import hu.bme.aut.datacollect.utils.Utils;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;

/**
 * Handle the GCM message.
 * @author Eva Pataji
 *
 */
interface Action {    void performAction(String msg); }

public class MessageHandler {
	
	private static final String TAG = MessageHandler.class.getName();
	
	private HashMap<String,Action> cmdList=new HashMap<String, Action>();
	
	private Context context;
	private UploadTaskQueue queue;
	private DaoBase<RecurringRequest> recurringDao;
	
	public MessageHandler(Context context) {
		cmdList.put(Constants.OFFER_REQUEST,new Action(){public void performAction(String msg) {offerRequest(msg);}});
		cmdList.put(Constants.NOT_DEFINED,new Action(){public void performAction(String msg) {notDefined(msg);}});
		cmdList.put(Constants.JSON_PARSE_ERROR,new Action(){public void performAction(String msg) {parseJsonError(msg);}});
	
		this.context = context;
		this.queue = UploadTaskQueue.instance(context);
		
		DatabaseHelper dbHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
		this.recurringDao = dbHelper.getDaoBase(RecurringRequest.class);
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
		Log.i(TAG, msg);
		try {
			JSONObject jsMessage = new JSONObject(msg);
			
			//Calling the DataCollect module
			if (Constants.ALGTYPE_DIST_ALGOS.equals(jsMessage.getString(Constants.ALGTYPE))){
				
				String time = null;
				String date = null;
				String recurrence = null;
				JSONArray columns = null;
				String reqId = null;
				String port = Constants.DataCollectorServerPort;
				JSONObject requestParams = jsMessage.optJSONObject(Constants.REQUEST_PARAMS);
				
				if (requestParams != null){
					
					time = StringUtils.trimToNull(requestParams.optString(Constants.REQUEST_TIME));
					date = StringUtils.trimToNull(requestParams.optString(Constants.REQUEST_DATE));
					recurrence = StringUtils.trimToNull(requestParams.optString(Constants.REQUEST_RECURRENCE));
					columns = requestParams.optJSONArray(Constants.REQUEST_COLUMNS);	
					reqId = StringUtils.trimToNull(requestParams.optString(Constants.REQUEST_ID));
					String p = StringUtils.trimToNull(requestParams.optString(Constants.REQUEST_PORT));
					port = (p == null) ? port : p;
				}

				String ip = jsMessage.getString(Constants.REQUEST_ADDRESS);
				String dataType = jsMessage.getString(Constants.PARAM_NAME);
				String address = "http://"+ip+":"+port+"/"+ Constants.OFFER_REPLY;
				
				if ("ImageData".equals(dataType)){
					
					this.addNotificationImage(address, reqId);
				}
				else {	//dataType comes from SQLite
					String joinedCols = null;
					List<String> params = null;
					if (columns != null){
						params = Utils.convertJSONArrayToList(columns);
						joinedCols = Utils.convertListToCsv(params);					
					}
					Date queryDate = null;
					
					//only one of date and time should be given (if both are, time will overwrite date)
					if (date != null){
						queryDate = this.parseDate(date);
					}
					if (time != null){
						queryDate = this.subtractSeconds(time);
					}
					
					this.queue.add(new DataUploadTask(this.context, dataType, reqId, address, queryDate, params));
					
					//deleting previous similar request, not to remain recurring if the new is not that
					this.deleteRequestIfExists(ip, port, dataType);
					
					if (recurrence != null){
						long millis = Calendar.getInstance().getTimeInMillis();
						int recurrenceInt = Integer.parseInt(recurrence);
						RecurringRequest recurringRequest = new RecurringRequest(reqId, ip, port, recurrenceInt, millis, dataType, joinedCols);
						Log.d(TAG, "Saving recurring request: " + recurringRequest.toString());
						this.recurringDao.createOrUpdate(recurringRequest);
					}
				}
			}
			
		} catch (JSONException e) {
			Log.e(TAG, "Could not process msg");
			cmdList.get(Constants.JSON_PARSE_ERROR).performAction("Could not parse JSON");
			e.printStackTrace();
		}
	}
	
	//trying to parse param date in yyyy-MM-dd format
	private Date parseDate(String date){
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return format.parse(date);
		} catch (ParseException e) {
			Log.e(TAG, "Error parsing date in (yyyy-MM-dd) format: " + date);
			return null;
		}
	}
	
	private Date subtractSeconds(String time){
		
		try {
			int timeInt = Integer.parseInt(time);
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.SECOND, timeInt*(-1));
			return cal.getTime();
		} catch (NumberFormatException e) {
			Log.e(TAG, "Error parsing time to int: " + time);
			return null;
		}
	}
	
	//identifying by ip, port, dataType
	private boolean deleteRequestIfExists(String ip, String port, String dataType){
		
		try {
			RecurringRequest request = this.recurringDao.queryBuilder().where().eq("ip", ip)
					.and().eq("port", port)
					.and().eq("dataType", dataType).queryForFirst();
			
			if (request != null){
				Log.d(TAG, "Deleting previous request: " + request.toString());
				this.recurringDao.delete(request);
				return true;
			}			
		} catch (SQLException e) {			
			Log.e(TAG, e.getMessage());
		}		
		return false;
	}
	
	public void addNotificationImage(String address, String reqId){
		
		Intent notificationIntent = new Intent(context, CameraActivity.class);
		notificationIntent.putExtra("address", address);
		notificationIntent.putExtra("reqId", reqId);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("K�p k�relem �rkezett")
				.setContentText("Kattintson k�p k�sz�t�s�hez")
				.setContentIntent(pendingIntent);

		NotificationManager mNotificationManager =
			    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			// mId allows you to update the notification later on.
		mNotificationManager.notify(DataCollectService.IMAGE_NOTIF_ID, builder.build());
	}
}

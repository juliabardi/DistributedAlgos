package hu.bme.aut.communication.GCM;

import hu.bme.aut.communication.Constants;
import hu.bme.aut.datacollect.activity.CameraActivity;
import hu.bme.aut.datacollect.activity.DataCollectService;
import hu.bme.aut.datacollect.activity.R;
import hu.bme.aut.datacollect.db.DaoBase;
import hu.bme.aut.datacollect.db.DatabaseHelper;
import hu.bme.aut.datacollect.entity.RecurringRequest;
import hu.bme.aut.datacollect.upload.DataUploadTask;
import hu.bme.aut.datacollect.upload.TrafficStatsUploadTask;
import hu.bme.aut.datacollect.upload.UploadTaskQueue;
import hu.bme.aut.datacollect.utils.StringUtils;
import hu.bme.aut.datacollect.utils.Utils;

import java.io.Closeable;
import java.io.IOException;
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

public class MessageHandler implements Closeable {
	
	private static final String TAG = MessageHandler.class.getName();
	
	private HashMap<String,Action> cmdList=new HashMap<String, Action>();
	
	private Context context;
	private UploadTaskQueue queue;
	
	private DatabaseHelper dbHelper = null;
	private DaoBase<RecurringRequest> recurringDao;
	
	public MessageHandler(Context context) {
		cmdList.put(Constants.OFFER_REQUEST,new Action(){public void performAction(String msg) {offerRequest(msg);}});
		cmdList.put(Constants.NOT_DEFINED,new Action(){public void performAction(String msg) {notDefined(msg);}});
		cmdList.put(Constants.JSON_PARSE_ERROR,new Action(){public void performAction(String msg) {parseJsonError(msg);}});
	
		this.context = context;
		this.queue = UploadTaskQueue.instance(context);
		
		this.recurringDao = getHelper().getDaoBase(RecurringRequest.class);
	}
	
    private DatabaseHelper getHelper() {
        if (dbHelper == null) {
            dbHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        }
        return dbHelper;
    }
    
	@Override
	public void close() throws IOException {
        if (dbHelper != null) {
            OpenHelperManager.releaseHelper();
            dbHelper = null;
        }
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
				String port = Constants.getDataCollectorServerPort(context);
				String protocol = Constants.getDataCollectorServerProtocol(context);
				JSONObject requestParams = jsMessage.optJSONObject(Constants.REQUEST_PARAMS);
				
				String width = null;
				String height = null;
				String times = null;				
				
				if (requestParams != null){
					
					time = StringUtils.trimToNull(requestParams.optString(Constants.REQUEST_TIME));
					date = StringUtils.trimToNull(requestParams.optString(Constants.REQUEST_DATE));
					recurrence = StringUtils.trimToNull(requestParams.optString(Constants.REQUEST_RECURRENCE));
					columns = requestParams.optJSONArray(Constants.REQUEST_COLUMNS);	
					reqId = StringUtils.trimToNull(requestParams.optString(Constants.REQUEST_ID));
					String p = StringUtils.trimToNull(requestParams.optString(Constants.REQUEST_PORT));
					port = (p == null) ? port : p;
					String prot = StringUtils.trimToNull(requestParams.optString(Constants.REQUEST_PROTOCOL));
					protocol = (prot == null) ? protocol : prot;
					
					width = StringUtils.trimToNull(requestParams.optString("width"));
					height = StringUtils.trimToNull(requestParams.optString("height"));
					times = StringUtils.trimToNull(requestParams.optString("times"));
				}

				String ip = jsMessage.getString(Constants.REQUEST_ADDRESS);
				String dataType = jsMessage.getString(Constants.PARAM_NAME);
				String address = protocol + "://"+ip+":"+port+"/"+ Constants.OFFER_REPLY;
				
				if (DataCollectService.IMAGE.equals(dataType) && 
						DataCollectService.isDataTypeEnabled(context, DataCollectService.IMAGE)){
					
					this.addNotificationImage(address, reqId, width, height, times, recurrence);
					return;
				}
				
				String joinedCols = null;
				List<String> params = null;
				if (columns != null){
					params = Utils.convertJSONArrayToList(columns);
					joinedCols = Utils.convertListToCsv(params);					
				}
				int recurrenceInt = recurrence==null?0:Integer.parseInt(recurrence);
				int timesInt = times==null?0:Integer.parseInt(times);
				
				if (DataCollectService.TRAFFIC.equals(dataType) && 
						DataCollectService.isDataTypeEnabled(context, DataCollectService.TRAFFIC)){
					
					this.queue.add(new TrafficStatsUploadTask(context, address, port, reqId, timesInt, recurrenceInt, params));
				}
				else {	
					Date queryDate = null;
					
					//only one of date and time should be given (if both are, time will overwrite date)
					if (date != null){
						queryDate = this.parseDate(date);
					}
					if (time != null){
						queryDate = this.subtractSeconds(time);
					}
					
					this.queue.add(new DataUploadTask(this.context, dataType, reqId, address, port, queryDate, params));
					
					//deleting previous similar request, not to remain recurring if the new is not that
					this.deleteRequestIfExists(ip, port, dataType);
					
					if (recurrence != null){
						long millis = Calendar.getInstance().getTimeInMillis();
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
	
	public void addNotificationImage(String address, String reqId, String width, String height, String times, String recurrence){
		
		Intent notificationIntent = new Intent(context, CameraActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		notificationIntent.putExtra("address", address);
		notificationIntent.putExtra("reqId", reqId);
		this.putIntIfExists(notificationIntent, "width", width);
		this.putIntIfExists(notificationIntent, "height", height);
		this.putIntIfExists(notificationIntent, "times", times);
		this.putIntIfExists(notificationIntent, "recurrence", recurrence);
		
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("Kép kérelem érkezett")
				//.setContentText("Kattintson kép készítéséhez")
				.setContentText("Id: " + reqId + " Hányszor: " + times + " Idõköz: " + recurrence)
				.setContentIntent(pendingIntent);

		NotificationManager mNotificationManager =
			    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			// mId allows you to update the notification later on.
		//mNotificationManager.cancel(DataCollectService.IMAGE_NOTIF_ID);
		mNotificationManager.notify(reqId, DataCollectService.IMAGE_NOTIF_ID, builder.build());
	}

	private void putIntIfExists(Intent intent, String name, String input){
		
		if (input != null){
			try {
				intent.putExtra(name, Integer.parseInt(input));
			} catch (NumberFormatException e){
				Log.e(TAG, e.getMessage());
			}
		}
	}
}

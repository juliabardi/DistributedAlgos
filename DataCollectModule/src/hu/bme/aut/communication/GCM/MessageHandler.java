package hu.bme.aut.communication.GCM;

import hu.bme.aut.communication.Constants;
import hu.bme.aut.communication.entity.RequestLogData;
import hu.bme.aut.communication.utils.HttpParamsUtils;
import hu.bme.aut.datacollect.activity.AlgorithmActivity;
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

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;

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
	private DaoBase<RequestLogData> requestLogDao;
	
	public MessageHandler(Context context) {
		cmdList.put(Constants.OFFER_REQUEST,new Action(){public void performAction(String msg) {offerRequest(msg);}});
		cmdList.put(Constants.NOT_DEFINED,new Action(){public void performAction(String msg) {notDefined(msg);}});
		cmdList.put(Constants.JSON_PARSE_ERROR,new Action(){public void performAction(String msg) {parseJsonError(msg);}});
	
		this.context = context;
		this.queue = UploadTaskQueue.instance(context);
		
		this.recurringDao = getHelper().getDaoBase(RecurringRequest.class);
		this.requestLogDao = getHelper().getDaoBase(RequestLogData.class);
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
		RequestLogData requestLog = new RequestLogData();
		requestLog.setRequestReceived(Calendar.getInstance().getTimeInMillis());
		requestLog.setRequestParams(msg);
		try {
			JSONObject jsMessage = new JSONObject(msg);
			
			//Calling the DataCollect module
			if (Constants.ALGTYPE_DIST_ALGOS.equals(jsMessage.getString(Constants.ALGTYPE))){
				requestLog.setValidRequest(true);
				RequestParams rParams = new RequestParams();				
				rParams.setPort(HttpParamsUtils.getDataCollectorServerPort(context));
				rParams.setProtocol(HttpParamsUtils.getDataCollectorServerProtocol(context));
				rParams.setScript(jsMessage.optString(Constants.SCRIPT));
				
				JSONObject requestParams = jsMessage.optJSONObject(Constants.REQUEST_PARAMS);			
				
				if (requestParams != null){
					
					rParams.setTime(StringUtils.trimToNull(requestParams.optString(Constants.REQUEST_TIME)));
					rParams.setDate(StringUtils.trimToNull(requestParams.optString(Constants.REQUEST_DATE)));
					rParams.setRecurrence(StringUtils.trimToNull(requestParams.optString(Constants.REQUEST_RECURRENCE)));
					rParams.setColumns(requestParams.optJSONArray(Constants.REQUEST_COLUMNS));	
					rParams.setReqId(StringUtils.trimToNull(requestParams.optString(Constants.REQUEST_ID)));
					String p = StringUtils.trimToNull(requestParams.optString(Constants.REQUEST_PORT));
					if (p != null){ rParams.setPort(p); }
					String prot = StringUtils.trimToNull(requestParams.optString(Constants.REQUEST_PROTOCOL));
					if (prot != null){ rParams.setProtocol(prot); }
					
					rParams.setWidth(StringUtils.trimToNull(requestParams.optString("width")));
					rParams.setHeight(StringUtils.trimToNull(requestParams.optString("height")));
					rParams.setTimes(StringUtils.trimToNull(requestParams.optString("times")));
				}

				rParams.setIp(jsMessage.getString(Constants.REQUEST_ADDRESS));
				rParams.setDataType(jsMessage.getString(Constants.PARAM_NAME));
				
				requestLog.setLogData(rParams.getReqId(), rParams.getRecurrence()!=null?true:false, rParams.getDataType());
				this.requestLogDao.create(requestLog);
				rParams.setIdRequestLog(requestLog.getId());
				
				if (DataCollectService.ALGORITHM.equals(rParams.getDataType()) &&
						DataCollectService.isDataTypeEnabled(context, DataCollectService.ALGORITHM)){
					
					Intent intent = new Intent(context, AlgorithmActivity.class);
					intent.putExtra("requestParams", rParams);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);					
					return;				
				} 
				if (DataCollectService.IMAGE.equals(rParams.getDataType()) && 
						DataCollectService.isDataTypeEnabled(context, DataCollectService.IMAGE)){
					
					this.addNotificationImage(rParams);
					return;
				}
				
				if (DataCollectService.TRAFFIC.equals(rParams.getDataType()) && 
						DataCollectService.isDataTypeEnabled(context, DataCollectService.TRAFFIC)){
					TrafficStatsUploadTask trafficTask=new TrafficStatsUploadTask(context, rParams);
					this.queue.add(trafficTask);
				}
				//send only if enabled
				else if (DataCollectService.sharedPrefKeys.contains(rParams.getDataType()) && 
						DataCollectService.isDataTypeEnabled(context, rParams.getDataType())){
					
					DataUploadTask dataTask = new DataUploadTask(this.context, rParams); 
					this.queue.add(dataTask);
					
					//deleting previous similar request, not to remain recurring if the new is not that
					this.deleteRequestIfExists(rParams);
					
					if (rParams.getRecurrence() != null){
						long millis = Calendar.getInstance().getTimeInMillis();
						RecurringRequest recurringRequest = new RecurringRequest(rParams, millis);
						Log.d(TAG, "Saving recurring request: " + recurringRequest.toString());
						this.recurringDao.createOrUpdate(recurringRequest);
					}
				}
			}else{
				requestLog.setValidRequest(false);
			}
			
		} catch (JSONException e) {
			Log.e(TAG, "Could not process msg");
			requestLog.setValidRequest(false);
			cmdList.get(Constants.JSON_PARSE_ERROR).performAction("Could not parse JSON");
			e.printStackTrace();
		}finally{
			this.requestLogDao.createOrUpdate(requestLog);
		}
	}
	
	//identifying by ip, port, dataType
	private boolean deleteRequestIfExists(RequestParams rParams){
		
		try {
			RecurringRequest request = this.recurringDao.queryBuilder().where().eq("ip", rParams.getIp())
					.and().eq("port", rParams.getPort())
					.and().eq("dataType", rParams.getDataType()).queryForFirst();
			
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
	
	public void addNotificationImage(RequestParams rParams){
		
		Intent notificationIntent = new Intent(context, CameraActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		notificationIntent.putExtra("requestParams", rParams);
		
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("Kép kérelem érkezett")
				.setContentText("Id: " + rParams.getReqId() + " Hányszor: " + (rParams.getTimes()==null?"1":rParams.getTimes()) + " Idõköz: " + (rParams.getRecurrence()==null?"-":rParams.getRecurrence()))
				.setContentIntent(pendingIntent);

		NotificationManager mNotificationManager =
			    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(rParams.getReqId(), DataCollectService.IMAGE_NOTIF_ID, builder.build());
	}
	
}

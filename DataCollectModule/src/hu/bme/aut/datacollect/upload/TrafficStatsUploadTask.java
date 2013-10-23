package hu.bme.aut.datacollect.upload;

import hu.bme.aut.datacollect.activity.DataCollectService;
import hu.bme.aut.datacollect.entity.IData;
import hu.bme.aut.datacollect.entity.TrafficData;

import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class TrafficStatsUploadTask extends UploadTask {

	private static final long serialVersionUID = -8857936566629091845L;

	static {
		TAG = "DataCollect:TrafficStatsUploadTask";
	}
	private Context context;
	private UploadTaskQueue queue;
	
	private List<String> params;
	private int max_times;
	private int interval;
	
	public TrafficStatsUploadTask(Context context, String address, String reqId, int max_times, int interval, List<String> params) {
		super(address, reqId);
		this.params = params;
		
		this.setTimesAndInterval(max_times, interval);
		
		this.context = context;
		this.queue = UploadTaskQueue.instance(context);
	}
	
	private void setTimesAndInterval(int max_times, int interval){
		if (max_times == 0 && interval == 0){
			this.max_times = 1;
			this.interval = 0;
		}
		else if (max_times == 0 && interval != 0){
			this.max_times = Integer.MAX_VALUE;
			this.interval = interval;
		}
		else {
			this.max_times = max_times;
			this.interval = interval;
		}
	}
	
	@Override
	public void execute(final Callback callback) {
		super.execute(callback);
		
		//checking if traffic got disabled in the meantime
		if (!DataCollectService.isDataTypeEnabled(context, DataCollectService.TRAFFIC)){			
			mCallback.onFailure("TrafficData is disabled by user");
			return;
		}
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				
				JSONObject result = IData.toJSONObject(Arrays.asList(new TrafficData()), reqId, params);
	
				if (result == null){
					mCallback.onFailure("No data available.");
					return;
				}
				
				httpManager.sendPostRequest(address, result.toString());				
				--max_times;
				
				if (max_times > 0 && interval != 0){
					Log.d(TAG, "Executed Task, " + max_times + " remained.");
					try {
						Thread.sleep(interval * 1000);
					} catch (InterruptedException e) {
						Log.e(TAG, e.getMessage());
					}
					//creating another task while max_times is not 0
					queue.add(new TrafficStatsUploadTask(context, address, reqId, max_times-1, interval, params));
				}
				
			}}).start();
	}

}

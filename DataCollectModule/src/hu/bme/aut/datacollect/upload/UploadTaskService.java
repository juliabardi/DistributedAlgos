package hu.bme.aut.datacollect.upload;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class UploadTaskService extends Service implements
		UploadTask.Callback {

	private static final String TAG = "DataCollect:UploadTaskService";

	private UploadTaskQueue queue = UploadTaskQueue.instance(this);
	private boolean running;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "Starting UploadTaskService, executing next UploadTask.");
		this.executeNext();
		return START_STICKY;
	}

	private void executeNext() {
		if (running){
			return; // Only one task at a time.
		}

		UploadTask task = queue.peek();
		if (task != null) {
			running = true;
			task.execute(this);
		} else {
			Log.i(TAG, "No more UploadTasks for now, UploadTaskService stopping.");
			stopSelf(); 
		}
	}

	@Override
	public void onSuccess(final String url) {
		//Log.d(TAG, "Success, executing next UploadTask.");
		running = false;
		queue.remove();
		executeNext();
	}

	@Override
	public void onFailure(String message) {
		//Log.d(TAG, "Failure, executing next UploadTask. Reason: " + message);
		//continuing the upload in case of failure for now
		running = false;
		queue.remove();
		executeNext();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}

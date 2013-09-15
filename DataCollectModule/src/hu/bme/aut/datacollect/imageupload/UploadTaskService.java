package hu.bme.aut.datacollect.imageupload;

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
		Log.d(TAG, "Starting service, executing next task.");
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
			Log.i(TAG, "No more tasks for now, service stopping.");
			stopSelf(); 
		}
	}

	@Override
	public void onSuccess(final String url) {
		Log.d(TAG, "Success, executing next.");
		running = false;
		queue.remove();
		executeNext();
	}

	@Override
	public void onFailure() {
		Log.d(TAG, "Failure, executing next.");
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

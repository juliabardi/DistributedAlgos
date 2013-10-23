package hu.bme.aut.datacollect.upload;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ImageUploadTaskService extends Service implements
		ImageUploadTask.Callback {

	private static final String TAG = "DataCollect:ImageUploadTaskService";

	private ImageUploadTaskQueue queue = ImageUploadTaskQueue.instance(this);
	private boolean running;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "Starting ImageUploadTaskService, executing next ImageUploadTask.");
		this.executeNext();
		return START_STICKY;
	}

	private void executeNext() {
		if (running){
			return; // Only one task at a time.
		}

		ImageUploadTask task = queue.peek();
		if (task != null) {
			running = true;
			task.execute(this);
		} else {
			Log.i(TAG, "No more ImageUploadTasks for now, ImageUploadTaskService stopping.");
			stopSelf(); 
		}
	}

	@Override
	public void onSuccess(final String url) {
		Log.d(TAG, "Success, executing next ImageUploadTask.");
		running = false;
		queue.remove();
		executeNext();
	}

	@Override
	public void onFailure(String message) {
		Log.d(TAG, "Failure, executing ImageUploadTask. Reason: " + message);
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

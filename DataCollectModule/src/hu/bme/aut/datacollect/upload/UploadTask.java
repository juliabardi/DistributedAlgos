package hu.bme.aut.datacollect.upload;

import hu.bme.aut.communication.HttpManager;
import hu.bme.aut.communication.HttpManager.HttpManagerListener;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.squareup.tape.Task;

public abstract class UploadTask implements Task<UploadTask.Callback>,
		HttpManagerListener {
	
	private static final long serialVersionUID = -7039527931471322551L;

	public interface Callback {
		void onSuccess(String url);
		void onFailure();
	}
	
	protected static String TAG = "DataCollect:UploadTask";
	
	protected static final Handler MAIN_THREAD = new Handler(
			Looper.getMainLooper());
	
	protected Callback mCallback;
	
	protected HttpManager httpManager = new HttpManager(this);

	@Override
	public void responseArrived(String response) {
		Log.d(TAG, response);
		this.uploadSuccess();
	}

	@Override
	public void errorOccuredDuringParse(String error) {
		Log.e(TAG, error);
		this.uploadFailed();
	}

	@Override
	public void errorOccured(String error) {
		Log.e(TAG, error);
		this.uploadFailed();
	}
	
	private void uploadFailed(){
		Log.i(TAG, "Upload failed :(");

		// Get back to the main thread before invoking a callback.
		MAIN_THREAD.post(new Runnable() {
			@Override
			public void run() {
				mCallback.onFailure();
			}
		});
	}
	
	private void uploadSuccess(){
		Log.i(TAG, "Upload success!");					
		
		MAIN_THREAD.post(new Runnable() {
			@Override
			public void run() {
				mCallback.onSuccess("url");
			}
		});
	}

//	@Override
//	public void execute(Callback arg0) {
//		// TODO Auto-generated method stub
//		
//	}

}

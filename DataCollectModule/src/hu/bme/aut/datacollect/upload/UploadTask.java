package hu.bme.aut.datacollect.upload;

import hu.bme.aut.communication.helpers.HttpManager;
import hu.bme.aut.communication.helpers.HttpManager.HttpManagerListener;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.squareup.tape.Task;

public abstract class UploadTask implements Task<UploadTask.Callback>,
		HttpManagerListener {
	
	private static final long serialVersionUID = -7039527931471322551L;
	
	protected String address;
	protected String reqId;
	protected String port;

	public interface Callback {
		void onSuccess(String url);
		void onFailure(String message);
	}
	
	protected static String TAG = "DataCollect:UploadTask";
	
	protected static final Handler MAIN_THREAD = new Handler(
			Looper.getMainLooper());
	
	protected Callback mCallback;
	
	protected HttpManager httpManager = new HttpManager(this);
	
	public UploadTask(String address, String reqId, String port){
		this.address = address;
		this.reqId = reqId;
		this.port = port;
	}

	@Override
	public void responseArrived(String response) {
		Log.d(TAG, response);
		this.uploadSuccess();
	}

	@Override
	public void errorOccuredDuringHandleResponse(String error) {
		Log.e(TAG, error);
		this.uploadFailed();
	}

	@Override
	public void errorOccured(String error) {
		Log.e(TAG, error);
		this.uploadFailed();
	}
	
	protected void uploadFailed(){
		Log.i(TAG, "Upload failed :(");

		// Get back to the main thread before invoking a callback.
		MAIN_THREAD.post(new Runnable() {
			@Override
			public void run() {
				mCallback.onFailure("");
			}
		});
		
		this.cleanup();
	}
	
	protected void uploadSuccess(){
		Log.i(TAG, "Upload success!");					
		
		MAIN_THREAD.post(new Runnable() {
			@Override
			public void run() {
				mCallback.onSuccess("url");
			}
		});
		
		this.cleanup();
	}

	//function to clean up when the task finished
	protected void cleanup(){}

	/**
	 * Call this in your implementation
	 */
	@Override
	public void execute(Callback callback) {
		this.mCallback = callback;		
	}

}

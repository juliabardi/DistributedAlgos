package hu.bme.aut.datacollect.upload;

import java.io.IOException;
import java.util.Date;

import hu.bme.aut.communication.GCM.RequestParams;
import hu.bme.aut.communication.entity.ResponseLogData;
import hu.bme.aut.communication.helpers.HttpManager;
import hu.bme.aut.communication.helpers.HttpManager.HttpManagerListener;
import hu.bme.aut.datacollect.db.DataProvider;
import hu.bme.aut.datacollect.db.IDataProvider;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.squareup.tape.Task;

public abstract class UploadTask implements Task<UploadTask.Callback>,
		HttpManagerListener {
	
	private static final long serialVersionUID = -7039527931471322551L;
	
	protected RequestParams rParams;
	
	protected ResponseLogData responseLog;
	
	protected IDataProvider dataProvider;

	public interface Callback {
		void onSuccess(String url);
		void onFailure(String message);
	}
	
	protected static String TAG = "DataCollect:UploadTask";
	
	protected static final Handler MAIN_THREAD = new Handler(
			Looper.getMainLooper());
	
	protected Callback mCallback;
	
	protected HttpManager httpManager = new HttpManager(this);
	
	public UploadTask(Context context, RequestParams requestParams){
		
		this.rParams = requestParams;
		
		this.dataProvider = new DataProvider(context);
	}

	@Override
	public void responseArrived(String response, String code) {
		//Log.d(TAG, response);
		this.saveResponseLog(response, code);
		this.uploadSuccess();
	}

	@Override
	public void errorOccuredDuringHandleResponse(String error, String code) {
		//Log.e(TAG, error);
		this.saveResponseLog(error, code);
		this.uploadFailed();
	}

	@Override
	public void errorOccured(String error) {
		//Log.e(TAG, error);
		this.saveResponseLog(error, null);
		this.uploadFailed();
	}
	
	@SuppressWarnings("deprecation")
	protected void saveResponseLog(String message, String code){
		
		this.responseLog.setAnswerReceived(System.currentTimeMillis());
		this.responseLog.setAnswerParams(message);
		this.responseLog.setStatusCode(code);
		Log.d(TAG, String.format("Saving ResponseLogData: requestLogId: %s, responseSent: %s, answerReceived: %s, statusCode: %s, answerParams: %s", 
				responseLog.getRequestLogId().getId(), new Date(responseLog.getResponseSent()).toLocaleString(), new Date(responseLog.getAnswerReceived()).toLocaleString(), 
				responseLog.getStatusCode(), responseLog.getAnswerParams()));
		this.dataProvider.createResponseLogData(responseLog);
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
	protected void cleanup(){
		
		try {
			this.dataProvider.close();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	/**
	 * Call this in your implementation
	 */
	@Override
	public void execute(Callback callback) {
		this.mCallback = callback;		
		
		this.responseLog = new ResponseLogData();
		this.responseLog.setRequestLogId(this.dataProvider.getRequestLogDataById(rParams.getIdRequestLog()));
	}

}

package hu.bme.aut.datacollect.upload;

import hu.bme.aut.datacollect.db.DataProvider;
import hu.bme.aut.datacollect.db.IDataProvider;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

/**
 * Methods that can be called from Javascript
 *
 */
public class JavascriptCallback implements Closeable {
	
	public static final String TAG = "DataCollect:JavascriptCallback";
	
	private IDataProvider dataProvider;
	
	private UploadTaskQueue queue;
	
	private String address;
	private String reqId;
	private String port;
	
	public JavascriptCallback(Context context, String address, String reqId, String port){
		this.dataProvider = new DataProvider(context);
		queue = UploadTaskQueue.instance(context);
		
		this.address = address;
		this.reqId = reqId;
		this.port = port;
	}
	
	@JavascriptInterface
	public String getAllData(String name, String id){
		Log.d(TAG, "Javascript getAllData: " + name);
		JSONObject result = this.dataProvider.getAllData(name, id);
		return result == null ? null : result.toString();
	}

	@JavascriptInterface
	public String getAllDataParamsString(String name, String id, String params){
		String[] p = params.split(",");
		Log.d(TAG, "Javascript getAllDataParamsString: " + name + ", " + params);
		JSONObject result = this.dataProvider.getAllDataParams(name, id, Arrays.asList(p));	
		return result == null ? null : result.toString();
	}
	
	@JavascriptInterface
	public void sendResult(final String json){
		
		Log.d(TAG, "Javascript returned string: " + json);
		try {
			//validating
			JSONArray j = new JSONArray(json);
			
			this.queue.add(new UploadTask(address, reqId, port) {

				@Override
				public void execute(Callback callback) {
					super.execute(callback);
					
					new Thread(new Runnable() {
						@Override
						public void run() {				
							Log.d(TAG, "Sending result to address: " + address);
							httpManager.sendPostRequest(address, json, port);				
							
						}}).start();
				}
			});
		} catch (JSONException e) {
			Log.e(TAG, "Javascript-returned json parse failed.");
			return;
		}
	}

	@Override
	public void close() throws IOException {		
		this.dataProvider.close();
	}
}

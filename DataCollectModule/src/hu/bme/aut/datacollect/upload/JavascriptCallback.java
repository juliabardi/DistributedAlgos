package hu.bme.aut.datacollect.upload;

import hu.bme.aut.communication.GCM.RequestParams;
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
	
	private Context context;
	
	private RequestParams reqParams;
	
	public JavascriptCallback(Context context, RequestParams rParams){
		this.dataProvider = new DataProvider(context);
		queue = UploadTaskQueue.instance(context);
	
		this.context = context;
		this.reqParams = rParams;
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
			new JSONArray(json);
			
			this.queue.add(new UploadTask(context, reqParams) {

				private static final long serialVersionUID = -487489535369947349L;

				@Override
				public void execute(Callback callback) {
					super.execute(callback);
					
					new Thread(new Runnable() {
						@Override
						public void run() {				
							Log.d(TAG, "Sending result to address: " + rParams.getAddress());
							responseLog.setResponseSent(System.currentTimeMillis());
							httpManager.sendPostRequest(rParams.getAddress(), json, rParams.getPort());				
							
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

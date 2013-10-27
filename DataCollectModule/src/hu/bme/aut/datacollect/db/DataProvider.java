package hu.bme.aut.datacollect.db;

import hu.bme.aut.datacollect.entity.IData;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.j256.ormlite.android.apptools.OpenHelperManager;

public class DataProvider implements IDataProvider{
	
	private static final String TAG ="DataCollect:DataProvider";
	
	private DatabaseHelper dbHelper;
	
	public DataProvider(Context context){
		this.dbHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
	}
	
	@Override
	public <T extends IData> JSONObject getAllData(Class<T> clazz, String reqId, List<String> params) {
		List<T> list = this.dbHelper.getDaoBase(clazz).queryForAll();
		return IData.toJSONObject(list, reqId, params);
	}

	@Override
	public <T extends IData> JSONObject getDataAfterTimestamp(Class<T> clazz, String reqId, long timestamp, List<String> params) {
		List<T> list = this.dbHelper.getDaoBase(clazz).queryAfterTimestamp(timestamp);
		return IData.toJSONObject(list, reqId, params);
	}

	@Override
	public void close() throws IOException {
		if (dbHelper != null){
			OpenHelperManager.releaseHelper();
		}		
	}
	
	@Override
	@JavascriptInterface
	public JSONObject getAllData(String name, String reqId) {		
		return this.getAllDataParams(name, reqId, null);
	}
	
	@Override
	public JSONObject getAllDataParams(String name, String reqId, List<String> params) {
		
		Class clazz;
		try {
			clazz = Class.forName("hu.bme.aut.datacollect.entity." + name);
		} catch (ClassNotFoundException e) {
			Log.e(TAG, e.getMessage());
			return null;
		}
		return this.getAllData(clazz, reqId, params);
	}
	
	@Override
	public JSONObject getDataAfterDate(String name, String reqId, Date date){				
		return this.getDataAfterDate(name, reqId, date, null);
	}

	@Override
	public JSONObject getDataAfterDate(String name, String reqId, Date date,
			List<String> params) {
		
		Class clazz;
		try {
			clazz = Class.forName("hu.bme.aut.datacollect.entity." + name);
		} catch (ClassNotFoundException e) {
			Log.e(TAG, e.getMessage());
			return null;
		}
		
		return this.getDataAfterTimestamp(clazz, reqId, date.getTime(), params);
	}
	
	@JavascriptInterface
	public void setJSON(String json){
		
		Log.d(TAG, "Javascript returned string: " + json);
		JSONArray j = null;
		try {
			j = new JSONArray(json);
		} catch (JSONException e) {
			Log.e(TAG, "Javascript-returned json parse failed.");
			return;
		}
		Log.d(TAG, "JsonArray processed: " + j);
	}
	
	@Override
	@JavascriptInterface
	public JSONObject getAllDataParamsString(String name, String reqId, String params){
		String[] p = params.split(",");
		Log.d(TAG, "Javascript params: " + params);
		return this.getAllDataParams(name, reqId, Arrays.asList(p));		
	}
}

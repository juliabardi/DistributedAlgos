package hu.bme.aut.datacollect.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public abstract class IData {
	
	private static final String TAG ="DataCollect:IData";

	//get all params for a given Data type
	public abstract List<String> getParams();

	//get all values
	public abstract Map<String,String> getValues();
	
	//get values for the given parameters
	public List<String> getValues(List<String> params) {
		
		List<String> values = new ArrayList<String>();
		Map<String,String> allValues = this.getValues();
		
		for (String param : params){
			
			values.add(allValues.get(param));
		}
		return values;
	}

	// converts the given list to JSONObject, reqId is the id from the server, params is the required column names	
	public static <T extends IData> JSONObject toJSONObject(List<T> data, String reqId, List<String> params){
		JSONObject json = new JSONObject();
		if (data != null && data.size() != 0) {
			try {
				json.put("id", reqId);
				json.put("name", data.get(0).getClass().getSimpleName());
				json.put("type", "simple");
				JSONArray values = new JSONArray();
				if (params != null && params.size() != 0){
					json.put("params", new JSONArray(params));
					for (T d : data) {
							values.put(new JSONArray(d.getValues(params)));
					}
				}
				else {
					json.put("params", new JSONArray(data.get(0).getValues().keySet()));
					for (T d : data) {
						values.put(new JSONArray(d.getValues().values()));
					}
				}
				json.put("values", values);
				Log.d(TAG, json.toString());
			} catch (JSONException e){
				Log.e(TAG, e.getMessage());
			}
		}
		return json;
	}
}

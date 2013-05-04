package hu.bme.aut.datacollect.entity;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class IData {

	public abstract List<String> getParams();

	public abstract List<String> getValues();

	// converts the given list to JSONObject
	public static <T extends IData> JSONObject toJSONObject(List<T> data)
			throws JSONException {
		JSONObject json = new JSONObject();
		if (data != null && data.size() != 0) {
			json.put("name", data.get(0).getClass().getSimpleName());
			json.put("type", "simple");
			json.put("params", new JSONArray(data.get(0).getParams()));

			JSONArray values = new JSONArray();
			for (T d : data) {
				values.put(new JSONArray(d.getValues()));
			}
			json.put("values", values);
		}
		return json;
	}
}

package hu.bme.aut.communication.utils;

import hu.bme.aut.communication.Constants;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {
	
	
	/**
	 * Put the algo containing need and offers into the top level body. Last step before sending to server.
	 * @param algType
	 * @param algo
	 * @return
	 */
	public static JSONObject createMainBodyWithAlgos(String algType,JSONObject algo)
	{
		JSONObject main=new JSONObject();
		try {
			main.put(algType, algo);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return main;
	}
	
	/**
	 * Create body of an algType.
	 * @param offerArray
	 * @param needArray
	 * @return
	 */
	public static JSONObject createAlgoBody(JSONArray offerArray, JSONArray needArray)
	{
		JSONObject algo=new JSONObject();
		try {
			if(offerArray!=null){
				algo.put(Constants.OFFER,offerArray);
			}
			if(needArray!=null){
				algo.put(Constants.NEED,needArray);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return algo;
	}
	
	/**
	 * Only containing name, no params or other extra information.
	 * @param items
	 * @return
	 */
	public static JSONArray createSimpleArray(ArrayList<String> items)
	{
		JSONArray array = new JSONArray();
		for(String item : items)
		{
			try {
				array.put(new JSONObject().put(Constants.PARAM_NAME,item));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return array;
	}

}

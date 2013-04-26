package hu.bme.aut.communication;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsHelper {
	
	public static JSONObject registerOfferNeed()
	{
		JSONObject main=new JSONObject();
		JSONObject distAlgo=new JSONObject();
		JSONArray offerArray = new JSONArray();
		JSONArray needArray = new JSONArray();
		JSONObject complex=new JSONObject();
		
		try {
			offerArray.put(new JSONObject().put("name", "GPS"));
			offerArray.put(new JSONObject().put("name", "telephone"));
			offerArray.put(new JSONObject().put("name", "thermo"));
			complex.put("name", "camera");
			complex.put("params", "front,back");
			offerArray.put(new JSONObject().put("name", "telephone"));
			needArray.put(complex);
			distAlgo.put("offer",offerArray);
			distAlgo.put("need",needArray);
			main.put("DistributedAlgos", distAlgo);
		    
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	    return main;
	}
	
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
				algo.put("offer",offerArray);
			}
			if(needArray!=null){
				algo.put("need",needArray);
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
				array.put(new JSONObject().put("name",item));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return array;
	}

}

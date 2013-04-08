package hu.bme.aut.communication;

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

}

package hu.bme.aut.datacollect.utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

public class Utils {

	public static List<String> convertCsvToList(String csv){
		List<String> list = new ArrayList<String>();
		for (String elem : csv.split(",")){
			list.add(elem);
		}
		return list;
	}
	
	public static List<String> convertJSONArrayToList(JSONArray array){
		
		List<String> list = new ArrayList<String>();
		for (int i=0; i<array.length(); ++i){
			list.add(array.optString(i));
		}
		return list;
	}
	
	public static String convertListToCsv(List<String> list){
		
		StringBuilder builder = new StringBuilder();
		for (int i=0; i<list.size(); ++i){
			builder.append(list.get(i));
			if (i != list.size()-1){
				builder.append(",");
			}
		}
		return builder.toString();
	}
}

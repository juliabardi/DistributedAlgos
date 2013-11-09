package hu.bme.aut.datacollect.activity.log.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ViewDataUtils {

	public static String getDateInString(long value){
		try {
			Calendar.getInstance().getTimeInMillis();
		    Calendar calendar = Calendar.getInstance();
		    calendar.setTimeInMillis(value);
		    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
		} catch (Exception e) {
			return "-";
		}
		
	}

	public static String getStringValue(String data){
		if(data!= null  && !data.trim().equals(""))
			return data;
		return "-";	
	}
}

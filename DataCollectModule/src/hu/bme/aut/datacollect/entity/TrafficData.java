package hu.bme.aut.datacollect.entity;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.net.TrafficStats;

public class TrafficData extends IData {
	
	public long getMobileRxBytes(){
		return TrafficStats.getMobileRxBytes();
	}
	
	public long getMobileTxBytes(){
		return TrafficStats.getMobileTxBytes();
	}
	
	public long getTotalRxBytes(){
		return TrafficStats.getTotalRxBytes();
	}
	
	public long getTotalTxBytes(){
		return TrafficStats.getTotalTxBytes();
	}
	
	public long getTimestamp(){
		return Calendar.getInstance().getTimeInMillis();
	}

	@Override
	public List<String> getParams() {
		
		return Arrays.asList("timestamp","mobileSentBytes", "mobileReceivedBytes", "totalSentBytes", "totalReceivedBytes");
	}

	@Override
	public Map<String, String> getValues() {
		
		Map<String, String> values = new HashMap<String,String>();
		values.put("timestamp", String.valueOf(getTimestamp()));
		values.put("mobileSentBytes", String.valueOf(getMobileTxBytes()));
		values.put("mobileReceivedBytes", String.valueOf(getMobileRxBytes()));
		values.put("totalSentBytes", String.valueOf(getTotalTxBytes()));
		values.put("totalReceivedBytes", String.valueOf(getTotalRxBytes()));
		return values;
	}

}

package hu.bme.aut.datacollect.entity;

import hu.bme.aut.datacollect.db.DaoBase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
/**
 * Connectivity types:
 * 
	TYPE_NONE        = -1;
    TYPE_MOBILE      = 0;
    TYPE_WIFI        = 1;
    TYPE_MOBILE_MMS  = 2;
    TYPE_MOBILE_SUPL = 3;
    TYPE_MOBILE_DUN  = 4;
    TYPE_MOBILE_HIPRI = 5;
    TYPE_WIMAX       = 6;
    TYPE_BLUETOOTH   = 7;
    TYPE_DUMMY       = 8;
    TYPE_ETHERNET    = 9;
    
    @see android.net.ConnectivityManager
 *
 */
@DatabaseTable(tableName="connectivities", daoClass=DaoBase.class)
public class ConnectivityData extends IData {
	
	@DatabaseField(generatedId=true)
	private int id;
	@DatabaseField(canBeNull=false)
	private long timestamp;
	@DatabaseField(canBeNull=false)
	private boolean isConnected;
	@DatabaseField(canBeNull=false)
	private int type;
	@DatabaseField(canBeNull=true)
	private String wifiAddress;
	@DatabaseField(canBeNull=true)
	private String gsmAddress;
	
	public ConnectivityData(){}

	public ConnectivityData(long timestamp, boolean isConnected, int type, String wifiAddress, String gsmAddress) {
		super();
		this.timestamp = timestamp;
		this.isConnected = isConnected;
		this.type = type;
		this.wifiAddress = wifiAddress;
		this.gsmAddress = gsmAddress;
	}
	
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public boolean isConnected() {
		return isConnected;
	}

	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public String getWifiAddress() {
		return wifiAddress;
	}

	public void setWifiAddress(String wifiAddress) {
		this.wifiAddress = wifiAddress;
	}

	public String getGsmAddress() {
		return gsmAddress;
	}

	public void setGsmAddress(String gsmAddress) {
		this.gsmAddress = gsmAddress;
	}

	@Override
	public String toString() {
		return String
				.format("ConnectivityData [id=%s, timestamp=%s, isConnected=%s, type=%s, wifiAddress=%s, gsmAddress=%s]",
						id, timestamp, isConnected, type, wifiAddress, gsmAddress);
	}

	@Override
	public List<String> getParams() {
		return Arrays.asList("id", "timestamp", "isConnected", "type", "wifiAddress", "gsmAddress");
	}

	@Override
	public Map<String, String> getValues() {
		
		Map<String,String> values = new HashMap<String,String>();
		values.put("id", String.valueOf(id));
		values.put("timestamp", String.valueOf(timestamp));
		values.put("isConnected", String.valueOf(isConnected));
		values.put("type", String.valueOf(type));
		values.put("wifiAddress", wifiAddress);
		values.put("gsmAddress", gsmAddress);
		return values;
	}

}

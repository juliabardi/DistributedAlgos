package hu.bme.aut.datacollect.entity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.bme.aut.datacollect.db.DaoBase;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * status and plugged values: (NONE = -1)
 * 
    BATTERY_STATUS_UNKNOWN = 1;
    BATTERY_STATUS_CHARGING = 2;
    BATTERY_STATUS_DISCHARGING = 3;
    BATTERY_STATUS_NOT_CHARGING = 4;
    BATTERY_STATUS_FULL = 5;
    
    BATTERY_PLUGGED_AC = 1;
    BATTERY_PLUGGED_USB = 2;
    BATTERY_PLUGGED_WIRELESS = 4;
    
    @see android.os.BatteryManager
 *
 */
@DatabaseTable(tableName="batteries", daoClass=DaoBase.class)
public class BatteryData extends IData{
	
	@DatabaseField(generatedId=true)
	private int id;
	@DatabaseField(canBeNull=false)
	private long timestamp;
	@DatabaseField(canBeNull=false)
	private float percent;
	@DatabaseField(canBeNull=true)
	private int status;
	@DatabaseField(canBeNull=true)
	private int plugged;
	
	public BatteryData(){}

	public BatteryData(long timestamp, float percent, int status, int plugged) {
		super();
		this.timestamp = timestamp;
		this.percent = percent;
		this.status = status;
		this.plugged = plugged;
	}

	public int getId() {
		return id;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public float getPercent() {
		return percent;
	}

	public void setPercent(float percent) {
		this.percent = percent;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getPlugged() {
		return plugged;
	}

	public void setPlugged(int plugged) {
		this.plugged = plugged;
	}

	@Override
	public String toString() {
		return String
				.format("BatteryData [id=%s, timestamp=%s, percent=%s, status=%s, plugged=%s]",
						id, timestamp, percent, status, plugged);
	}

	@Override
	public List<String> getParams(){
		return Arrays.asList("id", "timestamp", "percent", "status", "plugged");
	}
	
	@Override
	public Map<String,String> getValues(){
		
		Map<String,String> values = new HashMap<String,String>();
		values.put("id", String.valueOf(id));
		values.put("timestamp", String.valueOf(timestamp));
		values.put("percent", String.valueOf(percent));
		values.put("status", String.valueOf(status));
		values.put("plugged", String.valueOf(plugged));
		return values;
	}

}

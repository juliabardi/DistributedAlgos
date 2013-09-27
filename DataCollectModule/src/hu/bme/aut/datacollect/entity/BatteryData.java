package hu.bme.aut.datacollect.entity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.bme.aut.datacollect.db.DaoBase;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="batteries", daoClass=DaoBase.class)
public class BatteryData extends IData{
	
	@DatabaseField(generatedId=true)
	private int id;
	@DatabaseField(canBeNull=false)
	private long timestamp;
	@DatabaseField(canBeNull=false)
	private float percent;
	
	public BatteryData(){}

	public BatteryData(long timestamp, float percent) {
		super();
		this.timestamp = timestamp;
		this.percent = percent;
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

	@Override
	public String toString() {
		return String.format("LightData [id=%s, timestamp=%s, percent=%s]", id,
				timestamp, percent);
	}
	
	@Override
	public List<String> getParams(){
		return Arrays.asList("id", "timestamp", "percent");
	}
	
	@Override
	public Map<String,String> getValues(){
		
		Map<String,String> values = new HashMap<String,String>();
		values.put("id", String.valueOf(id));
		values.put("timestamp", String.valueOf(timestamp));
		values.put("percent", String.valueOf(percent));
		return values;
	}

}

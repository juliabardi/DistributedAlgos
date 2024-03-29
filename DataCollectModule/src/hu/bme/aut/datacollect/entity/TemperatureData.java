package hu.bme.aut.datacollect.entity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.bme.aut.datacollect.db.DaoBase;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="temperatures", daoClass=DaoBase.class)
public class TemperatureData extends IData {

	@DatabaseField(generatedId=true)
	private int id;
	@DatabaseField(canBeNull=false)
	private long timestamp;
	@DatabaseField(canBeNull=false)
	private float celsius;
	
	public TemperatureData(){
		
	}
	
	public TemperatureData(long timestamp, float celsius) {
		super();
		this.timestamp = timestamp;
		this.celsius = celsius;
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
	public float getCelsius() {
		return celsius;
	}
	public void setCelsius(float celsius) {
		this.celsius = celsius;
	}

	@Override
	public String toString() {
		return String.format("TemperatureData [id=%s, timestamp=%s, celsius=%s]",
				id, timestamp, celsius);
	}
	
	@Override
	public List<String> getParams(){
		return Arrays.asList("id", "timestamp", "celsius");
	}
	
	@Override
	public Map<String,String> getValues(){
		
		Map<String,String> values = new HashMap<String,String>();
		values.put("id", String.valueOf(id));
		values.put("timestamp", String.valueOf(timestamp));
		values.put("celsius", String.valueOf(celsius));
		return values;
	}
}

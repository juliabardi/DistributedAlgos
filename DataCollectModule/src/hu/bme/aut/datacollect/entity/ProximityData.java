package hu.bme.aut.datacollect.entity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.bme.aut.datacollect.db.DaoBase;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="proximities", daoClass=DaoBase.class)
public class ProximityData extends IData{
	
	@DatabaseField(generatedId=true)
	private int id;
	@DatabaseField(canBeNull=false)
	private long timestamp;
	@DatabaseField(canBeNull=false)
	private float proximity;
	
	public ProximityData(){}

	public ProximityData(long timestamp, float proximity) {
		super();
		this.timestamp = timestamp;
		this.proximity = proximity;
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

	public float getProximity() {
		return proximity;
	}

	public void setProximity(float proximity) {
		this.proximity = proximity;
	}

	@Override
	public String toString() {
		return String.format("LightData [id=%s, timestamp=%s, proximity=%s]", id,
				timestamp, proximity);
	}
	
	@Override
	public List<String> getParams(){
		return Arrays.asList("id", "timestamp", "proximity");
	}
	
	@Override
	public Map<String,String> getValues(){
		
		Map<String,String> values = new HashMap<String,String>();
		values.put("id", String.valueOf(id));
		values.put("timestamp", String.valueOf(timestamp));
		values.put("proximity", String.valueOf(proximity));
		return values;
	}

}

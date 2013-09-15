package hu.bme.aut.datacollect.entity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.bme.aut.datacollect.db.DaoBase;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="lights", daoClass=DaoBase.class)
public class LightData extends IData{
	
	@DatabaseField(generatedId=true)
	private int id;
	@DatabaseField(canBeNull=false)
	private long timestamp;
	@DatabaseField(canBeNull=false)
	private float lx;
	
	public LightData(){}

	public LightData(long timestamp, float lx) {
		super();
		this.timestamp = timestamp;
		this.lx = lx;
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

	public float getLx() {
		return lx;
	}

	public void setLx(float lx) {
		this.lx = lx;
	}

	@Override
	public String toString() {
		return String.format("LightData [id=%s, timestamp=%s, lx=%s]", id,
				timestamp, lx);
	}
	
	@Override
	public List<String> getParams(){
		return Arrays.asList("id", "timestamp", "lx");
	}
	
	@Override
	public Map<String,String> getValues(){
		
		Map<String,String> values = new HashMap<String,String>();
		values.put("id", String.valueOf(id));
		values.put("timestamp", String.valueOf(timestamp));
		values.put("lx", String.valueOf(lx));
		return values;
	}

}

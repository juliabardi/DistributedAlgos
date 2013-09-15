package hu.bme.aut.datacollect.entity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.bme.aut.datacollect.db.DaoBase;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="gyroscope", daoClass=DaoBase.class)
public class GyroscopeData extends IData{

	@DatabaseField(generatedId=true)
	private int id;
	@DatabaseField(canBeNull=false)
	private long timestamp;
	@DatabaseField(canBeNull=false)
	private float axisX;
	@DatabaseField(canBeNull=false)
	private float axisY;
	@DatabaseField(canBeNull=false)
	private float axisZ;
	
	public GyroscopeData(){}
	
	public GyroscopeData(long timestamp, float axisX, float axisY, float axisZ) {
		super();
		this.timestamp = timestamp;
		this.axisX = axisX;
		this.axisY = axisY;
		this.axisZ = axisZ;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public float getAxisX() {
		return axisX;
	}

	public void setAxisX(float axisX) {
		this.axisX = axisX;
	}

	public float getAxisY() {
		return axisY;
	}

	public void setAxisY(float axisY) {
		this.axisY = axisY;
	}

	public float getAxisZ() {
		return axisZ;
	}

	public void setAxisZ(float axisZ) {
		this.axisZ = axisZ;
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return String
				.format("GyroscopeData [id=%s, timestamp=%s, axisX=%s, axisY=%s, axisZ=%s]",
						id, timestamp, axisX, axisY, axisZ);
	}
	
	@Override
	public List<String> getParams(){
		return Arrays.asList("id", "timestamp", "axisX", "axisY", "axisZ");
	}
	
	@Override
	public Map<String,String> getValues(){
		
		Map<String,String> values = new HashMap<String,String>();
		values.put("id", String.valueOf(id));
		values.put("timestamp", String.valueOf(timestamp));
		values.put("axisX", String.valueOf(axisX));
		values.put("axisY", String.valueOf(axisY));
		values.put("axisZ", String.valueOf(axisZ));
		return values;
	}
}


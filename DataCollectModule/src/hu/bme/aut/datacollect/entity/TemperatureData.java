package hu.bme.aut.datacollect.entity;

import hu.bme.aut.datacollect.db.DaoBase;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="temperatures", daoClass=DaoBase.class)
public class TemperatureData {

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
	
	
}

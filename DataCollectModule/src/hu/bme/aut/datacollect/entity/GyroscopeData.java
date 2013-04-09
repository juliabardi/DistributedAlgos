package hu.bme.aut.datacollect.entity;

import hu.bme.aut.datacollect.db.DaoBase;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="gyroscope", daoClass=DaoBase.class)
public class GyroscopeData {

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
	
}


package hu.bme.aut.datacollect.entity;

import hu.bme.aut.datacollect.db.DaoBase;

import java.util.Arrays;
import java.util.List;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName="accelerations", daoClass=DaoBase.class)
public class AccelerationData extends IData {

	@DatabaseField(generatedId=true)
	private int id;
	@DatabaseField(canBeNull=false)
	private long timestamp;
	@DatabaseField(canBeNull=false)
	private float accX;
	@DatabaseField(canBeNull=false)
	private float accY;
	@DatabaseField(canBeNull=false)
	private float accZ;
	
	private final float NOISE = 2.0F;
	
	public AccelerationData(){}
	
	public AccelerationData(long timestamp, float accX, float accY,
			float accZ) {
		super();
		this.timestamp = timestamp;
		this.accX = accX;
		this.accY = accY;
		this.accZ = accZ;
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

	public float getAccX() {
		return accX;
	}

	public void setAccX(float accX) {
		this.accX = accX;
	}

	public float getAccY() {
		return accY;
	}

	public void setAccY(float accY) {
		this.accY = accY;
	}

	public float getAccZ() {
		return accZ;
	}

	public void setAccZ(float accZ) {
		this.accZ = accZ;
	}

	@Override
	public String toString() {
		return String
				.format("AccelerationData [id=%s, timestamp=%s, accX=%s, accY=%s, accZ=%s]",
						id, timestamp, accX, accY, accZ);
	}
	
	public boolean equalsWithNoise(AccelerationData acc){
		if (Math.abs(accX - acc.accX) < NOISE
				&& Math.abs(accY- acc.accY) < NOISE
				&& Math.abs(accZ - acc.accZ) < NOISE) {
			return true;
		}
		return false;
	}
	
	@Override
	public List<String> getParams(){
		return Arrays.asList("id", "timestamp", "accX", "accY", "accZ");
	}
	
	@Override
	public List<String> getValues(){
		return Arrays.asList(String.valueOf(id), String.valueOf(timestamp), 
				String.valueOf(accX), String.valueOf(accY), String.valueOf(accY));
	}
	
}

package hu.bme.aut.datacollect.entity;

import hu.bme.aut.datacollect.db.DaoBase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	public Map<String,String> getValues(){
		
		Map<String,String> values = new HashMap<String,String>();
		values.put("id", String.valueOf(id));
		values.put("timestamp", String.valueOf(timestamp));
		values.put("accX", String.valueOf(accX));
		values.put("accY", String.valueOf(accY));
		values.put("accZ", String.valueOf(accZ));
		return values;
	}
	
}

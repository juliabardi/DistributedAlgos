package hu.bme.aut.datacollect.entity;

import hu.bme.aut.datacollect.db.AccelerationDao;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName="accelerations", daoClass=AccelerationDao.class)
public class AccelerationData {

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
}

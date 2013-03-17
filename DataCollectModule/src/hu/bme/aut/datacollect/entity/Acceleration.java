package hu.bme.aut.datacollect.entity;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

public class Acceleration {

	private int id;
	private long timestamp;
	private float accX;
	private float accY;
	private float accZ;
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public static List<Acceleration> makeAcceleration(Cursor cursor){
		
		List<Acceleration> resultset = new ArrayList<Acceleration>();
		while (cursor.moveToNext()){
			Acceleration acc = new Acceleration();
			acc.id = cursor.getInt(0);
			acc.timestamp = cursor.getLong(1);
			acc.accX = cursor.getFloat(2);
			acc.accY = cursor.getFloat(3);
			acc.accZ = cursor.getFloat(4);
			resultset.add(acc);
		}		
		return resultset;
	}

	@Override
	public String toString() {
		return String
				.format("Acceleration [id=%s, timestamp=%s, accX=%s, accY=%s, accZ=%s]",
						id, timestamp, accX, accY, accZ);
	}
}
